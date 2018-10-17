package io.paysky.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.paysky.data.event.PaymentStatusEvent;
import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.SuccessfulWalletTransaction;
import io.paysky.ui.activity.payment.PaymentActivity;
import io.paysky.util.AppConstant;
import io.paysky.util.PaymentObservable;
import io.paysky.util.PaymentObserver;

/**
 * Created by amrel on 26/03/2018.
 */


public class PayButton implements PaymentObserver {

    //Variables.
    private long merchantId = 0;
    private long terminalId = 0;
    private double amount = 0.0;
    private int currencyCode = 0;
    private String merchantSecureHash;
    //Objects.
    private PaymentTransactionCallback transactionCallback;
    private Context context;


    public PayButton(Context context) {
        this.context = context;
    }


    public void setMerchantId(long merchantId) {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("invalid merchant id");
        }
        this.merchantId = merchantId;
    }


    public void setTerminalId(long terminalId) {
        if (terminalId <= 0) {
            throw new IllegalArgumentException("invalid terminal id");
        }
        this.terminalId = terminalId;
    }


    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be more than 0");
        }
        this.amount = amount;
    }

    public void setCurrencyCode(int currencyCode) {
        if (currencyCode <= 0) {
            throw new IllegalArgumentException("invalid currency code");
        }
        this.currencyCode = currencyCode;
    }

    public void setMerchantSecureHash(String merchantSecureHash) {
        this.merchantSecureHash = merchantSecureHash;
    }

    private void openPaymentActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId + "");
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId + "");
        bundle.putDouble(AppConstant.BundleKeys.PAY_AMOUNT, amount);
        bundle.putString(AppConstant.BundleKeys.SECURE_HASH_KEY, merchantSecureHash);
        if (currencyCode != 0) {
            bundle.putString(AppConstant.BundleKeys.CURRENCY_CODE, currencyCode + "");
        }
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void createTransaction(PaymentTransactionCallback itemClickListener) {
        this.transactionCallback = itemClickListener;
        validateUserInputs();
        openPaymentActivity();
        PaymentObservable.addObserver(this);
    }

    private void validateUserInputs() {
        if (merchantId == 0 || terminalId == 0 || amount == 0 || merchantSecureHash == null || merchantSecureHash.isEmpty()) {
            throw new IllegalStateException("add all required fields to create transaction");
        }
    }

    @Override
    public void sendPaymentStatus(PaymentStatusEvent paymentStatusEvent) {
        if (paymentStatusEvent.failException == null) {
            if (paymentStatusEvent.cardTransaction != null) {
                transactionCallback.onCardTransactionSuccess(paymentStatusEvent.cardTransaction);
            } else {
                transactionCallback.onWalletTransactionSuccess(paymentStatusEvent.walletTransaction);
            }
        } else {
            transactionCallback.onError(paymentStatusEvent.failException);
        }
    }

    public interface PaymentTransactionCallback {
        void onCardTransactionSuccess(SuccessfulCardTransaction cardTransaction);

        void onWalletTransactionSuccess(SuccessfulWalletTransaction walletTransaction);

        void onError(Throwable error);
    }


    @Override
    protected void finalize() throws Throwable {
        PaymentObservable.removeObserver(this);
        super.finalize();
    }

}
