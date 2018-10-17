package io.paysky.util;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

public class KeyStoreUtils {

    private KeyStore keyStore;
    private String alias = "PAYSKYUPG";
    private static KeyStoreUtils instance = null;


    public static KeyStoreUtils getInstance() {

        if (instance == null) {
            instance = new KeyStoreUtils();
        }


        return instance;

    }


    private KeyStoreUtils() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }


    public String decryptString(String cipherText) {
        try {


            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            ;

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");


            return finalText;
        } catch (Exception e) {
            Log.e("saved-dec", Log.getStackTraceString(e));
        }


        return "";
    }

    public void deleteKey(final String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

    }

    public String encryptString(String jsonObject) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)
                    keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            // Encrypt the text

            if (jsonObject.isEmpty()) {
                //  Toast.makeText(this, "Enter text in the 'Initial Text' widget", Toast.LENGTH_LONG).show();
                return "";
            }
            SecretKey key = (SecretKey) keyStore.getKey(alias, null);
            Cipher input = Cipher.getInstance("AES/ECB/PKCS5Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
            cipherOutputStream.write(jsonObject.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception ignored) {

        }

        return null;
    }

}
