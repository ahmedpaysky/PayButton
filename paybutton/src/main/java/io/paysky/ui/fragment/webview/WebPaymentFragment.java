package io.paysky.ui.fragment.webview;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.paybutton.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import io.paysky.data.model.PaymentData;
import io.paysky.data.model.ReceiptData;
import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.request.ManualPaymentRequest;
import io.paysky.data.model.request.Process3dTransactionRequest;
import io.paysky.data.model.response.ManualPaymentResponse;
import io.paysky.data.model.response.Process3dTransactionResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.activity.payment.PaymentActivity;
import io.paysky.ui.base.BaseFragment;
import io.paysky.ui.base.PaymentTransactionListener;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.ui.fragment.paymentfail.PaymentFailedFragment;
import io.paysky.ui.fragment.paymentsuccess.PaymentApprovedFragment;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;
import io.paysky.util.DialogUtils;
import io.paysky.util.HashGenerator;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebPaymentFragment extends BaseFragment implements WebPaymentView {

    private WebPaymentPresenter presenter;
    private WebView webView;
    private PaymentData paymentData;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String requestBody;
    private String url;
    private int gatewayType;

    public WebPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new WebPaymentPresenter();
        presenter.attachView(this);
        activity = (PaymentActivity) getActivity();
        extractBundleData();
    }

    private void extractBundleData() {
        Bundle arguments = getArguments();
        paymentData = arguments.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
        cardNumber = arguments.getString("card_number");
        expiryDate = arguments.getString("expiry_date");
        cvv = arguments.getString("cvv");
        requestBody = arguments.getString("request_body");
        url = arguments.getString("url");
        gatewayType = arguments.getInt("gateway_type");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_payment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        presenter.load3dWebView();
    }

    private void executeTransaction(Process3dTransactionResponse response) {
        ManualPaymentRequest paymentRequest = new ManualPaymentRequest();
        final int paidAmount = AppUtils.formatPaymentAmountToServer(paymentData.amountFormatted);
        paymentRequest.amountTrxn = paidAmount + "";
        paymentRequest.cardAcceptorIDcode = paymentRequest.merchantId;
        paymentRequest.cardAcceptorTerminalID = paymentData.terminalId;
        paymentRequest.currencyCodeTrxn = paymentData.currencyCode;
        paymentRequest.cvv2 = cvv;
        paymentRequest.dateExpiration = expiryDate;
        paymentRequest.pAN = cardNumber;
        paymentRequest.systemTraceNr = AppUtils.generateRandomNumber();
        paymentRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        paymentRequest.merchantId = paymentData.merchantId;
        paymentRequest.terminalId = paymentData.terminalId;
        paymentRequest.verToken = response.verToken;
        paymentRequest.threeDSECI = response.threeDSECI;
        paymentRequest.threeDSenrolled = response.threeDSenrolled;
        paymentRequest.threeDSstatus = response.threeDSstatus;
        paymentRequest.threeDSXID = response.threeDSXID;
        paymentRequest.verType = response.verType;
        paymentRequest.secureHash = HashGenerator.encode(paymentData.secureHashKey, paymentRequest.dateTimeLocalTrxn,
                paymentData.merchantId, paymentData.terminalId);
        showProgress();
        ApiConnection.executePayment(paymentRequest, new ApiResponseListener<ManualPaymentResponse>()

        {
            @Override
            public void onSuccess(ManualPaymentResponse response) {
                if (isDetached())return;
                // server make response.
                dismissProgress();
                PaymentTransactionListener transaction = ((PaymentTransactionListener) getActivity());
                if (response.mWActionCode != null) {
                    transaction.setFailTransactionError(new Exception(response.mWMessage));
                    Bundle bundle = new Bundle();
                    bundle.putString("decline_cause", response.mWMessage);
                    bundle.putString("opened_by","manual_payment");
                    activity.replaceFragmentAndAddOldToBackStack(PaymentFailedFragment.class, bundle);
                } else {
                    if (response.actionCode == null || response.actionCode.isEmpty() || !response.actionCode.equals("00")) {
                        transaction.setFailTransactionError(new Exception(response.message));
                        Bundle bundle = new Bundle();
                        bundle.putString("decline_cause", response.message);
                        activity.replaceFragmentAndRemoveOldFragment(PaymentFailedFragment.class, bundle);
                    } else {
                        // transaction success.
                        SuccessfulCardTransaction cardTransaction = new SuccessfulCardTransaction();
                        cardTransaction.ActionCode = response.actionCode;
                        cardTransaction.AuthCode = response.authCode;
                        cardTransaction.MerchantReference = response.merchantReference;
                        cardTransaction.Message = response.message;
                        cardTransaction.NetworkReference = response.networkReference;
                        cardTransaction.ReceiptNumber = response.receiptNumber;
                        cardTransaction.SystemReference = response.systemReference + "";
                        cardTransaction.Success = response.success;
                        transaction.successCardTransaction(cardTransaction);

                        showTransactionApprovedFragment(response.transactionNo, response.authCode,
                                response.receiptNumber, "", cardNumber, response.systemReference + "");
                    }
                }
            }

            @Override
            public void onFail(Throwable error) {
                // payment failed.
                if (isDetached())return;
                dismissProgress();
                activity.setFailTransactionError(error);
                error.printStackTrace();
                DialogUtils.showInfoDialog(activity, getString(R.string.error_try_again));
            }
        });
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
        transactionData.amount = paymentData.amountFormatted;
        transactionData.cardHolderName = cardHolderName;
        transactionData.cardNumber = cardNumber;
        transactionData.merchantName = paymentData.merchantName;
        transactionData.merchantId = paymentData.merchantId;
        transactionData.terminalId = paymentData.terminalId;
        transactionData.paymentType = ReceiptData.PaymentDoneBy.MANUAL.toString();
        transactionData.stan = systemTraceNumber;
        transactionData.transactionType = ReceiptData.TransactionType.SALE.name();
        transactionData.secureHashKey = paymentData.secureHashKey;
        bundle.putParcelable(AppConstant.BundleKeys.RECEIPT, transactionData);
        activity.replaceFragmentAndRemoveOldFragment(PaymentApprovedFragment.class, bundle);
    }


    @Override
    public void load3dTransactionWebView() {
        webView.postUrl(url, requestBody.getBytes());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith("http://localhost.com")) {
                    // call server.
                    Uri uri = Uri.parse(url);
                    Set<String> names = uri.getQueryParameterNames();
                    JSONObject jsonObject = new JSONObject();
                    for (String key : names) {
                        try {
                            jsonObject.put(key, uri.getQueryParameter(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    showProgress();
                    // request.
                    Process3dTransactionRequest request = new Process3dTransactionRequest();
                    request.gatewayType = gatewayType;
                    request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
                    request.merchantId = paymentData.merchantId;
                    request.terminalId = paymentData.terminalId;
                    request.secureHash = HashGenerator.encode(paymentData.secureHashKey, request.dateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);
                    request.threeDSResponseData = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.DEFAULT);

                    ApiConnection.process3dTransaction(request, new ApiResponseListener<Process3dTransactionResponse>() {
                        @Override
                        public void onSuccess(Process3dTransactionResponse response) {
                            dismissProgress();
                            executeTransaction(response);
                        }

                        @Override
                        public void onFail(Throwable error) {
                            dismissProgress();
                            Log.d("LOG", "error = " + error.getMessage());
                        }
                    });
                } else {
                    showProgress();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissProgress();
            }
        });
    }
}
