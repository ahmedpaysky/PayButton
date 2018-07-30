package io.paysky.ui.fragment.magnetic;

import android.os.Bundle;

import com.example.paybutton.R;

import io.paysky.data.model.PaymentData;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.data.network.request.magnetic.MigsRequest;
import io.paysky.data.network.request.magnetic.PointOfSale;
import io.paysky.data.network.response.MigsResonse;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.paymentfail.PaymentFailedFragment;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;
import io.paysky.util.TrackData;

public class MagneticPaymentManager {

    private MagneticPaymentFragment paymentFragment;

    public MagneticPaymentManager(MagneticPaymentFragment paymentFragment) {
        this.paymentFragment = paymentFragment;
    }


    void createTransaction(final TrackData trackData, PaymentData paymentData) {
        if (!paymentFragment.isInternetAvailable()) {
            paymentFragment.showNoInternetDialog();
            return;
        }
        paymentFragment.progressDialog.show();
        // create request.
        MigsRequest migsRequest = new MigsRequest();
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setTransactionType("sale");
        String formatedAmount = AppUtils.formatPaymentAmountToServer(paymentData.amount) + "";
        pointOfSale.setAmountTrxn(formatedAmount);
        pointOfSale.setTerminalId(paymentData.terminalId);
        pointOfSale.setMerchantId(paymentData.merchantId);
        pointOfSale.setPAN(trackData.cardNumber);
        pointOfSale.setDateExpiration(trackData.expiryDate);
        pointOfSale.setTrack2Data(trackData.getTrack2());
        switch (pointOfSale.getTransactionType()) {
            case "sale":
                pointOfSale.setProcessingCode("000000");
                break;
            case "void":
                pointOfSale.setProcessingCode("020000");
                pointOfSale.setTransactionNo(pointOfSale.getTransactionNo());
                break;
            case "refund":
                pointOfSale.setProcessingCode("200000");
                pointOfSale.setTransactionNo(pointOfSale.getTransactionNo());
                break;
        }
        pointOfSale.setISFromPOS(true);
        pointOfSale.setPOSEntryMode("022");
        pointOfSale.setDateTimeLocalTrxn("" + System.currentTimeMillis());
        pointOfSale.setCurrencyCodeTrxn("818");
        pointOfSale.setHostID(105);
        pointOfSale.setMessageTypeID("0200");
        pointOfSale.setSystemTraceNr("555898fdf");
        migsRequest.setPointOfSale(pointOfSale);
        String hashKey = HashGenerator.encode(HashGenerator.createHashData(migsRequest));
        pointOfSale.setSecureHash(hashKey);

        ApiConnection.createMagneticTransaction(migsRequest, new ApiResponseListener<MigsResonse>() {
            @Override
            public void onSuccess(MigsResonse response) {
                paymentFragment.progressDialog.dismiss();

                PaymentTransaction transaction = ((PaymentTransaction) paymentFragment.getActivity());
                if (response.getMWActionCode() != null) {
                    transaction.setFailTransactionError(new Exception(response.getMWMessage()));
                    Bundle bundle = new Bundle();
                    bundle.putString("decline_cause", response.getMWMessage());
                    paymentFragment.activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                } else {
                    String actionCode = response.getActionCode();
                    if (actionCode == null || actionCode.isEmpty() || !actionCode.equals("00")) {
                        transaction.setFailTransactionError(new Exception(response.getMessage()));
                        Bundle bundle = new Bundle();
                        bundle.putString("decline_cause", response.getMessage());
                        paymentFragment.activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                    } else {
                        // transaction success.
                        transaction.setSuccessTransactionId(response.getTransactionNo(),
                                response.getActionCode() + " " + response.getMessage(), response.getApprovalCode());
                        paymentFragment.showTransactionApprovedFragment(response.getTransactionNo(),
                                response.getApprovalCode(), response.getRetrievalRefNr(), trackData.cardHolderName,
                                trackData.cardNumber, response.getSystemTraceNr());
                    }
                }

            }

            @Override
            public void onFail(Throwable error) {
                paymentFragment.progressDialog.dismiss();
                error.printStackTrace();
                new InfoDialog(paymentFragment.getActivity()).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again).setButtonText(R.string.ok).showDialog();
            }
        });
    }
}
