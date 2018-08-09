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
import io.paysky.data.model.PaymentData;
import io.paysky.data.model.response.MerchantDataResponse;
import io.paysky.data.network.ApiLinks;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.dialog.DialogButtonClick;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.magnetic.MagneticPaymentFragment;
import io.paysky.ui.fragment.manualpayment.ManualPaymentFragment;
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


    private final boolean IS_DEBUG_APP = false;

    //GUI.
    private ImageView headerBackImage;
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
    private String defaultPayment;


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
        } else if (enableMagnetic && !AppUtils.isPaymentMachine(PayActivity.this)) {
            enableMagnetic = false;
            //  showDeviceNotSupportMagneticPaymentDialog();
        }
        // check default payment method.
        defaultPayment = extras.getString(AppConstant.BundleKeys.DEFAULT_PAYMENT, null);
        if (defaultPayment == null) {
            // not select default payment , make one of them as default.
            if (enableManual) {
                defaultPayment = PaymentData.DefaultPayment.MANUAL.name();
            } else if (enableMagnetic) {
                defaultPayment = PaymentData.DefaultPayment.MAGNETIC.name();
            } else {
                defaultPayment = PaymentData.DefaultPayment.QR_CODE.name();
            }
        }
        if (defaultPayment.equals(PaymentData.DefaultPayment.MANUAL.name())) {
            showManualPaymentFragment();
        } else if (defaultPayment.equals(PaymentData.DefaultPayment.MAGNETIC.name())) {
            if (AppUtils.isPaymentMachine(PayActivity.this)) {
                showMagneticReaderFragment();
            } else {
                showDeviceNotSupportMagneticPaymentDialog();
            }
        } else {
            showQrReaderFragment();
        }
    }

    private void showQrReaderFragment() {
        Bundle bundle = createBundle();
        replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
    }

    private void showMagneticReaderFragment() {
        Bundle bundle = createBundle();
        // show magnetic fragment.
        replaceFragmentAndRemoveOldFragment(MagneticPaymentFragment.class, bundle);
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        PaymentData paymentData = new PaymentData();
        paymentData.enableManual = enableManual;
        paymentData.enableMagnetic = enableMagnetic;
        paymentData.enableQr = enableQr;
        paymentData.defaultPayment = defaultPayment;
        paymentData.receiverMail = receiverMail;
        paymentData.terminalId = terminalId + "";
        paymentData.merchantId = merchantId + "";
        paymentData.amount = payAmount;
        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
        return bundle;
    }

    private void showDeviceNotSupportMagneticPaymentDialog() {
        InfoDialog infoDialog = new InfoDialog(this).setDialogText(R.string.device_not_support_magnetic)
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

    private void showManualPaymentFragment() {
        Bundle bundle = createBundle();
        // show manual payment fragment.
        replaceFragmentAndRemoveOldFragment(ManualPaymentFragment.class, bundle);
    }

    private void checkMerchantDataExistence() {
        merchantData = AppCache.getMerchantData(this);
        if (merchantData == null) {
            // get merchant Data from server.
            payActivityManager.getMerchantData(merchantId, terminalId);
        } else {
            enableQr = merchantData.isTahweel;
            enableMagnetic = merchantData.isCard;
            enableManual = merchantData.isCard;
            // show merchant data in views.
            showMerchantData();
            // check if config data exists or not.
            checkTerminalConfigExistence();
            showUserNeededPaymentMethod();
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
        if (extras == null) {
            throw new IllegalStateException("cannot call our sdk without pass data with bundle");
        }
        merchantId = extras.getLong(AppConstant.BundleKeys.MERCHANT_ID, 0);
        terminalId = extras.getLong(AppConstant.BundleKeys.TERMINAL_ID, 0);
        double payAmount = extras.getDouble(AppConstant.BundleKeys.PAY_AMOUNT);
        DecimalFormat format = new DecimalFormat("0.00");
        this.payAmount = AppUtils.convertToEnglishDigits(format.format(payAmount));
        receiverMail = extras.getString(AppConstant.BundleKeys.RECEIVER_MAIL, null);
        // get payment config params.
        if (IS_DEBUG_APP) {
            enableManual = extras.getBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, false);
            enableMagnetic = extras.getBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, false);
            enableQr = extras.getBoolean(AppConstant.BundleKeys.ENABLE_QR, false);
            ApiLinks.MAIN_LINK = extras.getString(AppConstant.BundleKeys.SERVER_LINK, ApiLinks.MAIN_LINK);
        } else {
          //  ApiLinks.MAIN_LINK = ApiLinks.GRAY_LINK;
            ApiLinks.MAIN_LINK = ApiLinks.MAIN_LINK;
        }
    }

    private void showMerchantData() {
        merchantNameTextView.setText(merchantData.merchantName);
        amountTextView.setText(payAmount);
    }


    //GUI Methods.

    private void initView() {
        headerBackImage = findViewById(R.id.header_back_imageVieww);
        headerBackImage.setOnClickListener(this);
        merchantNameTextView = findViewById(R.id.merchant_name_textView);
        amountTextView = findViewById(R.id.amount_textView);
        TextView languageTextView = findViewById(R.id.language_textView);
        languageTextView.setOnClickListener(this);
        TextView termsTextView = findViewById(R.id.terms_conditions_textView);
        termsTextView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
    }

    private void makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        headerBackImage.setImageResource(icon);
    }

    @Override
    public void setHeaderIconClickListener(View.OnClickListener clickListener) {
        headerBackImage.setOnClickListener(clickListener);
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


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    //Server methods----//
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

    public void loadMerchantDataSuccess(MerchantDataResponse response) {
        merchantData = response;
        enableQr = merchantData.isTahweel;
        enableMagnetic = merchantData.isCard;
        enableManual = merchantData.isCard;
        showMerchantData();
        checkTerminalConfigExistence();
        showUserNeededPaymentMethod();
    }

}
