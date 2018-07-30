package io.paysky.util;

import android.content.Context;

import io.paysky.data.model.response.MerchantDataResponse;

import static io.paysky.util.AppCache.CacheKeys.MERCHANT_DATA;
import static io.paysky.util.AppCache.CacheKeys.TERMINAL_CONFIG;


/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class AppCache {

    public static MerchantDataResponse getMerchantData(Context context) {
        return PrefsUtils.get(context, MERCHANT_DATA, MerchantDataResponse.class);
    }

    public static void saveMerchantData(Context context, MerchantDataResponse merchantDataResponse) {
        PrefsUtils.saveAsJson(context, MERCHANT_DATA, merchantDataResponse);
    }


    protected interface CacheKeys {
        String MERCHANT_DATA = "merchant_data";
        String TERMINAL_CONFIG = "terminal_config";
    }

    public static String getTerminalConfig(Context context) {
        return PrefsUtils.get(context, TERMINAL_CONFIG);
    }

    public static void saveTerminalConfig(Context context, String config) {
        PrefsUtils.save(context, TERMINAL_CONFIG, config);
    }

}
