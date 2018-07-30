package io.paysky.ui.fragment.paymentsuccess;

import com.example.paybutton.R;

import java.util.TreeMap;

import io.paysky.data.model.request.SendReceiptByMailRequest;
import io.paysky.data.model.response.SendReceiptByMailResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;


class PaymentApprovedManager {
    private PaymentApprovedFragment paymentApprovedFragment;

    PaymentApprovedManager(PaymentApprovedFragment paymentApprovedFragment) {
        this.paymentApprovedFragment = paymentApprovedFragment;
    }


    void sendEmail(final String email, String terminalId, String merchantId,
                   String referenceNumber, String channelName, String transactionId, final int operationType) {
        // check internet.
        if (!paymentApprovedFragment.isInternetAvailable()) {
            paymentApprovedFragment.showNoInternetDialog();
            return;
        }
        // show progress dialog and send email to user.
        paymentApprovedFragment.showProgressDialog();
        // create request body.
        final SendReceiptByMailRequest request = new SendReceiptByMailRequest();
        request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        request.emailTo = email;
        request.terminalId = terminalId;
        request.merchantId = merchantId;
        request.clientId = 204;
        request.transactionChannel = channelName;
        if (request.transactionChannel.equals(AppConstant.TransactionChannelName.CARD)) {
            request.externalReceiptNo = referenceNumber;
        } else {
            request.transactionId = transactionId;
        }

        TreeMap<String, Object> hashing = new TreeMap<>();
        hashing.put("TerminalId", terminalId);
        hashing.put("MerchantId", merchantId);
        hashing.put("ClientId", request.clientId);
        hashing.put("DateTimeLocalTrxn", request.dateTimeLocalTrxn);
        request.secureHash = HashGenerator.hashEmail(hashing);
        ApiConnection.sendReceiptByMail(request, new ApiResponseListener<SendReceiptByMailResponse>() {
            @Override
            public void onSuccess(SendReceiptByMailResponse response) {
                paymentApprovedFragment.dismissProgressDialog();
                if (response.success) {
                    paymentApprovedFragment.setSendEmailSuccess(request.emailTo, operationType);
                } else {
                    // show error dialog.
                    new InfoDialog(paymentApprovedFragment.getActivity()).setDialogTitle(R.string.error)
                            .setDialogText(response.message).setButtonText(R.string.ok).showDialog();
                }
            }

            @Override
            public void onFail(Throwable error) {
                paymentApprovedFragment.dismissProgressDialog();
                error.printStackTrace();
                new InfoDialog(paymentApprovedFragment.getActivity()).setDialogTitle(R.string.error)
                        .setDialogText(R.string.cannot_send_mail).setButtonText(R.string.ok).showDialog();
            }
        });
    }

}
