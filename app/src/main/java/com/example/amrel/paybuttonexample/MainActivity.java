package com.example.amrel.paybuttonexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.paysky.data.model.SuccessfulCardTransaction;
import io.paysky.data.model.SuccessfulWalletTransaction;
import io.paysky.ui.custom.PayButton;
import io.paysky.util.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    //GUI.
    private EditText merchantIdEditText, terminalIdEditText, amountEditText;
    private TextView paymentStatusTextView;
    private TextView payTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // find views.
        payTextView = (TextView) findViewById(R.id.pay_textView);
        merchantIdEditText = findViewById(R.id.merchant_id_editText);
        terminalIdEditText = findViewById(R.id.terminal_id_editText);
        amountEditText = findViewById(R.id.amount_editText);
        paymentStatusTextView = findViewById(R.id.payment_status_textView);
        final PayButton payButton = new PayButton(this);
        payTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminalId = terminalIdEditText.getText().toString().trim();
                String merchantId = merchantIdEditText.getText().toString().trim();
                String amount = amountEditText.getText().toString().trim();
                if (terminalId.isEmpty() || merchantId.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(MainActivity.this, "check all inputs", Toast.LENGTH_SHORT).show();
                    return;
                }


                // add payments data.

                payButton.setMerchantId(merchantId); // Merchant id
                payButton.setTerminalId(terminalId); // Terminal  id
                payButton.setAmount(Double.valueOf(amount)); // Amount
                payButton.setCurrencyCode(826); // Currency Code
                payButton.setMerchantSecureHash("35393434313266342D636662392D343334612D613765332D646365626337663334386363");
                payButton.createTransaction(new PayButton.PaymentTransactionCallback() {
                    @Override
                    public void onCardTransactionSuccess(SuccessfulCardTransaction cardTransaction) {
                        paymentStatusTextView.setText(cardTransaction.toString());
                    }

                    @Override
                    public void onWalletTransactionSuccess(SuccessfulWalletTransaction walletTransaction) {
                        paymentStatusTextView.setText(walletTransaction.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        paymentStatusTextView.setText("failed by:- " + error.getMessage());
                    }
                });
            }
        });
        TextView appVersion = findViewById(R.id.app_version_textView);
        appVersion.setText("PaySDK - PayButton module - Ver.  " + AppUtils.getVersionNumber(this));
        ImageView logoImageView = findViewById(R.id.logo_imageView);
        logoImageView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        startActivity(new Intent(this, SettingActivity.class));
        return true;
    }
}
