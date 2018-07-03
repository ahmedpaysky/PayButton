package io.paysky.data.model.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class TransactionStatusResponse{

	@SerializedName("ExternalTxnId")
	public String externalTxnId;

	@SerializedName("Message")
	public String message;

	@SerializedName("IsPaid")
	public boolean isPaid;

	@SerializedName("Success")
	public boolean success;
}