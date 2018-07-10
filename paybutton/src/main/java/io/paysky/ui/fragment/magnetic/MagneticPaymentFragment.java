package io.paysky.ui.fragment.magnetic;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.paybutton.R;

import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.data.network.request.magnetic.MigsRequest;
import io.paysky.data.network.request.magnetic.PointOfSale;
import io.paysky.data.network.response.MigsResonse;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.base.PaymentTransaction;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.manualpayment.CardManualPaymentFragment;
import io.paysky.ui.fragment.paymentfail.PaymentFailedFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.HashGenerator;
import io.paysky.util.MagneticCardReaderService;
import io.paysky.util.TrackData;

/**
 * A simple {@link Fragment} subclass.
 */
public class MagneticPaymentFragment extends BaseFragment {


    private ActivityHelper activityHelper;
    private MagneticCardReaderService cardReaderService;
    private String terminalId;
    private String merchantId;
    private String payAmount;
    private String receiverMail;
    private TrackData trackData;
    private ProgressDialog progressDialog;


    public MagneticPaymentFragment() {
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

        Handler magneticHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                int x = msg.what;
                switch (x) {
                    case MagneticCardReaderService.MESSAGE_READ_MAG:
                        TrackData trackData = msg.getData().getParcelable(MagneticCardReaderService.CARD_TRACK_DATA);
                        if (trackData.getTrackLenght() != 2) {
                            return;
                        }
                        createTransaction(trackData);
                }
            }
        };
        cardReaderService = new MagneticCardReaderService(getActivity(), magneticHandler);
        extractBundle();
        progressDialog = AppUtils.createProgressDialog(getActivity(), R.string.please_wait);
    }

    private void extractBundle() {
        Bundle arguments = getArguments();
        terminalId = arguments.getString(AppConstant.BundleKeys.TERMINAL_ID);
        merchantId = arguments.getString(AppConstant.BundleKeys.MERCHANT_ID);
        payAmount = arguments.getString(AppConstant.BundleKeys.PAY_AMOUNT);
        receiverMail = arguments.getString(AppConstant.BundleKeys.RECEIVER_MAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_magnetic_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        LinearLayout manualLayout = view.findViewById(R.id.manual_layout);
        manualLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId + "");
                bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId + "");
                bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount + "");
                bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
                activityHelper.replaceFragmentAndRemoveOldFragment(CardManualPaymentFragment.class, bundle);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        cardReaderService.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        cardReaderService.stop();
    }


    private void createTransaction(TrackData trackData) {
        if (!isInternetAvailable()) {
            showNoInternetDialog();
            return;
        }
        progressDialog.show();
        // create request.
        MigsRequest migsRequest = new MigsRequest();
        PointOfSale pointOfSale = new PointOfSale();
        pointOfSale.setTransactionType("sale");
        String formatedAmount = AppUtils.formatPaymentAmountToServer(payAmount) + "";
        pointOfSale.setAmountTrxn(formatedAmount);
        pointOfSale.setCardAcceptorTerminalID(terminalId);
        pointOfSale.setCardAcceptorIDcode(merchantId);
        pointOfSale.setPAN(trackData.getCardNumber().trim());
        pointOfSale.setDateExpiration(trackData.getExpiryData().trim());
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
                progressDialog.dismiss();

                PaymentTransaction transaction = ((PaymentTransaction) getActivity());
                if (response.getMWActionCode() != null) {
                    transaction.setFailTransactionError(new Exception(response.getMWMessage()));
                    Bundle bundle = new Bundle();
                    bundle.putString("decline_cause", response.getMWMessage());
                    activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                } else {
                    String actionCode = response.getActionCode();
                    if (actionCode == null || actionCode.isEmpty() || !actionCode.equals("00")) {
                        transaction.setFailTransactionError(new Exception(response.getMessage()));
                        Bundle bundle = new Bundle();
                        bundle.putString("decline_cause", response.getMessage());
                        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                    } else {
                        // transaction success.
                        transaction.setSuccessTransactionId(response.getTransactionNo(),
                                response.getActionCode() + " " + response.getMessage(), response.getApprovalCode());
                        showTransactionApprovedFragment(response.getTransactionNo(), response.getApprovalCode(), response.getRetrievalRefNr());
                    }
                }

            }

            @Override
            public void onFail(Throwable error) {
                progressDialog.dismiss();
                error.printStackTrace();
                new InfoDialog(getActivity()).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again).setButtonText(R.string.ok).showDialog();
            }
        });
    }

    public void showTransactionApprovedFragment(String transactionNumber, String approvalCode, String retrievalRefNr) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_ID, transactionNumber);
        bundle.putString(AppConstant.BundleKeys.AUTH_NUMBER, approvalCode);
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId);
        bundle.putString(AppConstant.BundleKeys.REFERENCE_NUMBER, retrievalRefNr);
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_CHANNEL, AppConstant.TransactionChannelName.CARD);
        bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }

}
