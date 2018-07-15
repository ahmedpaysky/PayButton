package io.paysky.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.paybutton.R;

import io.paysky.data.event.PaymentStatusEvent;
import io.paysky.ui.activity.pay.PayActivity;
import io.paysky.util.AppConstant;
import io.paysky.util.PaymentObservable;
import io.paysky.util.PaymentObserver;

/**
 * Created by amrel on 26/03/2018.
 */


public class PayButton extends LinearLayout implements PaymentObserver {

    //Variables.
    long merchantId = 0;
    long terminalId = 0;
    Double payAmount = 0.0;
    int currencyCode = 0;
    String notificationValue = null;

    public boolean isEnableManualPayment() {
        return enableManualPayment;
    }

    public void setEnableManualPayment(boolean enableManualPayment) {
        this.enableManualPayment = enableManualPayment;
    }

    public boolean isEnableMagneticPayment() {
        return enableMagneticPayment;
    }

    public void setEnableMagneticPayment(boolean enableMagneticPayment) {
        this.enableMagneticPayment = enableMagneticPayment;
    }

    public boolean isEnableQrPayment() {
        return enableQrPayment;
    }

    public void setEnableQrPayment(boolean enableQrPayment) {
        this.enableQrPayment = enableQrPayment;
    }

    public int getDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(int defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    public String getServerLink() {
        return serverLink;
    }

    public void setServerLink(String serverLink) {
        this.serverLink = serverLink;
    }

    private boolean enableManualPayment, enableMagneticPayment, enableQrPayment;
    private String serverLink;
    private int defaultPayment;
    //Objects.
    PaymentTransactionCallback transactionCallback;
    NotificationType notificationType;

    public PayButton(Context context) {
        super(context);
        inflateView();
    }

    public PayButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public PayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PayButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflateView();
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


    public void setPayAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be more than 0");
        }
        this.payAmount = amount;
    }

    public void setCurrencyCode(int currencyCode) {
        if (currencyCode <= 0) {
            throw new IllegalArgumentException("invalid currency code");
        }
        this.currencyCode = currencyCode;
    }


    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setNotificationValue(String notificationValue) {
        this.notificationValue = notificationValue;
    }


    void inflateView() {
        View inflate = inflate(getContext(), R.layout.custom_pay_btn, null);
        addView(inflate);
        PaymentObservable.addObserver(this);
    }


    private void openPaymentActivity() {
        Bundle bundle = new Bundle();
        bundle.putLong("terminal_id", terminalId);
        bundle.putLong("merchant_id", merchantId);
        bundle.putDouble("pay_amount", payAmount);
        bundle.putString("receiver_mail", notificationValue);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MANUAL, enableManualPayment);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_MAGNETIC, enableMagneticPayment);
        bundle.putBoolean(AppConstant.BundleKeys.ENABLE_QR, enableQrPayment);
        bundle.putInt(AppConstant.BundleKeys.DEFAULT_PAYMENT, defaultPayment);
        bundle.putString(AppConstant.BundleKeys.SERVER_LINK, serverLink);
        Intent intent = new Intent(getContext(), PayActivity.class);
        intent.putExtras(bundle);
        //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    public void createTransaction(PaymentTransactionCallback itemClickListener) {
        this.transactionCallback = itemClickListener;
        if (merchantId == 0 || terminalId == 0 || currencyCode == 0 || payAmount == 0) {
            transactionCallback.onError(new Exception("Please Enter The Merchant and terminal and Currency Code and Amout"));
            return;
        }
        if (notificationType == NotificationType.EMAIL && notificationValue.equals("")) {
            transactionCallback.onError(new Exception("Please Enter notification value"));
            return;
        }
        openPaymentActivity();
    }

    @Override
    public void sendPaymentStatus(PaymentStatusEvent paymentStatusEvent) {
        if (paymentStatusEvent.referenceNumber != null) {
            transactionCallback.onSuccess(paymentStatusEvent.referenceNumber, paymentStatusEvent.responseCode,
                    paymentStatusEvent.authorizationCode);
        } else {
            transactionCallback.onError(paymentStatusEvent.failException);
        }
    }

    public interface PaymentTransactionCallback {
        void onSuccess(String referenceNumber, String responseCode, String authorizationCode);

        void onError(Throwable error);
    }


    @Override
    protected void finalize() throws Throwable {
        PaymentObservable.removeObserver(this);
        super.finalize();
    }
}
