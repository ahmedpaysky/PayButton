package io.paysky.data.network;

/**
 * Created by Paysky-202 on 5/13/2018.
 */

public interface ApiLinks {

    // note that https is 9006 port, http is 4006 port.
    String MAIN_LINK = "http://197.50.37.116:4006/";
    String MERCHANT_DATA = "Cube/PayLink.svc/api/FetchMerchantDetails";
    String EXECUTE_PAYMENT = "Cube/PayLink.svc/api/ExecuteCardAuthorization";
    String SEND_RECEIPT_BY_MAIL = "Cube/PayLink.svc/api/SendReceiptToEmail";
    String GENERATE_QRCODE = "Cube/PayLink.svc/api/GenerateQR";
    String CHECK_PAYMENT_STATUS = "Cube/PayLink.svc/api/CheckTxnStatus";
    String TERMINAL_CONFIG = "Portal/TerminalConfigurationFile/S{terminalId}.xml";
    String SMS_PAYMENT = "Cube/PayLink.svc/api/RequestToPay";
    String MAGNETIC_PAYMENT = "Cube/mPOSHosting.svc/api/Execute";
}

