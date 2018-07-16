package io.paysky.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.util.ViewUtil;

/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class InfoDialog extends Dialog implements View.OnClickListener {

    //GUI.
    private TextView dialogTitleTextView;
    private TextView dialogContentTextView;
    private Button dialogButton;
    private DialogButtonClick buttonClickListener;

    public InfoDialog(@NonNull Context context) {
        super(context);
        initView();
    }


    private void initView() {
        View dialogView = ViewUtil.inflateView(getContext(), R.layout.dialog_info);
        dialogTitleTextView = dialogView.findViewById(R.id.dialog_title_textView);
        dialogContentTextView = dialogView.findViewById(R.id.dialog_content_textView);
        dialogButton = dialogView.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
    }


    public InfoDialog setDialogTitle(@StringRes int title) {
        dialogTitleTextView.setText(title);
        return this;
    }

    public InfoDialog setDialogText(@StringRes int content) {
        dialogContentTextView.setText(content);
        return this;
    }

    public InfoDialog setDialogText(String content) {
        dialogContentTextView.setText(content);
        return this;
    }

    public InfoDialog setButtonText(@StringRes int buttonText) {
        dialogButton.setText(buttonText);
        return this;
    }

    public InfoDialog setButtonClickListener(DialogButtonClick buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
        return this;
    }

    public void showDialog() {
        show();
    }


    @Override
    public void onClick(View view) {
        if (buttonClickListener != null) {
            buttonClickListener.onButtonClick(this);
        } else {
            dismiss();
        }
    }
}
