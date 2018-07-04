package io.paysky.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paysky.data.model.response.terminalconfig.config.DEVICE;

/**
 * Created by Paysky-202 on 5/10/2018.
 */

public class AppUtils {

    public static void register(Object o) {
        EventBus.getDefault().register(o);
    }

    public static void unregister(Object o) {
        EventBus.getDefault().unregister(o);
    }

    public static boolean isRegister(Object o) {
        return EventBus.getDefault().isRegistered(o);
    }

    public static void sendEvent(Object o) {
        EventBus.getDefault().post(o);
    }


    public static boolean isInternetAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return (connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null)
                != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static ProgressDialog createProgressDialog(Context context, @StringRes int text) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(text));
        return progressDialog;
    }

    public static String getDateTimeLocalTrxn() {

        int arg1 = Calendar.getInstance().get(Calendar.YEAR);

        String arg1s = "" + arg1;
        arg1s = arg1s.substring(2, 4);

        int arg2 = Calendar.getInstance().get(Calendar.MONTH);

        int arg3 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        int arg4 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        int arg5 = Calendar.getInstance().get(Calendar.MINUTE);

        int arg6 = Calendar.getInstance().get(Calendar.SECOND);

        int arg7 = Calendar.getInstance().get(Calendar.MILLISECOND);
        arg2 = arg2 + 1;

        String nowData = "" + new StringBuilder().append(arg1s).append("")

                .append((arg2 < 10 ? "0" + arg2 : arg2)).append("").append((arg3 < 10 ? "0" + arg3 : arg3))

                .append((arg4 < 10 ? "0" + arg4 : arg4)).append((arg5 < 10 ? "0" + arg5 : arg5))

                .append((arg6 < 10 ? "0" + arg6 : arg6)).append("" + arg7);
        return nowData;
    }

    public static String generateRandomNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (index < 6) {
            stringBuilder.append(1 + (int) (Math.random() * 49));
            index++;
        }

        return stringBuilder.toString();
    }


    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public static boolean validEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static DEVICE getTerminalConfig(Context context) {
        String terminalConfig = AppCache.getTerminalConfig(context);
        Serializer serializer = new Persister();
        try {
            return serializer.read(DEVICE.class, terminalConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validPhone(String phone) {
        return !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public static void openActivity(Context context, Class<? extends Activity> activity, Bundle bundle, boolean clearStack) {
        Intent i = new Intent(context, activity);
        if (bundle != null) {
            i.putExtras(bundle);
        }
        if (clearStack) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(i);
        ((Activity) context).finish();
    }

    public static String convertToEnglishDigits(String value) {

        return value.replace("١", "1").replace("٢", "2")
                .replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("7", "٧").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5")
                .replace("۶", "6").replace("۷", "7").replace("۸", "8")
                .replace("۹", "9").replace("۰", "0").replace("٫", ".");
    }

    public static boolean isPOSDevice() {
        String model = Build.MODEL;
        return model.equals("SQ27");
    }
}
