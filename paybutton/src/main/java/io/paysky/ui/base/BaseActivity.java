package io.paysky.ui.base;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.ui.dialog.InfoDialog;
import io.paysky.util.AppUtils;
import io.paysky.util.ToastUtils;

/**
 * Created by Paysky-202 on 5/13/2018.
 */

public class BaseActivity extends AppCompatActivity {


    public boolean isInternetAvailable() {
        return AppUtils.isInternetAvailable(this);
    }

    public void showToast(@StringRes int text) {
        ToastUtils.showToast(this, text);
    }


    public void showToast(String text) {
        ToastUtils.showToast(this, text);
    }

    public void showHtmlText(TextView textView, @StringRes int text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(getString(text), Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(getString(text)));
        }
    }

    public void showHtmlText(TextView textView, String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }

    protected void replaceFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean addOldToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        fragmentTransaction.replace(R.id.fragment_frame, fragment);
        if (addOldToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentTransaction.commit();
    }

    public void showNoInternetDialog() {
        new InfoDialog(this).setDialogTitle(R.string.error)
                .setButtonText(R.string.check_internet_connection)
                .setButtonText(R.string.ok).showDialog();
    }
}
