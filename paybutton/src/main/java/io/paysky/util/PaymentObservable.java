package io.paysky.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.paysky.data.event.PaymentStatusEvent;

/**
 * Created by Paysky-202 on 6/5/2018.
 */

public class PaymentObservable {

    private static List<WeakReference<PaymentObserver>> paymentObservers = new ArrayList<>();


    public static void addObserver(PaymentObserver paymentObserver) {
        paymentObservers.add(new WeakReference<PaymentObserver>(paymentObserver));
    }


    public static void sendPaymentStatus(PaymentStatusEvent paymentStatusEvent) {
        for (WeakReference<PaymentObserver> observerWeakReference : paymentObservers) {
            PaymentObserver paymentObserver = observerWeakReference.get();
            if (paymentObserver != null) {
                paymentObserver.sendPaymentStatus(paymentStatusEvent);
            }
        }
    }


    public static void removeObserver(PaymentObserver paymentObserver) {
        int index = -1;
        for (WeakReference<PaymentObserver> observerWeakReference : paymentObservers) {
            PaymentObserver observer = observerWeakReference.get();
            if (observer != null && observer == paymentObserver) {
                break;
            }
            index++;
        }
        if (index > -1) {
            paymentObservers.remove(index);
        }
    }

}
