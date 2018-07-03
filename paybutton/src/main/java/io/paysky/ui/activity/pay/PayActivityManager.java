package io.paysky.ui.activity.pay;

import com.example.paybutton.R;

import java.io.IOException;
import java.util.TreeMap;

import io.paysky.data.model.request.MerchantDataRequest;
import io.paysky.data.model.response.MerchantDataResponse;
import io.paysky.data.network.ApiConnection;
import io.paysky.data.network.ApiResponseListener;
import io.paysky.ui.dialog.InfoDialog;
import io.paysky.util.AppCache;
import io.paysky.util.HashGenerator;
import okhttp3.ResponseBody;

/**
 * Created by Paysky-202 on 5/27/2018.
 */

public class PayActivityManager {
    private PayActivity payActivity;

    PayActivityManager(PayActivity payActivity) {
        this.payActivity = payActivity;
    }

    public void getMerchantData(long merchantId, long terminalId) {
        if (!payActivity.isInternetAvailable()) {
            payActivity.showNoInternetDialog();
            return;
        }
        payActivity.showProgress();
        MerchantDataRequest merchantDataRequest = new MerchantDataRequest();
        merchantDataRequest.merchantId = merchantId;
        merchantDataRequest.terminalId = terminalId;
        TreeMap<String, Object> hashing = new TreeMap<>();
        hashing.put("CardAcceptorIDcode", merchantId);
        hashing.put("CardAcceptorTerminalID", terminalId);
        merchantDataRequest.secureHash = HashGenerator.encode(hashing);
        //  Log.d("data", new Gson().toJson(merchantDataRequest));
        ApiConnection.getMerchantData(merchantDataRequest, new ApiResponseListener<MerchantDataResponse>() {
            @Override
            public void onSuccess(MerchantDataResponse response) {
                payActivity.dismissProgress();
                if (response.success) {
                    AppCache.saveMerchantData(payActivity, response);
                    payActivity.loadMerchantDataSuccess(response);
                } else {
                    // show error dialog.
                    new InfoDialog(payActivity)
                            .setDialogTitle(R.string.error)
                            .setDialogText(response.message)
                            .setButtonText(R.string.ok).showDialog();
                }
            }

            @Override
            public void onFail(Throwable error) {
                error.printStackTrace();
                payActivity.dismissProgress();
                new InfoDialog(payActivity)
                        .setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again)
                        .setButtonText(R.string.ok).showDialog();
            }
        });
    }

    public void getTerminalConfig(long terminalId) {
        if (!payActivity.isInternetAvailable()) {
            payActivity.showNoInternetDialog();
            return;
        }
        payActivity.showProgress();
        ApiConnection.getTerminalConfig(String.valueOf(terminalId), new ApiResponseListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                payActivity.dismissProgress();
                try {
                    AppCache.saveTerminalConfig(payActivity, response.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Throwable error) {
                // show error.
                payActivity.dismissProgress();
                error.printStackTrace();
                new InfoDialog(payActivity).setDialogTitle(R.string.error)
                        .setDialogText(R.string.error_try_again).setButtonText(R.string.ok).showDialog();
            }
        });
    }


}
