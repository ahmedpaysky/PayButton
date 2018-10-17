package io.paysky.ui.activity.payment;

import android.os.Bundle;

import io.paysky.ui.mvp.BaseView;

public interface PayActivityView extends BaseView {

    void makeActivityFullScreen();

    void hideActionBar();

    void showMerchantInfo(String merChantName);

    void showPaidAmount(String amount, String currency);

    void showPaymentBasedOnPaymentOptions(int paymentOptions);

    void showFailedLoadMerchantDataDialog();

    void showCardPaymentFragment(Bundle bundle);

    void showQrPaymentFragment(Bundle bundle);
}
