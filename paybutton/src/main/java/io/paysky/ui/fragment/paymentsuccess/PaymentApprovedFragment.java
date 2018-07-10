package io.paysky.ui.fragment.paymentsuccess;


import android.app.ProgressDialog;
import android.content.Intent;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paybutton.R;

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
    private String paidAmount;
    //Objects.
    private ActivityHelper activityHelper;
    private PaymentApprovedManager paymentApprovedManager;
    //GUI.
    private EditText emailEditText;
    private ProgressDialog progressDialog;
    private LinearLayout sendEmailLayout, sendEmailNotificationLayout, sendEmailSuccessLayout, receiptLayout;
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
        paidAmount = bundle.getString(AppConstant.BundleKeys.PAY_AMOUNT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.fragment_payment_approved, container, false);


    }

    public void printReceiptButtonClick() {
        // set data to receipt layout, to print it.
        TextView merchantIdTextView = receiptLayout.findViewById(R.id.receipt_mid);
        merchantIdTextView.setText(String.format("MID: %s", merchantId));
        TextView terminalIdTextView = receiptLayout.findViewById(R.id.receipt_tid);
        terminalIdTextView.setText(String.format("TID: %s", terminalId));
        TextView receiptNumberTextView = receiptLayout.findViewById(R.id.receipt_number);
        receiptNumberTextView.setText(String.format("Receipt NO: %s", transactionId));
        TextView amountTextView = receiptLayout.findViewById(R.id.receipt_amount);
        StringBuilder formattedAmount = new StringBuilder();
        for (int i = 0; i < paidAmount.length(); i++) {
            if (i == 2) {
                formattedAmount.append(".");
            }
            formattedAmount.append(paidAmount.charAt(i));
        }
        formattedAmount.append(" EGP");
        amountTextView.setText(String.format("Amount: %s", formattedAmount.toString()));
        TextView transactionTypeTextView = receiptLayout.findViewById(R.id.receipt_transaction_type);
        transactionTypeTextView.setText("Transaction type: SALE");
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        TextView dateTextView = receiptLayout.findViewById(R.id.receipt_date);
        dateTextView.setText(String.format("Date: %d-%d-%d", today.monthDay, today.month, today.year));
        TextView timeTextView = receiptLayout.findViewById(R.id.receipt_time);
        timeTextView.setText(String.format("Time: %d:%d", today.hour, today.minute));
        // get layout image to print.
        receiptLayout.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        receiptLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        receiptLayout.layout(0, 0, receiptLayout.getMeasuredWidth(), receiptLayout.getMeasuredHeight());

        receiptLayout.buildDrawingCache(true);
        Bitmap myBitmap = Bitmap.createBitmap(receiptLayout.getDrawingCache());
        receiptLayout.setDrawingCacheEnabled(false); // clear drawing cache
        // print image.
        PrinterManager printer = new PrinterManager();
        printer.setupPage(384, -1);
        int ret = printer.drawBitmap(myBitmap, 0, 0);
        int retprinter = printer.printPage(0);
        if (retprinter != 0) {
            Toast.makeText(getActivity(), "Printer failure", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent("urovo.prnt.message");
        intent.putExtra("ret", ret);
        getActivity().sendBroadcast(intent);
        printer.prn_paperForWard(15);
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
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        Button printReceipt = view.findViewById(R.id.print_receipt);
        receiptLayout = view.findViewById(R.id.layout);
        if (AppUtils.isPaymentMachine(getContext())) {
            printReceipt.setVisibility(View.VISIBLE);
            printReceipt.setOnClickListener(this);
            receiptLayout.setVisibility(View.INVISIBLE);
        } else {
            printReceipt.setVisibility(View.GONE);
            receiptLayout.setVisibility(View.GONE);
        }
        emailEditText = view.findViewById(R.id.email_editText);
        Button sendEmailButton = view.findViewById(R.id.send_email_button);
        sendEmailButton.setOnClickListener(this);
        sendEmailLayout = view.findViewById(R.id.send_email_layout);
        sendEmailNotificationLayout = view.findViewById(R.id.send_email_notification);
        sendEmailSuccessLayout = view.findViewById(R.id.sent_email_success_layout);
        mailSentTextView = view.findViewById(R.id.mail_sent_textView);
        if (receiverMail != null) {
            hideSendNotificationEmailLayout();
            hideSendEmailLayout();
        } else {
            showSendNotificationEmailLayout();
            showSendEmailLayout();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.close_button) {
            getActivity().finish();
        } else if (i == R.id.send_email_button) {
            sendEmailButtonClick();
        } else if (i == R.id.print_receipt) {
            printReceiptButtonClick();
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
