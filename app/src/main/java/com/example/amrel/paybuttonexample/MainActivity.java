package com.example.amrel.paybuttonexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.paysky.data.network.ApiLinks;
import io.paysky.ui.custom.PayButton;
import io.paysky.util.AppConstant;
import io.paysky.util.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

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

                // add config
                boolean enableManualValue = SettingPrefs.getBooleanPrefs(MainActivity.this, "enable_manual", false);
                boolean enableMagneticValue = SettingPrefs.getBooleanPrefs(MainActivity.this, "enable_magnetic", false);
                boolean enableQrValue = SettingPrefs.getBooleanPrefs(MainActivity.this, "enable_qr", false);
                String defaultPayment = SettingPrefs.getStringPrefs(MainActivity.this, "default_payment", "Manual");
                String serverLink = SettingPrefs.getStringPrefs(MainActivity.this, "default_payment", ApiLinks.MAIN_LINK);
                payButton.setServerLink(serverLink);
                payButton.setEnableMagneticPayment(enableMagneticValue);
                payButton.setEnableManualPayment(enableManualValue);
                payButton.setEnableQrPayment(enableQrValue);
                if (defaultPayment.equals("Manual")) {
                    payButton.setDefaultPayment(AppConstant.PaymentMethods.MANUAL);
                } else if (defaultPayment.equals("Magnetic")) {
                    payButton.setDefaultPayment(AppConstant.PaymentMethods.MAGNETIC);
                } else {
                    payButton.setDefaultPayment(AppConstant.PaymentMethods.QR_READER);
                }

                // add payments data.
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
        ImageView logoImageView = findViewById(R.id.logo_imageView);
        logoImageView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        startActivity(new Intent(this, SettingActivity.class));
        return true;
    }
}
