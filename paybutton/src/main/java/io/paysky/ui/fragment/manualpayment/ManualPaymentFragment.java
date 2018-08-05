package io.paysky.ui.fragment.manualpayment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.paybutton.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.paysky.data.model.ReceiptData;
import io.paysky.data.model.PaymentData;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.custom.CardEditText;
import io.paysky.ui.fragment.magnetic.MagneticPaymentFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.util.AppCache;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.LocaleHelper;


public class ManualPaymentFragment extends BaseFragment implements View.OnClickListener {


    //Objects,
    ActivityHelper activityHelper;
    private ManualPaymentManager paymentManager;
    private PaymentData paymentData;
    //GUI.
    private CardEditText cardNumberEditText;
    private EditText cardOwnerNameEditText;
    private EditText expireDateEditText;
    private EditText ccvEditText;
    private ProgressDialog progressDialog;
    private View qrPayment;
    private View magneticPayment;
    private Button proceedButton;
    private ImageView scanCardImageView;


    public ManualPaymentFragment() {
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
        // get data from bundle.
        extractBundle();
        paymentManager = new ManualPaymentManager(this);
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
        return inflater.inflate(R.layout.fragment_card_manual_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        showViewsBasedOnUserPrefs();
    }

    private void showViewsBasedOnUserPrefs() {
        if (paymentData.enableQr) {
            qrPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
                    activityHelper.replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
                }
            });
        } else {
            qrPayment.setVisibility(View.GONE);
        }


        if (!paymentData.enableManual) {
            // enable payment is disabled , prevent edit in editText.
            cardNumberEditText.setEnabled(false);
            cardOwnerNameEditText.setEnabled(false);
            ccvEditText.setEnabled(false);
            expireDateEditText.setEnabled(false);
            scanCardImageView.setEnabled(false);
            proceedButton.setEnabled(false);
        }

        if (paymentData.enableMagnetic) {
            if (AppUtils.isPaymentMachine(getContext())) {
                magneticPayment.setVisibility(View.VISIBLE);
                magneticPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // replace with magnetic fragment.
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, paymentData);
                        activityHelper.replaceFragmentAndRemoveOldFragment(MagneticPaymentFragment.class, bundle);
                    }
                });
            }
        } else {
            magneticPayment.setVisibility(View.GONE);
        }
    }

    private void initView(View view) {
        activityHelper.setHeaderIcon(R.drawable.ic_back);
        activityHelper.setHeaderIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        ImageView cardTypeImageView = view.findViewById(R.id.card_type_imageView);
        cardNumberEditText = view.findViewById(R.id.card_number_editText);
        cardNumberEditText.setCardTypeImage(cardTypeImageView);
        cardOwnerNameEditText = view.findViewById(R.id.card_owner_name_editText);
        expireDateEditText = view.findViewById(R.id.expire_date_editText);
        ccvEditText = view.findViewById(R.id.ccv_editText);
        proceedButton = view.findViewById(R.id.proceed_button);
        proceedButton.setOnClickListener(this);
        qrPayment = view.findViewById(R.id.wallet_payment_layout);
        scanCardImageView = view.findViewById(R.id.scan_camera_imageView);
        scanCardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanCardCameraButtonClick();
            }
        });

        magneticPayment = view.findViewById(R.id.magnetic_payment);
        if (LocaleHelper.getLocale(getActivity()).equals("ar")) {
            cardNumberEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            expireDateEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            ccvEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
    }


    @Override
    public void onClick(View view) {
        // validate user inputs.
        String cardNumber = getText(cardNumberEditText).replaceAll(" ", "");
        String cardOwnerName = getText(cardOwnerNameEditText);
        String expireDate = getText(expireDateEditText).replaceAll("/", "");
        String ccv = getText(ccvEditText);
        if (!isInputsValid(cardNumber, cardOwnerName, expireDate, ccv)) return;
        // replace expire date.
        String month = expireDate.substring(0, 2);
        String year = expireDate.substring(2);
        expireDate = year + month;
        hideKeyboard(view);
        paymentManager.makePayment(paymentData.amount, paymentData.merchantId,
                paymentData.terminalId, ccv, expireDate, cardOwnerName, cardNumber, paymentData.receiverMail);
    }

    private void hideKeyboard(View view) {
        AppUtils.hideKeyboard(view);
    }


    private boolean isInputsValid(String cardNumber, String ownerName, String expireDate, String ccv) {
        boolean isValidInputs = true;
        if (isEmpty(cardNumber) || cardNumber.length() < 16) {
            isValidInputs = false;
            cardNumberEditText.setError(getString(R.string.invalid_card_number_length));
        }

        if (isEmpty(ownerName)) {
            isValidInputs = false;
            cardOwnerNameEditText.setError(getString(R.string.enter_valid_owner));
        }
        if (isEmpty(expireDate) || expireDate.length() < 4) {
            isValidInputs = false;
            expireDateEditText.setError(getString(R.string.invalid_expire_date));
        } else {
            // validate that expire date large than today.
            int enteredMonth = Integer.parseInt(expireDate.substring(0, 2));
            int enteredYear = Integer.parseInt(expireDate.substring(2));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.US);
            String date = sdf.format(new Date());
            String month = date.substring(0, date.indexOf("/"));
            String year = date.substring(date.indexOf("/") + 1);
            int monthNumber = Integer.valueOf(month);
            int yearNumber = Integer.valueOf(year);
            if (enteredYear < yearNumber) {
                // invalid year.
                isValidInputs = false;
                expireDateEditText.setError(getString(R.string.invalid_expire_date_date));
            } else if (enteredYear == yearNumber) {
                // validate month.
                if (enteredMonth < monthNumber) {
                    isValidInputs = false;
                    expireDateEditText.setError(getString(R.string.invalid_expire_date_date));
                }
            }
        }

        if (isEmpty(ccv) || ccv.length() < 3) {
            isValidInputs = false;
            ccvEditText.setError(getString(R.string.invalid_ccv));
        }
        return isValidInputs;
    }

    void showProgress() {
        if (progressDialog == null) {
            progressDialog = AppUtils.createProgressDialog(getActivity(), R.string.please_wait);
        }
        progressDialog.show();
    }

    void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void onScanCardCameraButtonClick() {
        Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, 1005);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1005) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                // success.
                char[] chars = creditCard.cardNumber.toCharArray();
                StringBuilder numberBuilder = new StringBuilder();
                for (int i = 0; i < chars.length; i++) {
                    numberBuilder.append(chars[i]);
                    if (i + 1 % 4 == 0 && i + 1 != chars.length) {
                        numberBuilder.append(" ");
                    }
                }
                cardNumberEditText.setText(numberBuilder.toString());
            }
            // else handle other activity results
        }

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
        transactionData.merchantName = AppCache.getMerchantData(getActivity()).merchantName;
        transactionData.merchantId = paymentData.merchantId;
        transactionData.terminalId = paymentData.terminalId;
        transactionData.paymentDoneBy = ReceiptData.PaymentDoneBy.MANUAL.toString();
        transactionData.stan = systemTraceNumber;
        transactionData.transactionType = ReceiptData.TransactionType.SALE.name();
        bundle.putParcelable(AppConstant.BundleKeys.RECEIPT, transactionData);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }
}
