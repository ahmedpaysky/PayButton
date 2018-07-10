package io.paysky.data.network;

import com.example.paybutton.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import io.paysky.data.model.request.ManualPaymentRequest;
import io.paysky.data.model.request.MerchantDataRequest;
import io.paysky.data.model.request.QrGenratorRequest;
import io.paysky.data.model.request.SendReceiptByMailRequest;
import io.paysky.data.model.request.SmsPaymentRequest;
import io.paysky.data.model.request.TransactionStatusRequest;
import io.paysky.data.model.response.GenerateQrCodeResponse;
import io.paysky.data.model.response.ManualPaymentResponse;
import io.paysky.data.model.response.MerchantDataResponse;
import io.paysky.data.model.response.SendReceiptByMailResponse;
import io.paysky.data.model.response.SmsPaymentResponse;
import io.paysky.data.model.response.TransactionStatusResponse;
import io.paysky.data.network.request.magnetic.MigsRequest;
import io.paysky.data.network.response.MigsResonse;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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

    public static void getMerchantData(MerchantDataRequest merchantDataRequest, final ApiResponseListener<MerchantDataResponse> listener) {
        createConnection().getMerchantData(merchantDataRequest).enqueue(new Callback<MerchantDataResponse>() {
            @Override
            public void onResponse(Call<MerchantDataResponse> call, Response<MerchantDataResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    onFailure(call, new Exception("fail to connect response code = " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<MerchantDataResponse> call, Throwable t) {
                listener.onFail(t);
            }
        });
    }

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

    public static void generateQrCode(QrGenratorRequest request, final ApiResponseListener<GenerateQrCodeResponse> listener) {
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

    public static void getTerminalConfig(String terminalId, final ApiResponseListener<ResponseBody> listener) {
        createConnection().getTerminalConfig(terminalId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
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
                .baseUrl(ApiLinks.MAIN_LINK)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    public static void createMagneticTransaction(MigsRequest migsRequest, final ApiResponseListener<MigsResonse> listener) {
        createConnection().payWithMagneticCard(migsRequest).enqueue(new Callback<MigsResonse>() {
            @Override
            public void onResponse(Call<MigsResonse> call, Response<MigsResonse> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<MigsResonse> call, Throwable t) {
                listener.onFail(t);
            }
        });
    }
}
