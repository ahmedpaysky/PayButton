package io.paysky.ui.activity.pay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paybutton.R;

import java.text.DecimalFormat;

import io.paysky.data.event.PaymentStatusEvent;
import io.paysky.data.model.response.MerchantDataResponse;
import io.paysky.data.network.ApiLinks;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.dialog.DialogButtonClick;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.magnetic.MagneticPaymentFragment;
import io.paysky.ui.fragment.manualpayment.CardManualPaymentFragment;
import io.paysky.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.util.AppCache;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.DialogUtils;
import io.paysky.util.LocaleHelper;
import io.paysky.util.PaymentObservable;
import io.paysky.util.PrefsUtils;

public class PayActivity extends BaseActivity
        implements ActivityHelper, PaymentTransaction, View.OnClickListener {

    //GUI.
    private ImageView headerBackImageVieww;
    private TextView merchantNameTextView;
    private TextView amountTextView;
    private ProgressDialog progressDialog;
    //Objects,
    private MerchantDataResponse merchantData;
    private Throwable failTransactionException;
    private PayActivityManager payActivityManager;
    private Bundle extras;
    //Variables.
    private long merchantId;
    private long terminalId;
    private String payAmount;
    private String receiverMail;
    private String referenceNumber, responseCode, authorizationCode;
    private boolean enableManual, enableMagnetic, enableQr;
    private int defaultPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefsUtils.initialize(this);
        payActivityManager = new PayActivityManager(this);
        makeActivityFullScreen();
        setContentView(R.layout.activity_pay);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
        extractDataFromBundle();
        initView();
        checkMerchantDataExistence();
        showUserNeededPaymentMethod();
    }

    private void showUserNeededPaymentMethod() {
        if (!enableManual && !enableMagnetic && !enableQr) {
            InfoDialog infoDialog = new InfoDialog(this).setDialogText(R.string.no_payment_selected)
                    .setDialogTitle(R.string.error)
                    .setButtonText(R.string.ok).setButtonClickListener(new DialogButtonClick() {
                        @Override
                        public void onButtonClick(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            infoDialog.setCancelable(false);
            infoDialog.showDialog();
            return;
        } else if (enableMagnetic) {
            if (!AppUtils.isPaymentMachine(PayActivity.this)) {
                enableMagnetic = false;
                showDeviceNotSupportMagneticPaymentDialog();
                return;
            }
        }
        // check default payment method.
        defaultPayment = extras.getInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, -1);
        if (defaultPayment == -1) {
            // not select default payment , make one of them as default.
            if (enableManual) {
                defaultPayment = AppConstant.PaymentMethods.MANUAL;
            } else if (enableMagnetic) {
                defaultPayment = AppConstant.PaymentMethods.MAGNETIC;
            } else {
                defaultPayment = AppConstant.PaymentMethods.QR_READER;
            }
        }
        if (defaultPayment == AppConstant.PaymentMethods.MANUAL) {
            showPayByManualCardFragment();
        } else if (defaultPayment == AppConstant.PaymentMethods.MAGNETIC) {
            if (AppUtils.isPaymentMachine(PayActivity.this)) {
                showMagneticReaderFragment();
            } else {
                showDeviceNotSupportMagneticPaymentDialog();
            }
        } else {
            showQrReaderFragment();
        }
    }

    private void showDeviceNotSupportMagneticPaymentDialog() {
        InfoDialog infoDialog = new InfoDialog(this).setDialogText(R.string.device_not_supprt_magnetic)
                .setDialogTitle(R.string.error)
                .setButtonText(R.string.ok).setButtonClickListener(new DialogButtonClick() {
                    @Override
                    public void onButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
        infoDialog.setCancelable(false);
        infoDialog.showDialog();
    }

    private void showQrReaderFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId + "");
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId + "");
        bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount + "");
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQr);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagnetic);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManual);
        bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
        // show Qr Fragment.
        replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
    }

    private void showMagneticReaderFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId + "");
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId + "");
        bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount + "");
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQr);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagnetic);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManual);
        bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
        // show magnetic fragment.
        replaceFragmentAndRemoveOldFragment(MagneticPaymentFragment.class, bundle);
    }


    private void showPayByManualCardFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId + "");
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId + "");
        bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount + "");
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQr);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagnetic);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManual);
        bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
        // show manual payment fragment.
        replaceFragmentAndRemoveOldFragment(CardManualPaymentFragment.class, bundle);
    }

    private void makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        headerBackImageVieww = findViewById(R.id.header_back_imageVieww);
        headerBackImageVieww.setOnClickListener(this);
        merchantNameTextView = findViewById(R.id.merchant_name_textView);
        amountTextView = findViewById(R.id.amount_textView);
        TextView languageTextView = findViewById(R.id.language_textView);
        languageTextView.setOnClickListener(this);
        TextView termsTextView = findViewById(R.id.terms_conditions_textView);
        termsTextView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
    }


    private void checkMerchantDataExistence() {
        merchantData = AppCache.getMerchantData(this);
        if (merchantData == null) {
            // get merchant Data from server.
            payActivityManager.getMerchantData(merchantId, terminalId);
        } else {
            // show merchant data in views.
            showMerchantData();
            // check if config data exists or not.
            checkTerminalConfigExistence();
        }
    }

    private void checkTerminalConfigExistence() {
        if (AppCache.getTerminalConfig(this).isEmpty()) {
            // get terminal config from server.
            payActivityManager.getTerminalConfig(terminalId);
        }
    }


    private void extractDataFromBundle() {
        extras = getIntent().getExtras();
        merchantId = extras.getLong(AppConstant.BundleKeys.MERCHANT_ID, 0);
        terminalId = extras.getLong(AppConstant.BundleKeys.TERMINAL_ID, 0);
        double payAmount = extras.getDouble(AppConstant.BundleKeys.PAY_AMOUNT);
        DecimalFormat format = new DecimalFormat("0.00");
        this.payAmount = AppUtils.convertToEnglishDigits(format.format(payAmount));
        receiverMail = extras.getString(AppConstant.BundleKeys.RECEIVER_MAIL, null);
        // get payment config params.
        enableManual = extras.getBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, false);
        enableMagnetic = extras.getBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, false);
        enableQr = extras.getBoolean(AppConstant.BundleKeys.ENABLE_QR, false);
        ApiLinks.MAIN_LINK = extras.getString(AppConstant.BundleKeys.SERVER_LINK, ApiLinks.MAIN_LINK);
    }

    private void showMerchantData() {
        merchantNameTextView.setText(merchantData.merchantName);
        amountTextView.setText(payAmount);
    }


    public void showProgress() {
        progressDialog = AppUtils.createProgressDialog(this, R.string.please_wait);
        progressDialog.show();
    }

    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void setHeaderIcon(int icon) {
        headerBackImageVieww.setImageResource(icon);
    }

    @Override
    public void setHeaderIconClickListener(View.OnClickListener clickListener) {
        headerBackImageVieww.setOnClickListener(clickListener);
    }

    @Override
    public void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass) {
        replaceFragment(fragmentClass, null, false);
    }

    @Override
    public void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass) {
        replaceFragment(fragmentClass, null, true);
    }

    @Override
    public void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, false);
    }

    @Override
    public void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, true);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.header_back_imageVieww) {
            onBackPressed();
        } else if (i == R.id.language_textView) {
            // change app language.
            String appLanguage = LocaleHelper.getLocale(this);
            if (appLanguage.equals("en")) {
                LocaleHelper.setLocale(this, "ar");
                LocaleHelper.saveLocale(this, "ar");
            } else {
                LocaleHelper.setLocale(this, "en");
                LocaleHelper.saveLocale(this, "en");
            }
            //   AppUtils.openActivity(this, PayActivity.class, extras, true);
            recreate();
        } else if (i == R.id.terms_conditions_textView) {
            // show terms dialog.
            DialogUtils.showTermsAndConditionsDialog(this);
        }
    }


    @Override
    public void setSuccessTransactionId(String referenceNumber, String responseCode, String authorizationCode) {
        this.referenceNumber = referenceNumber;
        this.responseCode = responseCode;
        this.authorizationCode = authorizationCode;
    }

    @Override
    public void setFailTransactionError(Throwable error) {
        failTransactionException = error;
    }

    @Override
    protected void onDestroy() {
        PaymentStatusEvent paymentStatusEvent = new PaymentStatusEvent();
        if (referenceNumber == null) {
            if (failTransactionException != null) {
                paymentStatusEvent.failException = failTransactionException;
            } else {
                paymentStatusEvent.failException = new Exception("Payment not completed");
            }

        } else {
            paymentStatusEvent.referenceNumber = referenceNumber;
            paymentStatusEvent.responseCode = responseCode;
            paymentStatusEvent.authorizationCode = authorizationCode;
        }

        PaymentObservable.sendPaymentStatus(paymentStatusEvent);
        super.onDestroy();
    }


    public void loadMerchantDataSuccess(MerchantDataResponse response) {
        merchantData = response;
        showMerchantData();
        checkTerminalConfigExistence();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
