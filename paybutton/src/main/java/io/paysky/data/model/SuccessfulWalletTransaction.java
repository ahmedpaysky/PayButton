package io.paysky.data.model;

public class SuccessfulWalletTransaction {

    public String Message;
    public boolean Success;
    public boolean IsPaid;
    public String SystemReference;
    public String NetworkReference;
    public String MerchantReference;
    public String Payer;
    public String PayerName;

    @Override
    public String toString() {
        return "SuccessfulWalletTransaction{" +
                "Message='" + Message + '\'' +
                ", Success=" + Success +
                ", IsPaid=" + IsPaid +
                ", SystemReference='" + SystemReference + '\'' +
                ", NetworkReference='" + NetworkReference + '\'' +
                ", MerchantReference='" + MerchantReference + '\'' +
                ", Payer='" + Payer + '\'' +
                ", PayerName='" + PayerName + '\'' +
                ", TxnDate='" + TxnDate + '\'' +
                ", AmountTrxn='" + AmountTrxn + '\'' +
                '}';
    }

    public String TxnDate;
    public String AmountTrxn;
}
