package io.paysky.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.paysky.data.model.request.QrGenratorRequest;
import io.paysky.data.network.request.magnetic.MigsRequest;

/**
 * Created by Ahmed AL Agamy on 8/27/2017.
 */

public class HashGenerator {
    private static final String KEY = "BC63D941AB9DFCBA2065086AD2451EF4";

    private static String encode(String key, String data) throws Exception {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8")))).toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | DecoderException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String encode(String data) {
        try {
            return encode(KEY, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(TreeMap<String, Object> data) {

        try {
            return encode(getHashKeys(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String hashEmail(TreeMap<String, Object> data) {
        try {
            String HASH_CODE = "583259473546523138394258464F5250415934534B59";
            return encode(HASH_CODE, getHashKeys(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hashCheckPaymentStatus(TreeMap<String, Object> data) {
        try {
            String HASH_CODE = "583259473546523138394258464F5250415934534B59";
            return encode(HASH_CODE, getHashKeys(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createHashData(QrGenratorRequest migsRequest) {
        String HASH_CODE = "583259473546523138394258464F5250415934534B59";
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(migsRequest);
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            Iterator<String> keys = jsonObject.keys();
            TreeSet<String> tree = new TreeSet<>();
            while (keys.hasNext()) {
                String keyName = keys.next();
                if (keyName.equals("AmountTrxn") //|| keyName.equals("CardAcceptorIDcode") || keyName.equals("CardAcceptorTerminalID")
                        || keyName.equals("DateTimeLocalTrxn")
                        || keyName.equals("MerchantId")
                        || keyName.equals("TerminalId")
                        || keyName.equals("ClientId")

                        || keyName.equals("MessageTypeID") ||
                        keyName.equals("POSEntryMode") || keyName.equals("FetchType")
                        || keyName.equals("ProcessingCode") || keyName.equals("SystemTraceNr")) {
                    tree.add(keyName);
                }

            }
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;
            for (String key : tree) {
                stringBuilder.append(key).append("=").append(jsonObject.get(key));
                index++;
                if (index < tree.size()) {
                    stringBuilder.append("&");
                }
            }

            Log.d("text", stringBuilder.toString());
            return encode(HASH_CODE, stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashKeys(TreeMap<String, Object> data) {
        Set<Map.Entry<String, Object>> entries = data.entrySet();
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, Object> entry : entries) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            index++;
            if (index == data.size()) {
                break;
            } else {
                stringBuilder.append("&");
            }
        }
        try {
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String createHashData(MigsRequest migsRequest) {
        String jsonRequest = new Gson().toJson(migsRequest.getPointOfSale());
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            Iterator<String> keys = jsonObject.keys();
            TreeSet<String> tree = new TreeSet<>();
            while (keys.hasNext()) {
                String keyName = keys.next();
                if (keyName.equals("AmountTrxn") || keyName.equals("CardAcceptorIDcode")
                        || keyName.equals("CardAcceptorTerminalID") || keyName.equals("DateTimeLocalTrxn")
                        || keyName.equals("MessageTypeID") || keyName.equals("POSEntryMode")
                        || keyName.equals("ProcessingCode") || keyName.equals("SystemTraceNr")) {
                    tree.add(keyName);
                }

            }
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;
            for (String key : tree) {
                stringBuilder.append(key).append("=").append(jsonObject.get(key));
                index++;
                if (index < tree.size()) {
                    stringBuilder.append("&");
                }
            }
            return stringBuilder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
