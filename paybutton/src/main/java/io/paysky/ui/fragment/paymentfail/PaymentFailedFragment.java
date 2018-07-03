package io.paysky.ui.fragment.paymentfail;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.paybutton.R;

import io.paysky.ui.base.ActivityHelper;
import io.paysky.ui.base.BaseActivity;
import io.paysky.ui.base.BaseFragment;


public class PaymentFailedFragment extends BaseFragment implements View.OnClickListener {


    //Objects,
    private ActivityHelper activityHelper;
    //GUI.
    private Button closeButton;
    private Button tryAgainButton;
    private TextView transactionDeclinedTextView;
    private TextView declineCauseTextView;
    //Variables.
    private String declineCause;

    public PaymentFailedFragment() {
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
        declineCause = getArguments().getString("decline_cause");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_failed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityHelper.setHeaderIcon(R.drawable.ic_close);
        activityHelper.setHeaderIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        initView(view);
    }

    private void initView(View view) {
        closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        tryAgainButton = view.findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(this);
        transactionDeclinedTextView = view.findViewById(R.id.transaction_declined_textView);
        declineCauseTextView = view.findViewById(R.id.declined_cause_textView);
        ((BaseActivity) getActivity()).showHtmlText(transactionDeclinedTextView, R.string.transaction_declined);
        declineCauseTextView.setText(declineCause);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close_button) {
            getActivity().onBackPressed();
        } else {
            getActivity().onBackPressed();
        }
    }
}
