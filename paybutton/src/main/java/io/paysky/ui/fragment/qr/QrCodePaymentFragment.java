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

import io.paysky.data.model.PaymentData;
import io.paysky.data.model.ReceiptData;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.fragment.manualpayment.ManualPaymentFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.util.AppCache;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.ConvertQrCodToBitmapTask;
import io.paysky.util.ToastUtils;


public class QrCodePaymentFragment extends BaseFragment implements View.OnClickListener {


    //Variables.
    private String testTerminalId = "88888887";
    private boolean qrGenerated, paymentDone;
    private String generatedQrCode;
    private int transactionId;
    //GUI.
    private ImageView qrImageView;
    private ProgressDialog progressDialog;
    private LinearLayout smsPaymentLayout;
    private EditText mobileNumberEditText;
    private View manualPaymentLayout;
    private Button sendOtpButton;
    private LinearLayout requestPaymentLayout;
    private Button requestPaymentButton;
    private LinearLayout orLayout;
    //Objects.
    private Handler checkTransactionHandler = new Handler();
    private Runnable checkPaymentRunnable;
    private ActivityHelper activityHelper;
    private QrPaymentManager paymentManager;
    private PaymentData paymentData;


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
        if (arguments != null) {
            paymentData = arguments.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
        }
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
                paymentManager.checkPaymentApproval(paymentData.merchantId, testTerminalId, transactionId, paymentData.amount);
            }
        };

        // this is terminal id
        paymentManager.generateQrCode(paymentData.amount, paymentData.merchantId, testTerminalId);
    }

    private void showViewsBasedOnUserPrefs() {
        if (paymentData.enableManual || paymentData.enableMagnetic) {
            manualPaymentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
                    activityHelper.replaceFragmentAndRemoveOldFragment(ManualPaymentFragment.class, bundle);
                }
            });
        } else {
            manualPaymentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (qrGenerated && !paymentDone) {
            checkTransactionHandler.postDelayed(checkPaymentRunnable, 5000);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (checkPaymentRunnable != null && qrGenerated && !paymentDone) {
            checkTransactionHandler.removeCallbacks(checkPaymentRunnable);
        }
    }

    private void showQrCode() {
        new ConvertQrCodToBitmapTask(qrImageView).execute(generatedQrCode);
    }


    private void initView(View view) {
        // find views.
        manualPaymentLayout = view.findViewById(R.id.manual_payment_layout);
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
            hideQrImageView();
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
        paymentManager.requestToPay(generatedQrCode, paymentData.merchantId, testTerminalId, transactionId, mobileNumber);
    }


    private void hideQrImageView() {
        qrImageView.setVisibility(View.GONE);
    }

    private void showSmsPaymentLayout() {
        smsPaymentLayout.setVisibility(View.VISIBLE);
    }

    public void setPaymentApproved(String externalTransactionId, String paidAmount) {
        paymentDone = true;
        checkTransactionHandler.removeCallbacks(checkPaymentRunnable);
        PaymentTransaction transaction = (PaymentTransaction) getActivity();
        transaction.setSuccessTransactionId(transactionId + "", "SUCCESS", "");
        Bundle bundle = new Bundle();
        ReceiptData receiptData = new ReceiptData();
        receiptData.terminalId = testTerminalId;
        receiptData.merchantId = paymentData.merchantId;
        receiptData.receiptNumber = externalTransactionId;
        receiptData.amount = paidAmount;
        receiptData.channelName = AppConstant.TransactionChannelName.TAHWEEL;
        receiptData.merchantName = AppCache.getMerchantData(getActivity()).merchantName;
        bundle.putParcelable(AppConstant.BundleKeys.RECEIPT, receiptData);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }

    public void listenToPaymentApproval() {
        checkTransactionHandler.postDelayed(checkPaymentRunnable, 5000);
    }

    public void setGenerateQrSuccess(String qrCode, int transactionId) {
        qrGenerated = true;
        this.generatedQrCode = qrCode;
        this.transactionId = transactionId;
        showQrCode();
        checkTransactionHandler.postDelayed(checkPaymentRunnable, 5000); // listen to payment approval.
    }

    public void showToastMessage(@StringRes int text) {
        ToastUtils.showToast(getActivity(), text);
    }
}
