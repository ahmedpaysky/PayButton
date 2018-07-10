package io.paysky.data.network.request.magnetic;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by PaySky-3 on 8/8/2017.
 */

public class PointOfSale  implements Serializable {


   
    public String SecureHash;


    public String getSecureHash() {
        return SecureHash;
    }

    public void setSecureHash(String secureHash) {
        this.SecureHash = secureHash;
    }


   
    private String PAN;
   
    private String transactionType;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    public String getPAN() {
        return this.PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

   
    private String DateExpiration;

    public String getDateExpiration() {
        return this.DateExpiration;
    }

    public void setDateExpiration(String DateExpiration) {
        this.DateExpiration = DateExpiration;
    }

   
    private String ProcessingCode;

    public String getProcessingCode() {
        return this.ProcessingCode;
    }

    public void setProcessingCode(String ProcessingCode) {
        this.ProcessingCode = ProcessingCode;
    }

   
    private String DateTimeLocalTrxn;

    public String getDateTimeLocalTrxn() {
        return this.DateTimeLocalTrxn;
    }

    public void setDateTimeLocalTrxn(String DateTimeLocalTrxn) {
        this.DateTimeLocalTrxn = DateTimeLocalTrxn;
    }

   
    private String AmountTrxn;

    public String getAmountTrxn() {
        return this.AmountTrxn;
    }

    public void setAmountTrxn(String AmountTrxn) {
        this.AmountTrxn = AmountTrxn;
    }

   
    private String CurrencyCodeTrxn;

    public String getCurrencyCodeTrxn() {
        return this.CurrencyCodeTrxn;
    }

    public void setCurrencyCodeTrxn(String CurrencyCodeTrxn) {
        this.CurrencyCodeTrxn = CurrencyCodeTrxn;
    }

   
    private String POSEntryMode;




    public String getPOSEntryMode() {
        return this.POSEntryMode;
    }

    public void setPOSEntryMode(String POSEntryMode) {
        this.POSEntryMode = POSEntryMode;
    }

   
    private String CardAcceptorTerminalID;

    public String getCardAcceptorTerminalID() {
        return this.CardAcceptorTerminalID;
    }

    public void setCardAcceptorTerminalID(String CardAcceptorTerminalID) {
        this.CardAcceptorTerminalID = CardAcceptorTerminalID;
    }

   
    private String MessageTypeID;

    public String getMessageTypeID() {
        return this.MessageTypeID;
    }

    public void setMessageTypeID(String MessageTypeID) {
        this.MessageTypeID = MessageTypeID;
    }

   
    private int HostID;

    public int getHostID() {
        return this.HostID;
    }

    public void setHostID(int HostID) {
        this.HostID = HostID;
    }

   
    private String cvv2;

    public String getCvv2() {
        return this.cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

   
    private String URN;

    public String getURN() {
        return this.URN;
    }

    public void setURN(String URN) {
        this.URN = URN;
    }

   
    private String CardAcceptorIDcode;

    public String getCardAcceptorIDcode() {
        return this.CardAcceptorIDcode;
    }

    public void setCardAcceptorIDcode(String CardAcceptorIDcode) {
        this.CardAcceptorIDcode = CardAcceptorIDcode;
    }

   
    private String SystemTraceNr;

    public String getSystemTraceNr() {
        return this.SystemTraceNr;
    }

    public void setSystemTraceNr(String SystemTraceNr) {
        this.SystemTraceNr = SystemTraceNr;
    }

   
    private String MobileNo;

    public String getMobileNo() {
        return this.MobileNo;
    }

    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }

   
    private String TransactionNo;

    public String getTransactionNo() {
        return this.TransactionNo;
    }

    public void setTransactionNo(String TransactionNo) {
        this.TransactionNo = TransactionNo;
    }

   
    private boolean ISFromPOS = true;

    public boolean getISFromPOS() {
        return this.ISFromPOS;
    }

    public void setISFromPOS(boolean ISFromPOS) {
        this.ISFromPOS = ISFromPOS;
    }
    
    
    private String Track2Data;

    public String getTrack2Data() {
        return this.Track2Data;
    }

    public void setTrack2Data(String Track2Data) {
        this.Track2Data = Track2Data;
    }

   
    private String ICCardSystemRelatedData;

    public String getICCardSystemRelatedData() {
        return this.ICCardSystemRelatedData;
    }

    public void setICCardSystemRelatedData(String ICCardSystemRelatedData) {
        this.ICCardSystemRelatedData = ICCardSystemRelatedData;
    }
}

