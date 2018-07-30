package io.paysky.util;

import android.content.Intent;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.data.model.ReceiptData;

public class ReceiptManager {

    //GUI.
    private View receiptView;
    private TextView merchantNameTextView;
    private TextView receiptDate;
    private TextView receiptTime;
    private TextView receiptMid;
    private TextView receiptTid;
    private TextView receiptNumber;
    private TextView transactionType;
    private TextView cardNumber;
    private TextView cardType;
    private TextView cardHolderName;
    private TextView stan;
    private TextView rrn;
    private TextView authNumber;
    private TextView total;
    private TextView transactionPayType;
    private LinearLayout signLayout;
    private TextView sign;
    private TextView receiptPrintTo;
    //Objects
    private ReceiptData receiptData;
    private PrintReceiptListener printReceiptListener;

    public ReceiptManager(View parentView, ReceiptData receiptData, PrintReceiptListener printReceiptListener) {
        receiptView = parentView.findViewById(R.id.receipt_layout);
        this.receiptData = receiptData;
        this.printReceiptListener = printReceiptListener;
        initView(receiptView);
    }


    private void initView(View receiptView) {
        merchantNameTextView = receiptView.findViewById(R.id.merchant_name_textView);
        receiptDate = receiptView.findViewById(R.id.receipt_date);
        receiptTime = receiptView.findViewById(R.id.receipt_time);
        receiptMid = receiptView.findViewById(R.id.receipt_mid);
        receiptTid = receiptView.findViewById(R.id.receipt_tid);
        receiptNumber = receiptView.findViewById(R.id.receipt_number);
        transactionType = receiptView.findViewById(R.id.transaction_type);
        cardNumber = receiptView.findViewById(R.id.card_number);
        cardType = receiptView.findViewById(R.id.card_type);
        cardHolderName = receiptView.findViewById(R.id.card_holder_name);
        stan = receiptView.findViewById(R.id.stan);
        rrn = receiptView.findViewById(R.id.rrn);
        authNumber = receiptView.findViewById(R.id.auth_number);
        total = receiptView.findViewById(R.id.total);
        transactionPayType = receiptView.findViewById(R.id.transaction_pay_type);
        sign = receiptView.findViewById(R.id.sign);
        receiptPrintTo = receiptView.findViewById(R.id.receipt_print_to);
        signLayout = receiptView.findViewById(R.id.sign_layout);
    }


    public void printMerchantReceipt() {
        setReceiptData();
        printReceipt();
    }


    public void printCustomerReceipt() {
        setReceiptData();
        printReceipt();
    }

    private void setReceiptData() {
        merchantNameTextView.setText(receiptData.merchantName);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        receiptDate.setText(String.format("Date: %d-%d-%d", today.monthDay, today.month, today.year));
        receiptTime.setText(String.format("Time: %d:%d:%d", today.hour, today.minute, today.second));
        receiptMid.setText("MID: " + receiptData.merchantId);
        receiptTid.setText("TID: " + receiptData.terminalId);
        receiptNumber.setText("Receipt #: " + receiptData.receiptNumber);
        transactionType.setText(receiptData.transactionType);
        cardNumber.setText(receiptData.cardNumber);
        cardHolderName.setText(receiptData.cardHolderName);
        if (receiptData.channelName.equals(AppConstant.TransactionChannelName.TAHWEEL)) {
            cardType.setText(AppConstant.TransactionChannelName.TAHWEEL);
            stan.setVisibility(View.GONE);
            rrn.setVisibility(View.GONE);
            authNumber.setVisibility(View.GONE);
            signLayout.setVisibility(View.GONE);
        } else {
            if (receiptData.cardNumber.startsWith("4")) {
                cardType.setText("VISA");
            } else {
                cardType.setText("MASTER");
            }

            stan.setText("STAN: " + receiptData.stan);
            rrn.setText("RRN: " + receiptData.rrn);
            authNumber.setText("Auth No #: " + receiptData.authNumber);
            transactionPayType.setText(receiptData.paymentDoneBy);
            String transactionDoneBy = receiptData.paymentDoneBy;
            if (transactionDoneBy.equals(ReceiptData.PaymentDoneBy.MANUAL) ||
                    transactionDoneBy.equals(ReceiptData.PaymentDoneBy.MAGNETIC)) {
                signLayout.setVisibility(View.VISIBLE);
            } else {
                signLayout.setVisibility(View.GONE);
            }
        }

        total.setText("TOTAL :      " + "EGP " + receiptData.amount);

    }

    private void printReceipt() {
        // get layout image to print.
        receiptView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                receiptView.setDrawingCacheEnabled(true);
                receiptView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                receiptView.layout(0, 0, receiptView.getMeasuredWidth(), receiptView.getMeasuredHeight());
                receiptView.buildDrawingCache(true);
                Bitmap myBitmap = Bitmap.createBitmap(receiptView.getDrawingCache());
                receiptView.setDrawingCacheEnabled(false); // clear drawing cache
                // print image.
                PrinterManager printer = new PrinterManager();
                printer.setupPage(384, -1);
                int ret = printer.drawBitmap(myBitmap, 0, 0);
                int retprinter = printer.printPage(0);
                if (retprinter != 0) {
                    printReceiptListener.onPrintFail();
                } else {
                    Intent intent = new Intent("urovo.prnt.message");
                    intent.putExtra("ret", ret);
                    receiptView.getContext().sendBroadcast(intent);
                    printer.prn_paperForWard(15);
                    printReceiptListener.onPrintSuccess();
                }
            }
        });
    }


}

