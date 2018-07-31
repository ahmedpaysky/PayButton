package com.example.amrel.paybuttonexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.paysky.ui.custom.PayButton;

public class PayListAdapter extends BaseAdapter implements PayButton.PaymentTransactionCallback {

    List<String> items = new ArrayList<>();
    Context context ;
    public PayListAdapter(Context context) {
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        items.add("item1");
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pay_list_item, viewGroup, false);
        final PayButton payButton = view.findViewById(R.id.payBtn);
        payButton.setMerchantId(44064);
        payButton.setTerminalId(98974561);
        payButton.setEnableQrPayment(true);
        payButton.setEnableManualPayment(true);
        payButton.setEnableMagneticPayment(true);
        payButton.setPayAmount(20);
        payButton.setCurrencyCode(818);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payButton.createTransaction(PayListAdapter.this);
            }
        });
        return view;
    }

    @Override
    public void onSuccess(String referenceNumber, String responseCode, String authorizationCode) {
        Toast.makeText(context, "pay success = " + responseCode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Throwable error) {
        Toast.makeText(context, "pay fail = " + error.getMessage(), Toast.LENGTH_LONG).show();
    }
}