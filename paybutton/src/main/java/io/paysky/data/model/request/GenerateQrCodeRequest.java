package io.paysky.data.model.request;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class GenerateQrCodeRequest{

/*	@SerializedName("LoyaltyNumber")
	public String loyaltyNumber;

	@SerializedName("PromptBillNumber")
	public boolean promptBillNumber;

	@SerializedName("PurposeOfTransaction")
	public String purposeOfTransaction;*/

	/*@SerializedName("ConsumerMobileNumber")
	public String consumerMobileNumber;*/

	@SerializedName("StoreLabel")
	public String storeLabel;

	@SerializedName("DateTimeLocalTrxn")
	public String dateTimeLocalTrxn;

	@SerializedName("Amount")
	public String amount;

/*	@SerializedName("PromptCustomerLabel")
	public boolean promptCustomerLabel;*/

	@SerializedName("MerchantId")
	public String merchantId;

/*	@SerializedName("OrderId")
	public String orderId;*/

/*	@SerializedName("PromptLoyaltyNumber")
	public boolean promptLoyaltyNumber;

	@SerializedName("PromptConsumerMobileNumber")
	public boolean promptConsumerMobileNumber;*/

	@SerializedName("SecureHash")
	public String secureHash;

/*	@SerializedName("CustomerLabel")
	public String customerLabel;*/

	@SerializedName("ClientId")
	public long clientId;

	@SerializedName("TerminalId")
	public String terminalId;

/*	@SerializedName("Tip")
	public boolean tip;

	@SerializedName("PromptPurposeOfTransaction")
	public boolean promptPurposeOfTransaction;

	@SerializedName("PromptStoreLabel")
	public boolean promptStoreLabel;*/
}