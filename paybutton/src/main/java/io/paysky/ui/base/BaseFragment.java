package io.paysky.ui.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.ui.dialog.InfoDialog;
import io.paysky.util.AppUtils;

/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class BaseFragment extends Fragment {


    public String getText(TextView textView) {
        return textView.getText().toString().trim();
    }


    public boolean isInternetAvailable() {
        return AppUtils.isInternetAvailable(getActivity());
    }

    public void showNoInternetDialog() {
        new InfoDialog(getActivity()).setDialogTitle(R.string.error)
                .setDialogText(R.string.check_internet_connection)
                .setButtonText(R.string.ok).showDialog();
    }

    public void showUsageAgreementDialog(){

    }

    public boolean isEmpty(String text){
        return text.isEmpty();
    }


    public Context getFragmentContext(){
        return getActivity();
    }
}
