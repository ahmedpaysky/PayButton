package io.paysky.data.model.request;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class MerchantDataRequest{

	@SerializedName("SecureHash")
	public String secureHash;

	@SerializedName("TerminalId")
	public long terminalId;

	@SerializedName("MerchantId")
	public long merchantId;
}