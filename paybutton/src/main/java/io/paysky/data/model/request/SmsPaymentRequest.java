package io.paysky.data.model.request;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class SmsPaymentRequest{

	@SerializedName("MobileNumber")
	public String mobileNumber;

	@SerializedName("TxnId")
	public long txnId;

	@SerializedName("SecureHash")
	public String secureHash;

	@SerializedName("DateTimeLocalTrxn")
	public String dateTimeLocalTrxn;

	@SerializedName("ClientId")
	public long clientId;

	@SerializedName("TerminalId")
	public String terminalId;

	@SerializedName("MerchantId")
	public String merchantId;

	@SerializedName("ISOQR")
	public String iSOQR;
}