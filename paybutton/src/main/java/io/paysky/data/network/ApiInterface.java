package io.paysky.data.network;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Paysky-202 on 5/17/2018.
 */

public interface ApiInterface {

    @POST(ApiLinks.MERCHANT_DATA)
    Call<MerchantDataResponse> getMerchantData(@Body MerchantDataRequest merchantDataRequest);

    @GET(ApiLinks.TERMINAL_CONFIG)
    Call<ResponseBody> getTerminalConfig(@Path("terminalId") String terminalId);

    @POST(ApiLinks.SEND_RECEIPT_BY_MAIL)
    Call<SendReceiptByMailResponse> sendReceiptByMail(@Body SendReceiptByMailRequest mailRequest);

    @POST(ApiLinks.CHECK_PAYMENT_STATUS)
    Call<TransactionStatusResponse> checkTransactionStatus(@Body TransactionStatusRequest request);

    @POST(ApiLinks.GENERATE_QRCODE)
    Call<GenerateQrCodeResponse> generateQrCode(@Body QrGenratorRequest request);

    @POST(ApiLinks.EXECUTE_PAYMENT)
    Call<ManualPaymentResponse> executeManualPayment(@Body ManualPaymentRequest paymentRequest);

    @POST(ApiLinks.SMS_PAYMENT)
    Call<SmsPaymentResponse> requestToPay(@Body
                                                  SmsPaymentRequest request);

    @POST(ApiLinks.MAGNETIC_PAYMENT)
    Call<MigsResonse> payWithMagneticCard(@Body MigsRequest migsRequest);
}
