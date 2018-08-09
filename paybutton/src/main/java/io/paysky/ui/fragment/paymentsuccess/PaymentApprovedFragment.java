package io.paysky.ui.fragment.paymentsuccess;


import android.app.ProgressDialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paybutton.R;

import io.paysky.data.model.ReceiptData;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.BaseFragment;
import io.paysky.util.AppUtils;
import io.paysky.util.PrintReceiptListener;
import io.paysky.util.ReceiptManager;


public class PaymentApprovedFragment extends BaseFragment implements View.OnClickListener, PrintReceiptListener {

    //Objects.
    private ActivityHelper activityHelper;
    private PaymentApprovedManager paymentApprovedManager;
    ReceiptManager receiptManager;
    private ReceiptData transactionData;
    private BaseActivity baseActivity;
    //GUI.
    private EditText emailEditText;
    private ProgressDialog progressDialog;
    private LinearLayout sendEmailLayout, sendEmailNotificationLayout, sendEmailSuccessLayout;
    private TextView mailSentTextView;
    private Button printReceipt;
    //Variables.
    private boolean printMerchantCopy = true;

    public PaymentApprovedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHelper = (ActivityHelper) getActivity();
        baseActivity = (BaseActivity) getActivity();
        extractBundle();
        paymentApprovedManager = new PaymentApprovedManager(this);
    }

    private void extractBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            transactionData = bundle.getParcelable("receipt");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(baseActivity, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                baseActivity.finish();
            }
        });
        receiptManager = new ReceiptManager(view, transactionData, this);
    }

    private void initView(View view) {
        // find views.
        TextView approvedTextView = view.findViewById(R.id.approved_textView);
        baseActivity.showHtmlText(approvedTextView, R.string.transaction_success);
        TextView authNumberTextView = view.findViewById(R.id.auth_number_textView);
        authNumberTextView.setText(getString(R.string.auth_number) + " #" + transactionData.authNumber);
        TextView trxIdTextView = view.findViewById(R.id.trx_id_textView);
        trxIdTextView.setText(getString(R.string.trx_id) + " #" + transactionData.rrn);
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        printReceipt = view.findViewById(R.id.print_receipt_button);
        if (AppUtils.isPaymentMachine(getContext())) {
            printReceipt.setVisibility(View.VISIBLE);
            printReceipt.setText(R.string.merchant_copy);
            printReceipt.setOnClickListener(this);
        } else {
            printReceipt.setVisibility(View.GONE);
        }
        emailEditText = view.findViewById(R.id.email_editText);
        Button sendEmailButton = view.findViewById(R.id.send_email_button);
        sendEmailButton.setOnClickListener(this);
        sendEmailLayout = view.findViewById(R.id.send_email_layout);
        sendEmailNotificationLayout = view.findViewById(R.id.send_email_notification);
        sendEmailSuccessLayout = view.findViewById(R.id.sent_email_success_layout);
        mailSentTextView = view.findViewById(R.id.mail_sent_textView);
        showSendNotificationEmailLayout();
        showSendEmailLayout();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.close_button) {
            baseActivity.finish();
        } else if (i == R.id.send_email_button) {
            AppUtils.hideKeyboard(view);
            sendEmailButtonClick();
        } else if (i == R.id.print_receipt_button) {
            printReceiptButtonClick();
        }
    }

    public void printReceiptButtonClick() {
        if (printMerchantCopy) {
            receiptManager.printMerchantReceipt();
        } else {
            receiptManager.printCustomerReceipt();
        }
    }

    private void sendEmailButtonClick() {
        String email = getText(emailEditText);
        if (!AppUtils.validEmail(email)) {
            emailEditText.setError(getString(R.string.invalid_mail));
            return;
        }
        int RECEIPT = 1;
        paymentApprovedManager.sendEmail(email, transactionData.terminalId, transactionData.merchantId,
                transactionData.refNumber, transactionData.channelName, transactionData.rrn,
                RECEIPT);
    }


    void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = AppUtils.createProgressDialog(baseActivity, R.string.please_wait);
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


    public void setSendEmailSuccess(String email, int operationType) {
        String sendMail = getString(R.string.mail_sent_to) + " " + email;
        int EMAIL = 2;
        if (operationType == EMAIL) {
            hideSendNotificationEmailLayout();
            hideSendEmailLayout();
            showSentEmailSuccessLayout();
            // String emailSent = String.format(Locale.US, , email);
            baseActivity.showHtmlText(mailSentTextView, sendMail);
        } else {
            baseActivity.showHtmlText(mailSentTextView, sendMail);
        }

    }

    @Override
    public void onPrintSuccess() {
        if (printMerchantCopy) {
            printMerchantCopy = false;
            printReceipt.setText(R.string.customer_copy);
        }
    }

    @Override
    public void onPrintFail() {
        Toast.makeText(getContext(), "Printer failure", Toast.LENGTH_SHORT).show();
    }
}
