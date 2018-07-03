package io.paysky.ui.fragment.paymentsuccess;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.paybutton.R;

import java.util.Locale;

import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.BaseFragment;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;


public class PaymentApprovedFragment extends BaseFragment implements View.OnClickListener {

    private String transactionId;
    private String receiverMail;
    private String terminalId;
    private String merchantId;
    private String referenceNumber;
    //Objects.
    private ActivityHelper activityHelper;
    private PaymentApprovedManager paymentApprovedManager;
    //GUI.
    private TableRow closePrintLayout;
    private EditText emailEditText;
    private ProgressDialog progressDialog;
    private LinearLayout sendEmailLayout, sendEmailNotificationLayout, sendEmailSuccessLayout;
    private TextView mailSentTextView;
    //Constants,
    private final int RECEIPT = 1, EMAIL = 2;
    private String transactionChannel;

    public PaymentApprovedFragment() {
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
        paymentApprovedManager = new PaymentApprovedManager(this);
    }

    private void extractBundle() {
        Bundle bundle = getArguments();
        String authNumber = bundle.getString(AppConstant.BundleKeys.AUTH_NUMBER);
        transactionId = bundle.getString(AppConstant.BundleKeys.TRANSACTION_ID);
        receiverMail = bundle.getString(AppConstant.BundleKeys.RECEIVER_MAIL, null);
        merchantId = bundle.getString(AppConstant.BundleKeys.MERCHANT_ID);
        terminalId = bundle.getString(AppConstant.BundleKeys.TERMINAL_ID);
        referenceNumber = bundle.getString(AppConstant.BundleKeys.REFERENCE_NUMBER);
        transactionChannel = bundle.getString(AppConstant.BundleKeys.TRANSACTION_CHANNEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_approved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        activityHelper.setHeaderIcon(R.drawable.ic_close);
        activityHelper.setHeaderIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private void initView(View view) {
        // find views.
        TextView approvedTextView = view.findViewById(R.id.approved_textView);
        ((BaseActivity) getActivity()).showHtmlText(approvedTextView, R.string.transaction_success);
        TextView authNumberTextView = view.findViewById(R.id.auth_number_textView);
        authNumberTextView.setText(getString(R.string.auth_number) + " #" + referenceNumber);
        TextView trxIdTextView = view.findViewById(R.id.trx_id_textView);
        trxIdTextView.setText(getString(R.string.trx_id) + " #" + transactionId);
        closePrintLayout = view.findViewById(R.id.close_print_layout);
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        Button sendReceiptButton = view.findViewById(R.id.send_receipt_button);
        sendReceiptButton.setOnClickListener(this);
        emailEditText = view.findViewById(R.id.email_editText);
        Button sendEmailButton = view.findViewById(R.id.send_email_button);
        sendEmailButton.setOnClickListener(this);
        sendEmailLayout = view.findViewById(R.id.send_email_layout);
        sendEmailNotificationLayout = view.findViewById(R.id.send_email_notification);
        sendEmailSuccessLayout = view.findViewById(R.id.sent_email_success_layout);
        mailSentTextView = view.findViewById(R.id.mail_sent_textView);
        Button closeButton2 = view.findViewById(R.id.close_view_button);
        closeButton2.setOnClickListener(this);
        if (receiverMail != null) {
            hideSendNotificationEmailLayout();
            hideSendEmailLayout();
        } else {
            hidePrintReceiptLayout();
            showSendNotificationEmailLayout();
            showSendEmailLayout();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.close_button || i == R.id.close_view_button) {
            getActivity().finish();
        } else if (i == R.id.send_email_button) {
            sendEmailButtonClick();
        } else if (i == R.id.send_receipt_button) {
            sendReceiptButtonClick();
        }
    }

    private void sendEmailButtonClick() {
        String email = getText(emailEditText);
        if (!AppUtils.validEmail(email)) {
            emailEditText.setError(getString(R.string.invalid_mail));
            return;
        }
        paymentApprovedManager.sendEmail(email, terminalId, merchantId, referenceNumber, transactionChannel, transactionId, RECEIPT);
    }

    private void sendReceiptButtonClick() {
        // check internet.
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


    void hideSendNotificationEmailLayout() {
        sendEmailNotificationLayout.setVisibility(View.GONE);
    }

    void hideSendEmailLayout() {
        sendEmailLayout.setVisibility(View.GONE);
    }


    private void showSendNotificationEmailLayout() {
        sendEmailNotificationLayout.setVisibility(View.VISIBLE);
    }

    private void showSendEmailLayout() {
        sendEmailLayout.setVisibility(View.VISIBLE);
    }

    void showSentEmailSuccessLayout() {
        sendEmailSuccessLayout.setVisibility(View.VISIBLE);
    }

    private void hidePrintReceiptLayout() {
        closePrintLayout.setVisibility(View.GONE);
    }

    public void setSendEmailSuccess(String email, int operationType) {
        String sendMail = getString(R.string.mail_sent_to) + " " + email;
        if (operationType == EMAIL) {
            hideSendNotificationEmailLayout();
            hideSendEmailLayout();
            showSentEmailSuccessLayout();
            // String emailSent = String.format(Locale.US, , email);
            ((BaseActivity) getActivity()).showHtmlText(mailSentTextView, sendMail);
        } else {
            ((BaseActivity) getActivity()).showHtmlText(mailSentTextView, sendMail);
        }

    }
}
