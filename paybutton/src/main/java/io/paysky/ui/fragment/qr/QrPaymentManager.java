package io.paysky.ui.fragment.qr;

import android.content.Context;

import com.example.paybutton.R;

import java.util.TreeMap;

import io.paysky.data.model.request.QrGenratorRequest;
import io.paysky.data.model.request.SmsPaymentRequest;
import io.paysky.data.model.request.TransactionStatusRequest;
import io.paysky.data.model.response.GenerateQrCodeResponse;
import io.paysky.data.model.response.SmsPaymentResponse;
import io.paysky.data.model.response.TransactionStatusResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;

/**
 * Created by Paysky-202 on 5/27/2018.
 */

public class QrPaymentManager {
    private QrCodePaymentFragment paymentFragment;
    private Context context;

    public QrPaymentManager(QrCodePaymentFragment paymentFragment, Context context) {
        this.paymentFragment = paymentFragment;
        this.context = context;
    }


    public void generateQrCode(String amount, String merchantId, String terminalId) {
        if (!paymentFragment.isInternetAvailable()) {
            paymentFragment.showNoInternetDialog();
            return;
        }
        paymentFragment.showProgressDialog();
        QrGenratorRequest request = new QrGenratorRequest();
        request.setAmount(amount + "");
        request.setMerchantId(merchantId);
        request.setTerminalId(terminalId);
        request.setDateTimeLocalTrxn(request.getDateTimeLocalTrxn());
        request.setSecureHash(HashGenerator.createHashData(request));
        ApiConnection.generateQrCode(request, new ApiResponseListener<GenerateQrCodeResponse>() {
            @Override
            public void onSuccess(GenerateQrCodeResponse response) {
                paymentFragment.dismissProgressDialog();
                if (response.success) {
                    paymentFragment.setGenerateQrSuccess(response.iSOQR, (int) response.txnId);
                } else {
                    new InfoDialog(context).setDialogTitle(R.string.error)
                            .setDialogText(response.message)
                            .setButtonText(R.string.ok).showDialog();
                }
            }

            @Override
            public void onFail(Throwable error) {
                paymentFragment.dismissProgressDialog();
                error.printStackTrace();
                new InfoDialog(context).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again)
                        .setButtonText(R.string.ok).showDialog();
            }
        });
    }


    void checkPaymentApproval(String merchantId, String terminalId, int transactionId) {
        TransactionStatusRequest request = new TransactionStatusRequest();
        request.merchantId = merchantId;
        request.terminalId = terminalId;
        request.txnId = transactionId;
        request.clientId = 204;
        request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        TreeMap<String, Object> hashing = new TreeMap<>();
        hashing.put("TerminalId", terminalId);
        hashing.put("MerchantId", merchantId);
        hashing.put("ClientId", 204);
        hashing.put("DateTimeLocalTrxn", request.dateTimeLocalTrxn);
        request.secureHash = HashGenerator.hashCheckPaymentStatus(hashing);
        ApiConnection.checkTransactionPaymentStatus(request, new ApiResponseListener<TransactionStatusResponse>() {
            @Override
            public void onSuccess(TransactionStatusResponse response) {
                if (response.success) {
                    // payment success.
                    if (response.isPaid) {
                        paymentFragment.setPaymentApproved(response.externalTxnId);
                    } else {
                        paymentFragment.listenToPaymentApproval();
                    }
                } else {
                    paymentFragment.listenToPaymentApproval();
                }
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                paymentFragment.listenToPaymentApproval();
            }
        });
    }

    void requestToPay(String qrCode, String merchantId, String terminalId, long transactionId, String mobileNumber) {
        if (!paymentFragment.isInternetAvailable()) {
            paymentFragment.showNoInternetDialog();
            return;
        }
        paymentFragment.showProgressDialog();
        SmsPaymentRequest smsPaymentRequest = new SmsPaymentRequest();
        smsPaymentRequest.clientId = 204;
        smsPaymentRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        smsPaymentRequest.iSOQR = qrCode;
        smsPaymentRequest.merchantId = merchantId;
        smsPaymentRequest.terminalId = terminalId;
        smsPaymentRequest.txnId = transactionId;
        smsPaymentRequest.mobileNumber = mobileNumber;
        // generate hashing.
        TreeMap<String, Object> hashing = new TreeMap<>();
        hashing.put("TerminalId", terminalId);
        hashing.put("MerchantId", merchantId);
        hashing.put("ClientId", 204);
        hashing.put("DateTimeLocalTrxn", smsPaymentRequest.dateTimeLocalTrxn);
        smsPaymentRequest.secureHash = HashGenerator.hashCheckPaymentStatus(hashing);
        ApiConnection.requestToPay(smsPaymentRequest, new ApiResponseListener<SmsPaymentResponse>() {
            @Override
            public void onSuccess(SmsPaymentResponse response) {
                paymentFragment.dismissProgressDialog();
                if (response.success) {
                    paymentFragment.showToastMessage(R.string.send_sms_success);
                    paymentFragment.listenToPaymentApproval();
                } else {
                    new InfoDialog(context).setDialogTitle(R.string.error)
                            .setDialogText(response.message)
                            .setButtonText(R.string.ok).showDialog();
                }
            }

            @Override
            public void onFail(Throwable error) {
                paymentFragment.dismissProgressDialog();
                error.printStackTrace();
                new InfoDialog(context).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again)
                        .setButtonText(R.string.ok).showDialog();
            }
        });
    }
}
