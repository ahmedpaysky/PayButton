package io.paysky.ui.activity.payment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.data.event.PaymentStatusEvent;
import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.SuccessfulWalletTransaction;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.PaymentTransactionListener;
import io.paysky.ui.dialog.DialogAgreeButtonClick;
import io.paysky.ui.dialog.DialogCancelButtonClick;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.manualpayment.ManualPaymentFragment;
import io.paysky.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.util.DialogUtils;
import io.paysky.util.LocaleHelper;
import io.paysky.util.PaymentObservable;
import io.paysky.util.PrefsUtils;

public class PaymentActivity extends BaseActivity implements PayActivityView, PaymentTransactionListener, View.OnClickListener {

    //GUI.
    private ImageView headerBackImage;
    private LinearLayout cardPaymentLayout;
    private LinearLayout qrPaymentLayout;
    private TextView currencyTextView;
    private TextView amountTextView;
    private TextView merchantNameTextView;
    //Objects,
    private Throwable failTransactionException;
    private SuccessfulWalletTransaction walletTransaction;
    private SuccessfulCardTransaction cardTransaction;
    private PayActivityPresenter presenter;
    public static Bitmap qrBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefsUtils.initialize(this);
        presenter = new PayActivityPresenter(getIntent());
        presenter.attachView(this);
        presenter.activityCreated();
        // show activity layout.
        setContentView(R.layout.activity_pay);
        initView();
        presenter.activitySetView();
        presenter.loadMerchantInfo();
    }


    @Override
    public void makeActivityFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void hideActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }


    //GUI Methods.
    private void initView() {
        headerBackImage = findViewById(R.id.header_back_imageView);
        headerBackImage.setOnClickListener(this);
        merchantNameTextView = findViewById(R.id.pb_merchant_name_textView);
        currencyTextView = findViewById(R.id.currency_textView);
        amountTextView = findViewById(R.id.amount_textView);
        TextView languageTextView = findViewById(R.id.language_textView);
        languageTextView.setOnClickListener(this);
        TextView termsTextView = findViewById(R.id.terms_conditions_textView);
        termsTextView.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
        cardPaymentLayout = findViewById(R.id.card_payment_layout);
        qrPaymentLayout = findViewById(R.id.qr_payment_layout);
        cardPaymentLayout.setOnClickListener(this);
        qrPaymentLayout.setOnClickListener(this);
    }

    @Override
    public void showMerchantInfo(String merchantName) {
        merchantNameTextView.setText(merchantName);
    }

    @Override
    public void showPaidAmount(String amount, String currency) {
        amountTextView.setText(amount);
        currencyTextView.setText(currency);
    }

    @Override
    public void showPaymentBasedOnPaymentOptions(int paymentOptions) {
        switch (paymentOptions) {
            case 0:
                // card payment enabled.
                cardPaymentLayout.setVisibility(View.VISIBLE);
                cardPaymentLayout.setOnClickListener(this);
                showCardPaymentFragment(presenter.getBundle());
                break;
            case 1:
                // wallet payment enabled.
                qrPaymentLayout.setVisibility(View.VISIBLE);
                qrPaymentLayout.setOnClickListener(this);
                showQrPaymentFragment(presenter.getBundle());
            case 2:
                // both payments enabled.
                cardPaymentLayout.setVisibility(View.VISIBLE);
                cardPaymentLayout.setOnClickListener(this);
                qrPaymentLayout.setVisibility(View.VISIBLE);
                qrPaymentLayout.setOnClickListener(this);
                showCardPaymentFragment(presenter.getBundle());
                break;
        }
    }

    @Override
    public void showFailedLoadMerchantDataDialog() {
        InfoDialog infoDialog = new InfoDialog(this);
        infoDialog.setDialogText(R.string.failed_load_merchant_data)
                .setDialogTitle(R.string.error)
                .showAgreeButton(new DialogAgreeButtonClick() {
                    @Override
                    public void onAgreeDialogButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        presenter.loadMerchantInfo();
                    }
                }).showCancelButton(new DialogCancelButtonClick() {
            @Override
            public void onCancelDialogButtonClick(Dialog dialog) {
                dialog.dismiss();
                finish();
            }
        }).setCancel(false).show();
    }

    @Override
    public void showCardPaymentFragment(Bundle bundle) {
        replaceFragmentAndRemoveOldFragment(ManualPaymentFragment.class, bundle);
    }

    @Override
    public void showQrPaymentFragment(Bundle bundle) {
        replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
    }


    public void setHeaderIcon(int icon) {
        headerBackImage.setImageResource(icon);
    }


    public void setHeaderIconClickListener(View.OnClickListener clickListener) {
        headerBackImage.setOnClickListener(clickListener);
    }


    public void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, false);
    }


    public void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass, Bundle bundle) {
        replaceFragment(fragmentClass, bundle, true);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.header_back_imageView) {
            onBackPressed();
        } else if (i == R.id.language_textView) {
            // change app language.
            String appLanguage = LocaleHelper.getLocale(this);
            if (appLanguage.equals("en")) {
                LocaleHelper.saveLocale(this, "ar");
                LocaleHelper.setLocale(this, "ar");
            } else {
                LocaleHelper.saveLocale(this, "en");
                LocaleHelper.setLocale(this, "en");
            }
            recreate();
        } else if (i == R.id.terms_conditions_textView) {
            // show terms dialog.
            DialogUtils.showTermsAndConditionsDialog(this);
        } else if (i == R.id.card_payment_layout) {
            changePaymentOptionButton(1);
            showCardPaymentFragment(presenter.getBundle());
        } else if (i == R.id.qr_payment_layout) {
            changePaymentOptionButton(2);
            showQrPaymentFragment(presenter.getBundle());
        }
    }

    private void changePaymentOptionButton(int type) {
        if (type == 1) {
            // manual payment.
            cardPaymentLayout.setBackgroundResource(R.drawable.payment_option_selected);
            TextView manualTextView = cardPaymentLayout.findViewById(R.id.card_payment_textView);
            manualTextView.setTextColor(getResources().getColor(android.R.color.white));
            if (LocaleHelper.getLocale(this).equals("ar")) {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_white,0, 0, 0);
            } else {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_card_white, 0);
            }
            qrPaymentLayout.setBackgroundResource(R.drawable.payment_option_unselected);
            TextView qrTextView = qrPaymentLayout.findViewById(R.id.qr_payment_textView);
            qrTextView.setTextColor(getResources().getColor(R.color.font_gray_color3));
            if (LocaleHelper.getLocale(this).equals("ar")) {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_gray,0, 0, 0);
            } else {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_wallet_gray, 0);
            }
        } else {
            // qr payment.
            cardPaymentLayout.setBackgroundResource(R.drawable.payment_option_unselected);
            TextView manualTextView = cardPaymentLayout.findViewById(R.id.card_payment_textView);
            manualTextView.setTextColor(getResources().getColor(R.color.font_gray_color3));
            if (LocaleHelper.getLocale(this).equals("ar")) {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_card_black, 0,0, 0);
            } else {
                manualTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_card_black, 0);
            }
            qrPaymentLayout.setBackgroundResource(R.drawable.payment_option_selected);
            TextView qrTextView = qrPaymentLayout.findViewById(R.id.qr_payment_textView);
            qrTextView.setTextColor(getResources().getColor(android.R.color.white));
            if (LocaleHelper.getLocale(this).equals("ar")) {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_white,0, 0, 0);
            } else {
                qrTextView.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_wallet_white, 0);
            }
        }
    }


    @Override
    protected void onDestroy() {
        PaymentStatusEvent paymentStatusEvent = new PaymentStatusEvent();
        if (cardTransaction == null && walletTransaction == null) {
            if (failTransactionException != null) {
                paymentStatusEvent.failException = failTransactionException;
            } else {
                paymentStatusEvent.failException = new Exception("Payment not completed");
            }

        } else {
            paymentStatusEvent.cardTransaction = cardTransaction;
            paymentStatusEvent.walletTransaction = walletTransaction;
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


    @Override
    public void successCardTransaction(SuccessfulCardTransaction cardTransaction) {
        this.cardTransaction = cardTransaction;
    }

    @Override
    public void successWalletTransaction(SuccessfulWalletTransaction walletTransaction) {
        this.walletTransaction = walletTransaction;
    }

    @Override
    public void setFailTransactionError(Throwable error) {
        failTransactionException = error;
    }

    public void showManualPayment() {
        cardPaymentLayout.performClick();
    }
}
