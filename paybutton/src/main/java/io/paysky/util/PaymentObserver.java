package io.paysky.util;

import io.paysky.data.event.PaymentStatusEvent;

/**
 * Created by Paysky-202 on 6/5/2018.
 */

public interface PaymentObserver {


    void sendPaymentStatus(PaymentStatusEvent paymentStatusEvent);
}
