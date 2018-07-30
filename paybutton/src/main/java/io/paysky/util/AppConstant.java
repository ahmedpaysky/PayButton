package io.paysky.util;


public interface AppConstant {

    interface BundleKeys {
        String TRANSACTION_CHANNEL = "transaction_channel";
        String TRANSACTION_ID = "transaction_id";
        String REFERENCE_NUMBER = "reference_number";
        String TERMINAL_ID = "terminal_id";
        String MERCHANT_ID = "merchant_id";
        String RECEIVER_MAIL = "receiver_mail";
        String AUTH_NUMBER = "auth_number";
        String PAY_AMOUNT = "pay_amount";
        String ENABLE_MANUAL = "manual";
        String ENABLE_MAGNETIC = "magnetic";
        String ENABLE_QR = "qr_reader";
        String DEFAULT_PAYMENT = "default_payment";
        String SERVER_LINK = "payment_link";
        String CARD_HOLDER_NAME = "card_holder_name";
        String CARD_NUMBER = "card_number";
        String PAYMENT_DATA = "payment_data";
        String RECEIPT = "receipt";
    }


    interface TransactionChannelName {
        String CARD = "Card";
        String TAHWEEL = "Tahweel";
    }


    interface PaymentMethods {
        int MANUAL = 1, MAGNETIC = 2, QR_READER = 3;
    }
}
