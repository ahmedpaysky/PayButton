package io.paysky.ui.fragment.smspayment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseFragment;


public class SmsPaymentFragment extends BaseFragment implements View.OnClickListener {

    //Objects.
    private ActivityHelper activityHelper;
    //GUI.
    private EditText pinEditText;
    private ImageView resendSms;
    private RelativeLayout sendingSmsLayout;
    private TextView secondsTextView;
    private Button proceedButton;
    //Variables.
    private String phoneNumber;

    public SmsPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ActivityHelper) {
            activityHelper = (ActivityHelper) getActivity();
        } else {
            throw new IllegalStateException("activity must implement " + ActivityHelper.class.getSimpleName());
        }

        extractBundle();
    }

    private void extractBundle() {
        phoneNumber = getArguments().getString("phone_number");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        pinEditText = view.findViewById(R.id.pin_editText);
        resendSms = view.findViewById(R.id.resend_sms);
        sendingSmsLayout = view.findViewById(R.id.sending_sms_layout);
        secondsTextView = view.findViewById(R.id.seconds_textView);
        proceedButton = view.findViewById(R.id.proceed_button);
        proceedButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(proceedButton)) {
            proceedButtonClick();
        }
    }

    private void proceedButtonClick() {

    }
}
