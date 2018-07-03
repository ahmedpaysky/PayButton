package io.paysky.ui.fragment.manualpayment;

import android.os.Bundle;

import com.example.paybutton.R;

import java.util.TreeMap;

import io.paysky.data.model.request.ManualPaymentRequest;
import io.paysky.data.model.response.ManualPaymentResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.paymentfail.PaymentFailedFragment;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;

/**
 * Created by Paysky-202 on 5/27/2018.
 */

public class ManualPaymentManager {
    private CardManualPaymentFragment paymentFragment;

    public ManualPaymentManager(CardManualPaymentFragment paymentFragment) {
        this.paymentFragment = paymentFragment;
    }


    public void makePayment(String payAmount, final String merchantId, final String terminalId, String ccv, String expiryDate, String cardNumber, final String receiverMail) {
        // check internet.
        if (!paymentFragment.isInternetAvailable()) {
            paymentFragment.showNoInternetDialog();
            return;
        }
        paymentFragment.showProgress();
        // create request.
        ManualPaymentRequest paymentRequest = new ManualPaymentRequest();
        int amount = (int) (Double.valueOf(payAmount) * 100);
        paymentRequest.amountTrxn = amount + "";
        paymentRequest.cardAcceptorIDcode = merchantId;
        paymentRequest.cardAcceptorTerminalID = terminalId;
        paymentRequest.currencyCodeTrxn = "818";
        paymentRequest.cvv2 = ccv;
        paymentRequest.dateExpiration = expiryDate;
        paymentRequest.iSFromPOS = true;
        paymentRequest.pAN = cardNumber;
        paymentRequest.hostID = 105;// Integer.valueOf(AppUtils.getTerminalConfig(getActivity()).getHostid());
        paymentRequest.messageTypeID = "0200";
        paymentRequest.pOSEntryMode = "011";
        paymentRequest.processingCode = "000000";
        paymentRequest.systemTraceNr = AppUtils.generateRandomNumber();
        paymentRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        // create secure hash.
        TreeMap<String, Object> hashing = new TreeMap<>();
        hashing.put("AmountTrxn", paymentRequest.amountTrxn);
        hashing.put("CardAcceptorTerminalID", terminalId);
        hashing.put("CardAcceptorIDcode", merchantId);
        hashing.put("DateTimeLocalTrxn", paymentRequest.dateTimeLocalTrxn);
        hashing.put("MessageTypeID", paymentRequest.messageTypeID);
        hashing.put("POSEntryMode", paymentRequest.pOSEntryMode);
        hashing.put("ProcessingCode", paymentRequest.processingCode);
        hashing.put("SystemTraceNr", paymentRequest.systemTraceNr);
        paymentRequest.secureHash = HashGenerator.encode(hashing);
        // make transaction.
        ApiConnection.executePayment(paymentRequest, new ApiResponseListener<ManualPaymentResponse>() {
            @Override
            public void onSuccess(ManualPaymentResponse response) {
                // server make response.
                paymentFragment.hideProgress();
                PaymentTransaction transaction = ((PaymentTransaction) paymentFragment.getActivity());
                if (response.mWActionCode != null) {
                    transaction.setFailTransactionError(new Exception(response.mWMessage));
                    Bundle bundle = new Bundle();
                    bundle.putString("decline_cause", response.mWMessage);
                    paymentFragment.activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                } else {
                    if (response.actionCode == null || response.actionCode.isEmpty() || !response.actionCode.equals("00")) {
                        transaction.setFailTransactionError(new Exception(response.message));
                        Bundle bundle = new Bundle();
                        bundle.putString("decline_cause", response.message);
                        paymentFragment.activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                    } else {
                        // transaction success.
                        transaction.setSuccessTransactionId(response.transactionNo,
                                response.actionCode + " " + response.message, response.approvalCode);
                        paymentFragment.showTransactionApprovedFragment(response.transactionNo, response.approvalCode, response.retrievalRefNr);
                    }
                }
            }

            @Override
            public void onFail(Throwable error) {
                // payment failed.
                paymentFragment.hideProgress();
                ((PaymentTransaction) paymentFragment.getActivity()).setFailTransactionError(error);
                error.printStackTrace();
                new InfoDialog(paymentFragment.getActivity()).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again).setButtonText(R.string.ok).showDialog();
            }
        });
    }

}
