package io.paysky.ui.base;

import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.SuccessfulWalletTransaction;

/**
 * Created by Paysky-202 on 5/16/2018.
 */

public interface PaymentTransactionListener {

    void successCardTransaction(SuccessfulCardTransaction cardTransaction);

    void successWalletTransaction(SuccessfulWalletTransaction walletTransaction);

    void setFailTransactionError(Throwable error);
}
