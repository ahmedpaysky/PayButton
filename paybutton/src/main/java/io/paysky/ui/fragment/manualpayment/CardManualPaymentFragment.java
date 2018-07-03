package io.paysky.ui.fragment.manualpayment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.paybutton.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.custom.CardEditText;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.ui.fragment.qr.QrCodePaymentFragment;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;


public class CardManualPaymentFragment extends BaseFragment implements View.OnClickListener {


    //Objects,
    ActivityHelper activityHelper;
    private ManualPaymentManager paymentManager;
    //GUI.
    private CardEditText cardNumberEditText;
    private EditText cardOwnerNameEditText;
    private EditText expireDateEditText;
    private EditText ccvEditText;
    private ProgressDialog progressDialog;
    //Variables.
    private String terminalId, merchantId;
    private String payAmount, receiverMail;

    public CardManualPaymentFragment() {
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
        terminalId = arguments.getString(AppConstant.BundleKeys.TERMINAL_ID);
        merchantId = arguments.getString(AppConstant.BundleKeys.MERCHANT_ID);
        payAmount = arguments.getString(AppConstant.BundleKeys.PAY_AMOUNT);
        receiverMail = arguments.getString(AppConstant.BundleKeys.RECEIVER_MAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_manual_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
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
        Button proceedButton = view.findViewById(R.id.proceed_button);
        proceedButton.setOnClickListener(this);
        LinearLayout walletPaymentLayout = view.findViewById(R.id.wallet_payment_layout);
        walletPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
                bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId);
                bundle.putString(AppConstant.BundleKeys.PAY_AMOUNT, payAmount);
                bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
                activityHelper.replaceFragmentAndRemoveOldFragment(QrCodePaymentFragment.class, bundle);
            }
        });
        ImageView scanCardWithCameraImageView = view.findViewById(R.id.scan_camera_imageView);
        scanCardWithCameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanCardCameraButtonClick();
            }
        });
        //SQ27
        //msm8610
        //qcom
        String model = Build.MODEL;
        LinearLayout swipeLayout = view.findViewById(R.id.swipe_layout);
        if (!model.equals("SQ27")) {
            swipeLayout.setVisibility(View.GONE);
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
        paymentManager.makePayment(payAmount, merchantId, terminalId, ccv, expireDate, cardNumber, receiverMail);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private boolean isInputsValid(String cardNumber, String ownerName, String expireDate, String ccv) {
        boolean isValidInputs = true;
        if (isEmpty(cardNumber) || cardNumber.length() < 16) {
            isValidInputs = false;
            cardNumberEditText.setError(getString(R.string.invalid_card_number_length));
        }

        if (!cardNumberEditText.isValid()) {
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

    public void showTransactionApprovedFragment(String transactionNumber, String approvalCode, String retrievalRefNr) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_ID, transactionNumber);
        bundle.putString(AppConstant.BundleKeys.AUTH_NUMBER, approvalCode);
        bundle.putString(AppConstant.BundleKeys.RECEIVER_MAIL, receiverMail);
        bundle.putString(AppConstant.BundleKeys.MERCHANT_ID, merchantId);
        bundle.putString(AppConstant.BundleKeys.TERMINAL_ID, terminalId);
        bundle.putString(AppConstant.BundleKeys.REFERENCE_NUMBER, retrievalRefNr);
        bundle.putString(AppConstant.BundleKeys.TRANSACTION_CHANNEL, AppConstant.TransactionChannelName.CARD);
        activityHelper.replaceFragmentAndAddOldToBackStack(PaymentApprovedFragment.class, bundle);
    }
}
