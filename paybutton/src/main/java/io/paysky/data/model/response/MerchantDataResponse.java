package io.paysky.data.model.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class MerchantDataResponse{

	@SerializedName("MerchantCity")
	public String merchantCity;

	@SerializedName("MerchantName")
	public String merchantName;

	@SerializedName("Message")
	public String message;

	@SerializedName("MerchantBankLogo")
	public String merchantBankLogo;

	@SerializedName("MerchantCategoryCode")
	public String merchantCategoryCode;

	@SerializedName("TerminalPublicKey")
	public String terminalPublicKey;

	@SerializedName("IsTahweel")
	public boolean isTahweel;

	@SerializedName("CountryCode")
	public String countryCode;

	@SerializedName("IsCard")
	public boolean isCard;

	@SerializedName("Success")
	public boolean success;
}