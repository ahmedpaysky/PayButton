package io.paysky.ui.activity.payment;

import android.content.Intent;
import android.os.Bundle;

import io.paysky.data.model.PaymentData;
import io.paysky.data.model.request.MerchantInfoRequest;
import io.paysky.data.model.response.MerchantInfoResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.mvp.BasePresenter;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;

public class PayActivityPresenter extends BasePresenter<PayActivityView> {

    //Variables.
    private String merchantId;
    private String terminalId;
    private String secureHash;
    private String currencyCode;
    private double amount;
    //Objects
    private MerchantInfoResponse merchantInfoResponse;
    private Intent intent;

    PayActivityPresenter(Intent intent) {
        this.intent = intent;
        extractDataFromBundle();
    }

    void activityCreated() {
        view.makeActivityFullScreen();
    }

    public void activitySetView() {
        view.hideActionBar();
    }

    public void loadMerchantInfo() {
        view.showProgress();
        MerchantInfoRequest request = new MerchantInfoRequest();
        request.merchantId = merchantId;
        request.terminalId = terminalId;
        request.paymentMethod = null;
        request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        request.secureHash = HashGenerator.encode(secureHash, request.dateTimeLocalTrxn, merchantId, terminalId);
        ApiConnection.getMerchantInfo(request, new ApiResponseListener<MerchantInfoResponse>() {
            @Override
            public void onSuccess(MerchantInfoResponse response) {
                view.dismissProgress();
                merchantInfoResponse = response;
                view.showMerchantInfo(merchantInfoResponse.merchantName);
                view.showPaidAmount(amount + "", "EGP");
                view.showPaymentBasedOnPaymentOptions(merchantInfoResponse.paymentMethod);
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                view.dismissProgress();
                view.showFailedLoadMerchantDataDialog();
            }
        });
    }

    // create bundle to send it to fragments.
    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        PaymentData paymentData = new PaymentData();
        paymentData.terminalId = terminalId;
        paymentData.merchantId = merchantId;
        paymentData.amount = amount;
        paymentData.amountFormatted = AppUtils.convertToEnglishDigits(amount + "");
        paymentData.secureHashKey = secureHash;
        paymentData.currencyCode = currencyCode;
        paymentData.is3dsEnabled = merchantInfoResponse.is3DS;
        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
        return bundle;
    }

    private void extractDataFromBundle() {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            throw new IllegalStateException("cannot call our sdk without pass data with bundle");
        }
        merchantId = extras.getString(AppConstant.BundleKeys.MERCHANT_ID);
        terminalId = extras.getString(AppConstant.BundleKeys.TERMINAL_ID);
        amount = extras.getDouble(AppConstant.BundleKeys.PAY_AMOUNT);
        secureHash = extras.getString(AppConstant.BundleKeys.SECURE_HASH_KEY);
        currencyCode = extras.getString(AppConstant.BundleKeys.CURRENCY_CODE, null);
    }

    public Bundle getBundle() {
        return createBundle();
    }
}
