package io.paysky.data.network;

import java.util.concurrent.TimeUnit;

import io.paysky.data.model.request.ManualPaymentRequest;
import io.paysky.data.model.request.Process3dTransactionRequest;
import io.paysky.data.model.request.QrGeneratorRequest;
import io.paysky.data.model.request.SendReceiptByMailRequest;
import io.paysky.data.model.request.SmsPaymentRequest;
import io.paysky.data.model.request.TransactionStatusRequest;
import io.paysky.data.model.request.Compose3dsTransactionRequest;
import io.paysky.data.model.response.GenerateQrCodeResponse;
import io.paysky.data.model.response.ManualPaymentResponse;
import io.paysky.data.model.response.MerchantInfoResponse;
import io.paysky.data.model.response.Process3dTransactionResponse;
import io.paysky.data.model.response.SendReceiptByMailResponse;
import io.paysky.data.model.response.SmsPaymentResponse;
import io.paysky.data.model.response.TransactionStatusResponse;
import io.paysky.data.model.request.MerchantInfoRequest;
import io.paysky.data.model.response.Compose3dsTransactionResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class ApiConnection {


    public static void executePayment(ManualPaymentRequest manualPaymentRequest, final ApiResponseListener<ManualPaymentResponse> listener) {
        createConnection().executeManualPayment(manualPaymentRequest)
                .enqueue(new Callback<ManualPaymentResponse>() {
                    @Override
                    public void onResponse(Call<ManualPaymentResponse> call, Response<ManualPaymentResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ManualPaymentResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void sendReceiptByMail(SendReceiptByMailRequest mailRequest, final ApiResponseListener<SendReceiptByMailResponse> listener) {
        createConnection().sendReceiptByMail(mailRequest)
                .enqueue(new Callback<SendReceiptByMailResponse>() {
                    @Override
                    public void onResponse(Call<SendReceiptByMailResponse> call, Response<SendReceiptByMailResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<SendReceiptByMailResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void generateQrCode(QrGeneratorRequest request, final ApiResponseListener<GenerateQrCodeResponse> listener) {
        createConnection().generateQrCode(request)
                .enqueue(new Callback<GenerateQrCodeResponse>() {
                    @Override
                    public void onResponse(Call<GenerateQrCodeResponse> call, Response<GenerateQrCodeResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<GenerateQrCodeResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void checkTransactionPaymentStatus(TransactionStatusRequest request, final ApiResponseListener<TransactionStatusResponse> listener) {
        createConnection().checkTransactionStatus(request)
                .enqueue(new Callback<TransactionStatusResponse>() {
                    @Override
                    public void onResponse(Call<TransactionStatusResponse> call, Response<TransactionStatusResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionStatusResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }


    public static void requestToPay(SmsPaymentRequest smsPaymentRequest, final ApiResponseListener<SmsPaymentResponse> listener) {
        createConnection().requestToPay(smsPaymentRequest)
                .enqueue(new Callback<SmsPaymentResponse>() {
                    @Override
                    public void onResponse(Call<SmsPaymentResponse> call, Response<SmsPaymentResponse> response) {
                        listener.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<SmsPaymentResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }


    private static ApiInterface createConnection() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(ApiLinks.GRAY_LINK)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    public static void getMerchantInfo(MerchantInfoRequest request, final ApiResponseListener<MerchantInfoResponse> listener) {
        createConnection().getMerchantInfo(request)
                .enqueue(new Callback<MerchantInfoResponse>() {
                    @Override
                    public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                        listener.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void compose3dsTransaction(Compose3dsTransactionRequest request, final ApiResponseListener<Compose3dsTransactionResponse> listener) {
        createConnection().compose3dpsTransaction(request)
                .enqueue(new Callback<Compose3dsTransactionResponse>() {
                    @Override
                    public void onResponse(Call<Compose3dsTransactionResponse> call, Response<Compose3dsTransactionResponse> response) {
                        listener.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<Compose3dsTransactionResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void process3dTransaction(Process3dTransactionRequest request, final ApiResponseListener<Process3dTransactionResponse> listener) {
        createConnection().process3dTransaction(request).enqueue(new Callback<Process3dTransactionResponse>() {
            @Override
            public void onResponse(Call<Process3dTransactionResponse> call, Response<Process3dTransactionResponse> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Process3dTransactionResponse> call, Throwable t) {
                listener.onFail(t);
            }
        });
    }

}
