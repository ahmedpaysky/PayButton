package io.paysky.data.model.request;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ManualPaymentRequest{

	@SerializedName("MobileNo")
	public String mobileNo;

	@SerializedName("cvv2")
	public String cvv2;

	@SerializedName("POSEntryMode")
	public String pOSEntryMode;

	@SerializedName("DateTimeLocalTrxn")
	public String dateTimeLocalTrxn;

	@SerializedName("AmountTrxn")
	public String amountTrxn;

	@SerializedName("CardAcceptorIDcode")
	public String cardAcceptorIDcode;

	@SerializedName("ProcessingCode")
	public String processingCode;

	@SerializedName("HostID")
	public int hostID;

	@SerializedName("CardAcceptorTerminalID")
	public String cardAcceptorTerminalID;

	@SerializedName("ISFromPOS")
	public boolean iSFromPOS;

	@SerializedName("DateExpiration")
	public String dateExpiration;

	@SerializedName("SystemTraceNr")
	public String systemTraceNr;

	@SerializedName("MessageTypeID")
	public String messageTypeID;

	@SerializedName("CurrencyCodeTrxn")
	public String currencyCodeTrxn;

	@SerializedName("PAN")
	public String pAN;

	@SerializedName("SecureHash")
	public String secureHash;
}