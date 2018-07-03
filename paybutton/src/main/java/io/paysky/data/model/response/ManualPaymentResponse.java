package io.paysky.data.model.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ManualPaymentResponse{

	@SerializedName("ExchangeRate")
	public String exchangeRate;

	@SerializedName("MWMessage")
	public String mWMessage;

	@SerializedName("Message")
	public String message;

	@SerializedName("SalesTotalAmount")
	public String salesTotalAmount;

	@SerializedName("DateAction")
	public String dateAction;

	@SerializedName("BatchID")
	public String batchID;

	@SerializedName("ActionCode")
	public String actionCode;

	@SerializedName("MerchantType")
	public String merchantType;

	@SerializedName("SalesTotalCount")
	public String salesTotalCount;

	@SerializedName("TransactionNo")
	public String transactionNo;

	@SerializedName("HostID")
	public int hostID;

	@SerializedName("ForeignCurrencyCode")
	public String foreignCurrencyCode;

	@SerializedName("RefundTotalCount")
	public String refundTotalCount;

	@SerializedName("CurrencyCode")
	public String currencyCode;

	@SerializedName("SignatureRequired")
	public boolean signatureRequired;

	@SerializedName("MerchantName")
	public String merchantName;

	@SerializedName("AcquirerCode")
	public String acquirerCode;

	@SerializedName("MessageTypeID")
	public String messageTypeID;

	@SerializedName("DateEffective")
	public String dateEffective;

	@SerializedName("SecurityRelatedControlInfo")
	public String securityRelatedControlInfo;

	@SerializedName("DataRecord")
	public String dataRecord;

	@SerializedName("field63")
	public String field63;

	@SerializedName("MWActionCode")
	public String mWActionCode;

	@SerializedName("ICCardSystemRelatedData")
	public String iCCardSystemRelatedData;

	@SerializedName("POSEntryMode")
	public String pOSEntryMode;

	@SerializedName("RefundTotalAmount")
	public String refundTotalAmount;

	@SerializedName("DateTimeLocalTrxn")
	public String dateTimeLocalTrxn;

	@SerializedName("AmountTrxn")
	public String amountTrxn;

	@SerializedName("FunctionCode")
	public String functionCode;

	@SerializedName("CardAcceptorIDcode")
	public String cardAcceptorIDcode;

	@SerializedName("Filename")
	public String filename;

	@SerializedName("POSCondCode")
	public String pOSCondCode;

	@SerializedName("CaptureDate")
	public String captureDate;

	@SerializedName("ProcessingCode")
	public String processingCode;

	@SerializedName("ApprovalCode")
	public String approvalCode;

	@SerializedName("CardAcceptorTerminalID")
	public String cardAcceptorTerminalID;

	@SerializedName("MWInternalMessage")
	public String mWInternalMessage;

	@SerializedName("RetrievalRefNr")
	public String retrievalRefNr;

	@SerializedName("SystemTraceNr")
	public String systemTraceNr;

	@SerializedName("versions")
	public String versions;

	@SerializedName("TxnResponseCode")
	public String txnResponseCode;

	@SerializedName("BatchStatus")
	public String batchStatus;

	@SerializedName("MarginRatePercentage")
	public String marginRatePercentage;

	@SerializedName("PAN")
	public String pAN;

	@SerializedName("B62")
	public String b62;
}