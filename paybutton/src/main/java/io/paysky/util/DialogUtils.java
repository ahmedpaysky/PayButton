package io.paysky.util;

import android.content.Context;

import com.example.paybutton.R;

import io.paysky.ui.dialog.InfoDialog;

/**
 * Created by Paysky-202 on 5/27/2018.
 */

public class DialogUtils {

    public static void showTermsAndConditionsDialog(Context context) {
        new InfoDialog(context).setDialogTitle(R.string.terms_conditions)
                .setDialogText(R.string.terms_conditions_text)
                .setButtonText(R.string.ok).showDialog();
    }
}
