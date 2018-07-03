package io.paysky.data.model.request;


import java.io.Serializable;

import io.paysky.util.AppUtils;

/**
 * Created by amrel on 17/04/2018.
 */

public class QrGenratorRequest implements Serializable {

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getEmailTo() {
        return EmailTo;
    }

    public void setEmailTo(String emailTo) {
        EmailTo = emailTo;
    }

    public String getTransactionChannel() {
        return TransactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        TransactionChannel = transactionChannel;
    }


    /**
     * ClientId : 1234
     * DateTimeLocalTrxn : 201808080404
     * MerchantId : 229401
     * SecureHash : 7777777777777777777777
     * TerminalId : 229300402
     * Amount : 120
     * ConsumerMobileNumber : 0100000001
     * CustomerLabel : customer label
     * LoyaltyNumber : 01000000010
     * OrderId : QWER123
     * PurposeOfTransaction : Purpose Of Transaction
     * StoreLabel : Store Label
     */


    private String fees = "0";
    private String total = "0";
    private String MobileNumber;
    private String Details;
    private String NewPassword;

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public String getOldPassword() {
        return OldPassword;
    }

    public void setOldPassword(String oldPassword) {
        OldPassword = oldPassword;
    }

    private String OldPassword;
    private String UserId;

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    private String TransactionId;
    private String EmailTo = "";
    private String RequestTypeId = "";
    private String SeverityTypeId = "";
    private String Comments = "";

    public String getRequestTypeId() {
        return RequestTypeId;
    }

    public void setRequestTypeId(String requestTypeId) {
        RequestTypeId = requestTypeId;
    }

    public String getSeverityTypeId() {
        return SeverityTypeId;
    }

    public void setSeverityTypeId(String severityTypeId) {
        SeverityTypeId = severityTypeId;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    private String TransactionChannel;
    private String RefundReason;
    private boolean Tip = false;

    public boolean isTip() {
        return Tip;
    }

    public void setTip(boolean tip) {
        Tip = tip;
    }

    private String FBToken;
    int CurrentPage = 0;

    public String getRefundReason() {
        return RefundReason;
    }

    public void setRefundReason(String refundReason) {
        RefundReason = refundReason;
    }


    public int getCurrentPage() {
        return CurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        CurrentPage = currentPage;
    }

    public String getFBToken() {
        return FBToken;
    }

    public void setFBToken(String FBToken) {
        this.FBToken = FBToken;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getTotal() {
        return getAmount();
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private int ClientId = 204;
    private String DateTimeLocalTrxn = "201808080404";
    private String ISOQR;
    private String TxnId;

    public String getISOQR() {
        return ISOQR;
    }

    public void setISOQR(String ISOQR) {
        this.ISOQR = ISOQR;
    }

    public String getTxnId() {
        return TxnId;
    }

    public void setTxnId(String txnId) {
        TxnId = txnId;
    }

    private String MerchantId;
    private String SecureHash;
    private String TerminalId;
    private String Amount;
    private String ConsumerMobileNumber = "";
    private String CustomerLabel = "";
    private String LoyaltyNumber = "";
    private String OrderId = "";
    private String PurposeOfTransaction = "";
    private String StoreLabel = "";

    public int getClientId() {
        return ClientId;
    }

    public void setClientId(int ClientId) {
        this.ClientId = ClientId;
    }

    public String getDateTimeLocalTrxn()

    {

        return AppUtils.getDateTimeLocalTrxn();
    }

    public void setDateTimeLocalTrxn(String DateTimeLocalTrxn) {
        this.DateTimeLocalTrxn = DateTimeLocalTrxn;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String MerchantId) {
        this.MerchantId = MerchantId;
    }

    public String getSecureHash() {
        return SecureHash;
    }

    public void setSecureHash(String SecureHash) {
        this.SecureHash = SecureHash;
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String TerminalId) {
        this.TerminalId = TerminalId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getConsumerMobileNumber() {
        return ConsumerMobileNumber;
    }

    public void setConsumerMobileNumber(String ConsumerMobileNumber) {
        this.ConsumerMobileNumber = ConsumerMobileNumber;
    }

    public String getCustomerLabel() {
        return CustomerLabel;
    }

    public void setCustomerLabel(String CustomerLabel) {
        this.CustomerLabel = CustomerLabel;
    }

    public String getLoyaltyNumber() {
        return LoyaltyNumber;
    }

    public void setLoyaltyNumber(String LoyaltyNumber) {
        this.LoyaltyNumber = LoyaltyNumber;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }

    public String getPurposeOfTransaction() {
        return PurposeOfTransaction;
    }

    public void setPurposeOfTransaction(String PurposeOfTransaction) {
        this.PurposeOfTransaction = PurposeOfTransaction;
    }

    public String getStoreLabel() {
        return StoreLabel;
    }

    public void setStoreLabel(String StoreLabel) {
        this.StoreLabel = StoreLabel;
    }


    boolean PromptConsumerMobileNumber = false;
    boolean PromptStoreLabel = false;
    boolean PromptLoyaltyNumber = false;
    boolean PromptCustomerLabel = false;
    boolean PromptPurposeOfTransaction = false;
    boolean PromptBillNumber = false;

    public boolean isPromptConsumerMobileNumber() {
        return PromptConsumerMobileNumber;
    }

    public void setPromptConsumerMobileNumber(boolean promptConsumerMobileNumber) {
        PromptConsumerMobileNumber = promptConsumerMobileNumber;
    }

    public boolean isPromptStoreLabel() {
        return PromptStoreLabel;
    }

    public void setPromptStoreLabel(boolean promptStoreLabel) {
        PromptStoreLabel = promptStoreLabel;
    }

    public boolean isPromptLoyaltyNumber() {
        return PromptLoyaltyNumber;
    }

    public void setPromptLoyaltyNumber(boolean promptLoyaltyNumber) {
        PromptLoyaltyNumber = promptLoyaltyNumber;
    }

    public boolean isPromptCustomerLabel() {
        return PromptCustomerLabel;
    }

    public void setPromptCustomerLabel(boolean promptCustomerLabel) {
        PromptCustomerLabel = promptCustomerLabel;
    }

    public boolean isPromptPurposeOfTransaction() {
        return PromptPurposeOfTransaction;
    }

    public void setPromptPurposeOfTransaction(boolean promptPurposeOfTransaction) {
        PromptPurposeOfTransaction = promptPurposeOfTransaction;
    }

    public boolean isPromptBillNumber() {
        return PromptBillNumber;
    }

    public void setPromptBillNumber(boolean promptBillNumber) {
        PromptBillNumber = promptBillNumber;
    }
}
