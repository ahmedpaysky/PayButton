package io.paysky.ui.base;

/**
 * Created by Paysky-202 on 5/16/2018.
 */

public interface PaymentTransaction {

    void setSuccessTransactionId(String referenceNumber, String responseCode, String authorizationCode);

    void setFailTransactionError(Throwable error);
}
