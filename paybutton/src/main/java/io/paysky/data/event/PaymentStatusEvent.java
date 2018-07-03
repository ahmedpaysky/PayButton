package io.paysky.data.event;

/**
 * Created by Paysky-202 on 5/16/2018.
 */

public class PaymentStatusEvent {
    public String referenceNumber, responseCode, authorizationCode;
    public Throwable failException;
}
