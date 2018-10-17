package io.paysky.data.event;

import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.SuccessfulWalletTransaction;

/**
 * Created by Paysky-202 on 5/16/2018.
 */

public class PaymentStatusEvent {


    public SuccessfulCardTransaction cardTransaction;
    public SuccessfulWalletTransaction walletTransaction;


    public Throwable failException;
}
