package io.paysky.ui.fragment.qr;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.paybutton.R;

import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.fragment.magnetic.MagneticPaymentFragment;
import io.paysky.ui.fragment.manualpayment.CardManualPaymentFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.ConvertQrCodToBitmapTask;
import io.paysky.util.ToastUtils;


public class QrCodePaymentFragment extends BaseFragment implements View.OnClickListener {


    //Variables.
    private String terminalId, testTerminalId = "88888887", merchantId, amount;
    private boolean isGenerateQrCode, isPaymentDone;
    private String qrCode;
    private String receiverMail;
    private int transactionId;
    private boolean enableQr, enableMagnetic, enableManual;
    private int defaultPayment;
    //GUI.
    private ImageView qrImageView;
    private ProgressDialog progressDialog;
    private LinearLayout smsPaymentLayout;
    private EditText mobileNumberEditText;
    private Button sendOtpButton;
    private LinearLayout requestPaymentLayout;
    private Button requestPaymentButton;
    private LinearLayout orLayout;
    private LinearLayout magneticLayout;
    //Objects.
    private Handler handler = new Handler();
    private Runnable checkPaymentRunnable;
    private ActivityHelper activityHelper;
    private QrPaymentManager paymentManager;
    private View cardPaymentLayout;

    public QrCodePaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ActivityHelper) {
            activityHelper = (ActivityHelper) getActivity();
        } else {
            throw new IllegalStateException("activity must implement " + ActivityHelper.class.getSimpleName());
        }
        extractBundle();
        paymentManager = new QrPaymentManager(this, getActivity());
    }

    private void extractBundle() {
        Bundle arguments = getArguments();
        terminalId = arguments.getString(AppConstant.BundleKeys.TERMINAL_ID);
        merchantId = arguments.getString(AppConstant.BundleKeys.MERCHANT_ID);
        amount = arguments.getString(AppConstant.BundleKeys.PAY_AMOUNT);
        receiverMail = arguments.getString(AppConstant.BundleKeys.RECEIVER_MAIL, null);
        enableMagnetic = arguments.getBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC);
        enableManual = arguments.getBoolean(AppConstant.BundleKeys.ENABLE_MANUAL);
        enableQr = arguments.getBoolean(AppConstant.BundleKeys.ENABLE_QR);
        defaultPayment = arguments.getInt(AppConstant.BundleKeys.DEFAULT_PAYMENT);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_code_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        showViewsBasedOnUserPrefs();
        activityHelper.setHeaderIcon(R.drawable.ic_close);
        activityHelper.setHeaderIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        checkPaymentRunnable = new Runnable() {
            @Override
            public void run() {
                paymentManager.checkPaymentApproval(merchantId, testTerminalId, transactionId);
            }
        };
        // this is terminal id
        //   terminalId = "88888887";
        paymentManager.generateQrCode(amount, merchantId, testTerminalId);
    }

    private void showViewsBasedOnUserPrefs() {
        if (enableManual) {
            cardPaymentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
                    bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId);
                    bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, amount);
                    bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQr);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagnetic);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManual);
                    bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
                    activityHelper.replaceFragmentAndRemoveOldFragment(CardManualPaymentFragment.class, bundle);
                }
            });
        } else {
            cardPaymentLayout.setVisibility(View.GONE);
        }
        if (enableMagnetic) {
            magneticLayout.setVisibility(View.VISIBLE);
            magneticLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
                    bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId);
                    bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, amount);
                    bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQr);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagnetic);
                    bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManual);
                    bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
                    activityHelper.replaceFragmentAndRemoveOldFragment(MagneticPaymentFragment.class, bundle);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isGenerateQrCode && !isPaymentDone) {
            handler.postDelayed(checkPaymentRunnable, 5000);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (checkPaymentRunnable != null && isGenerateQrCode && !isPaymentDone) {
            handler.removeCallbacks(checkPaymentRunnable);
        }
    }

    private void showQrCode() {
        new ConvertQrCodToBitmapTask(qrImageView).execute(qrCode);
    }


    private void initView(View view) {
        // find views.
        cardPaymentLayout = view.findViewById(R.id.card_manual_payment_layout);
        smsPaymentLayout = view.findViewById(R.id.sms_payment_layout);
        smsPaymentLayout.setOnClickListener(this);
        mobileNumberEditText = view.findViewById(R.id.mobile_number_editText);
        sendOtpButton = view.findViewById(R.id.send_otp_button);
        sendOtpButton.setOnClickListener(this);
        requestPaymentLayout = view.findViewById(R.id.request_payment_layout);
        requestPaymentButton = view.findViewById(R.id.request_payment);
        requestPaymentButton.setOnClickListener(this);
        qrImageView = view.findViewById(R.id.qr_imageView);
        orLayout = view.findViewById(R.id.or_layout);
        magneticLayout = view.findViewById(R.id.magnetic_payment_layout);
    }

    void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = AppUtils.createProgressDialog(getActivity(), R.string.please_wait);
        }
        progressDialog.show();
    }

    void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.equals(sendOtpButton)) {
            sendOtpButtonClick();
        } else if (view.equals(requestPaymentButton)) {
            hideQrImageVieww();
            requestPaymentLayout.setVisibility(View.GONE);
            orLayout.setVisibility(View.GONE);
            showSmsPaymentLayout();
        }
    }

    private void sendOtpButtonClick() {
        String mobileNumber = getText(mobileNumberEditText);
        if (!AppUtils.validPhone(mobileNumber)) {
            mobileNumberEditText.setError(getString(R.string.invalid_mobile_number));
            return;
        }
        // send to server.
        paymentManager.requestToPay(qrCode, merchantId, testTerminalId, transactionId, mobileNumber);
    }


    private void showQrImagView() {
        qrImageView.setVisibility(View.VISIBLE);
    }

    private void hideQrImageVieww() {
        qrImageView.setVisibility(View.GONE);
    }

    private void showSmsPaymentLayout() {
        smsPaymentLayout.setVisibility(View.VISIBLE);
    }

    private void hideSmsPaymentLayout() {
        smsPaymentLayout.setVisibility(View.GONE);
    }

    public void setPaymentApproved(String externalTransactionId) {
        isPaymentDone = true;
        handler.removeCallbacks(checkPaymentRunnable);
        PaymentTransaction transaction = (PaymentTransaction) getActivity();
        transaction.setSuccessTransactionId(transactionId + "", "SUCCESS", "");
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_ID, transactionId + "");
        bundle.putString(AppConstant.BundleKeys.AUTH_NUMBER, " ");
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, testTerminalId);
        bundle.putString(AppConstant.BundleKeys.REFERENCE_NUMBER, externalTransactionId);
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_CHANNEL, AppConstant.TransactionChannelName.TAHWEEL);
        bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, amount);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }

    public void listenToPaymentApproval() {
        handler.postDelayed(checkPaymentRunnable, 5000);
    }

    public void setGenerateQrSuccess(String qrCode, int transactionId) {
        isGenerateQrCode = true;
        this.qrCode = qrCode;
        this.transactionId = transactionId;
        showQrCode();
        handler.postDelayed(checkPaymentRunnable, 5000); // listen to payment approval.
    }

    public void showToastMessage(@StringRes int text) {
        ToastUtils.showToast(getActivity(), text);
    }
}
