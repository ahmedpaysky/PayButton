package io.paysky.ui.fragment.magnetic;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paybutton.R;

import io.paysky.data.model.ReceiptData;
import io.paysky.data.model.PaymentData;
import io.paysky.ui.activity.pay.PayActivity;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.fragment.manualpayment.ManualPaymentFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.util.AppCache;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.MagneticCardReader;
import io.paysky.util.TrackData;

/**
 * A simple {@link Fragment} subclass.
 */
public class MagneticPaymentFragment extends BaseFragment {


    //GUI.
    ProgressDialog progressDialog;
    private View manualPayment;
    private View qrPayment;
    private PayActivity payActivity;
    //Objects,
    ActivityHelper activityHelper;
    private MagneticCardReader cardReaderService;
    private Handler magneticHandler;
    private PaymentData paymentData;
    private MagneticPaymentManager paymentManager;

    public MagneticPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHelper = (ActivityHelper) getActivity();
        payActivity = (PayActivity) getActivity();
        paymentManager = new MagneticPaymentManager(this);
        magneticHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                int x = msg.what;
                switch (x) {
                    case MagneticCardReader.MESSAGE_READ_MAG:
                        TrackData trackData = msg.getData().getParcelable(MagneticCardReader.CARD_TRACK_DATA);
                        paymentManager.createTransaction(trackData, paymentData);
                }
            }
        };
        cardReaderService = new MagneticCardReader();
        extractBundle();
        progressDialog = AppUtils.createProgressDialog(getActivity(), R.string.please_wait);
    }


    private void extractBundle() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            paymentData = arguments.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_magnetic_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        showViewsBasedOnUserPrefs();
    }

    private void showViewsBasedOnUserPrefs() {
        if (paymentData.enableManual) {
            manualPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
                    activityHelper.replaceFragmentAndRemoveOldFragment(ManualPaymentFragment.class, bundle);
                }
            });
        } else {
            manualPayment.setVisibility(View.GONE);
        }
        if (paymentData.enableQr) {
            qrPayment.setVisibility(View.VISIBLE);
            qrPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
                    activityHelper.replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
                }
            });
        }
    }

    private void initView(View view) {
        manualPayment = view.findViewById(R.id.manual_payment);
        qrPayment = view.findViewById(R.id.qr_payment);
    }


    @Override
    public void onStart() {
        super.onStart();
        cardReaderService.start(magneticHandler);
    }

    @Override
    public void onStop() {
        super.onStop();
        cardReaderService.stop();
    }


    public void showTransactionApprovedFragment(String transactionNumber, String approvalCode,
                                                String retrievalRefNr, String cardHolderName, String cardNumber, String systemTraceNumber) {
        Bundle bundle = new Bundle();
        ReceiptData transactionData = new ReceiptData();
        transactionData.rrn = transactionNumber;
        transactionData.authNumber = approvalCode;
        transactionData.channelName = AppConstant.TransactionChannelName.CARD;
        transactionData.refNumber = retrievalRefNr;
        transactionData.receiptNumber = retrievalRefNr;
        transactionData.amount = paymentData.amount;
        transactionData.cardHolderName = cardHolderName;
        transactionData.cardNumber = cardNumber;
        transactionData.merchantName = AppCache.getMerchantData(payActivity).merchantName;
        transactionData.merchantId = paymentData.merchantId;
        transactionData.terminalId = paymentData.terminalId;
        transactionData.paymentDoneBy = ReceiptData.PaymentDoneBy.MAGNETIC.toString();
        transactionData.stan = systemTraceNumber;
        transactionData.transactionType = ReceiptData.TransactionType.SALE.name();
        bundle.putParcelable(AppConstant.BundleKeys.RECEIPT, transactionData);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }

}
