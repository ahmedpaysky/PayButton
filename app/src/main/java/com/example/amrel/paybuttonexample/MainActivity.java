package com.example.amrel.paybuttonexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.paysky.ui.custom.PayButton;
import io.paysky.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    //GUI.
    private EditText merchantIdEditText, terminalIdEditText, amountEditText;
    private TextView paymentStatusTextView;
    PayButton payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // find views.
        payButton = (PayButton) findViewById(R.id.paybtn);
        merchantIdEditText = findViewById(R.id.merchant_id_editText);
        terminalIdEditText = findViewById(R.id.terminal_id_editText);
        amountEditText = findViewById(R.id.amount_editText);
        paymentStatusTextView = findViewById(R.id.payment_status_textView);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminalId = terminalIdEditText.getText().toString().trim();
                String merchantId = merchantIdEditText.getText().toString().trim();
                String amount = amountEditText.getText().toString().trim();
                if (terminalId.isEmpty() || merchantId.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(MainActivity.this, "check all inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                payButton.setMerchantId(Long.valueOf(merchantId)); // Merchant id
                payButton.setTerminalId(Long.valueOf(terminalId)); // Terminal  id
                payButton.setPayAmount(Double.valueOf(amount)); // Amount
                payButton.setCurrencyCode(818); // Currency Code
                payButton.createTransaction(new PayButton.PaymentTransactionCallback() {
                    @Override
                    public void onSuccess(String referenceNumber, String responseCode, String authorizationCode) {
                        paymentStatusTextView.setText("reference number = " + referenceNumber
                                + " , response code = " + responseCode + " , authorization code = " + authorizationCode);

                    }

                    @Override
                    public void onError(Throwable error) {
                        paymentStatusTextView.setText("payment failed because = " + error.getMessage());
                    }
                });
            }
        });
        TextView appVersion = findViewById(R.id.app_version_textView);
        appVersion.setText("PaySky PayButton Demo Version " + AppUtils.getVersionNumber(this));
    }
}
