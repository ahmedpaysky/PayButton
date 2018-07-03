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
        String PAY_AMOUNT ="pay_amount";
    }


    interface TransactionChannelName{
        String CARD = "Card";
        String TAHWEEL ="Tahweel";
    }
}
