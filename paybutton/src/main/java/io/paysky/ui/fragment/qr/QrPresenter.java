package io.paysky.ui.fragment.qr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;

import com.example.paybutton.R;

import io.paysky.data.model.PaymentData;
import io.paysky.data.model.request.QrGeneratorRequest;
import io.paysky.data.model.request.SmsPaymentRequest;
import io.paysky.data.model.request.TransactionStatusRequest;
import io.paysky.data.model.response.GenerateQrCodeResponse;
import io.paysky.data.model.response.SmsPaymentResponse;
import io.paysky.data.model.response.TransactionStatusResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.activity.payment.PaymentActivity;
import io.paysky.ui.mvp.BasePresenter;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.ConvertQrCodToBitmapTask;
import io.paysky.util.HashGenerator;
import io.paysky.util.QrBitmapLoadListener;

public class QrPresenter extends BasePresenter<QrView> implements QrBitmapLoadListener {


    private final PaymentData paymentData;
    private String qrCode;
    private long transactionId;

    QrPresenter(Bundle arguments) {
        paymentData = arguments.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
    }

    public void checkPaymentApproval(long transactionId) {
        // create request.
        TransactionStatusRequest request = new TransactionStatusRequest();
        request.merchantId = paymentData.merchantId;
        request.terminalId = paymentData.terminalId;
        request.txnId = transactionId;
        request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        request.secureHash = HashGenerator.encode(paymentData.secureHashKey, request.dateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);
        // call api.
        ApiConnection.checkTransactionPaymentStatus(request, new ApiResponseListener<TransactionStatusResponse>() {
            @Override
            public void onSuccess(TransactionStatusResponse response) {
                if (response.success) {
                    // payment success.
                    if (response.isPaid) {
                        view.setPaymentApproved(response, paymentData);
                    } else {
                        view.listenToPaymentApproval();
                    }
                } else {
                    view.listenToPaymentApproval();
                }
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                view.listenToPaymentApproval();
            }
        });
    }

    public void generateQrCode() {
        if (!view.isInternetAvailable()) {
            view.showNoInternetDialog();
            return;
        }
        view.showProgress();
        QrGeneratorRequest request = new QrGeneratorRequest();
        request.setAmount(paymentData.amountFormatted);
        request.setMerchantId(paymentData.merchantId);
        request.setTerminalId(paymentData.terminalId);
        request.setTahweelQR(true);
        request.setmVisaQR(true);
        request.setDateTimeLocalTrxn(request.getDateTimeLocalTrxn());
        request.setSecureHash(HashGenerator.encode(paymentData.secureHashKey, request.getDateTimeLocalTrxn(), paymentData.merchantId, paymentData.terminalId));
        ApiConnection.generateQrCode(request, new ApiResponseListener<GenerateQrCodeResponse>() {
            @Override
            public void onSuccess(GenerateQrCodeResponse response) {
                view.dismissProgress();
                if (response.success) {
                    qrCode = response.iSOQR;
                    transactionId = response.txnId;
                    view.showProgress();
                    float sizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            300, view.getContext().getResources().getDisplayMetrics());
                    new ConvertQrCodToBitmapTask(QrPresenter.this, (int) sizePx).execute(response.iSOQR);
                    view.setGenerateQrSuccess(response.txnId);
                } else {
                    view.showInfoDialog(response.message);
                }
            }

            @Override
            public void onFail(Throwable error) {
                view.dismissProgress();
                error.printStackTrace();
                view.showErrorInServerDialog();
            }
        });
    }

    public void requestToPay(String mobileNumber) {
        if (!view.isInternetAvailable()) {
            view.showNoInternetDialog();
            return;
        }
        view.showProgress();
        SmsPaymentRequest smsPaymentRequest = new SmsPaymentRequest();
        smsPaymentRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        smsPaymentRequest.iSOQR = qrCode;
        smsPaymentRequest.merchantId = paymentData.merchantId;
        smsPaymentRequest.terminalId = paymentData.terminalId;
        smsPaymentRequest.txnId = transactionId;
        smsPaymentRequest.mobileNumber = mobileNumber;
        // generate hashing.
        smsPaymentRequest.secureHash = HashGenerator.encode(paymentData.secureHashKey, smsPaymentRequest.dateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);
        ApiConnection.requestToPay(smsPaymentRequest, new ApiResponseListener<SmsPaymentResponse>() {
            @Override
            public void onSuccess(SmsPaymentResponse response) {
                view.dismissProgress();
                if (response.success) {
                    view.showToast(R.string.send_sms_success);
                    view.listenToPaymentApproval();
                } else {
                    view.showInfoDialog(response.message);
                }
            }

            @Override
            public void onFail(Throwable error) {
                view.dismissProgress();
                error.printStackTrace();
                view.showErrorInServerDialog();
            }
        });
    }

    @Override
    public void onLoadBitmapQrSuccess(Bitmap bitmap) {
        view.dismissProgress();
        PaymentActivity.qrBitmap = bitmap;
        view.showQrImage(bitmap);
    }

    @Override
    public void onLoadBitmapQrFailed() {
        view.dismissProgress();
        view.showErrorInServerDialog();
    }
}
