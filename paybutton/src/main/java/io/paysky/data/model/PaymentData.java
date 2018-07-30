package io.paysky.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.paysky.util.AppConstant;

public class PaymentData implements Parcelable {

    public String merchantId, terminalId, amount,
            receiverMail, defaultPayment;
    public boolean enableQr, enableMagnetic, enableManual;

    public PaymentData(){

    }

    protected PaymentData(Parcel in) {
        merchantId = in.readString();
        terminalId = in.readString();
        amount = in.readString();
        receiverMail = in.readString();
        defaultPayment = in.readString();
        enableQr = in.readByte() != 0;
        enableMagnetic = in.readByte() != 0;
        enableManual = in.readByte() != 0;
    }

    public static final Creator<PaymentData> CREATOR = new Creator<PaymentData>() {
        @Override
        public PaymentData createFromParcel(Parcel in) {
            return new PaymentData(in);
        }

        @Override
        public PaymentData[] newArray(int size) {
            return new PaymentData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(merchantId);
        parcel.writeString(terminalId);
        parcel.writeString(amount);
        parcel.writeString(receiverMail);
        parcel.writeString(defaultPayment);
        parcel.writeByte((byte) (enableQr ? 1 : 0));
        parcel.writeByte((byte) (enableMagnetic ? 1 : 0));
        parcel.writeByte((byte) (enableManual ? 1 : 0));
    }

    public enum DefaultPayment {
        MANUAL, MAGNETIC, QR_CODE;
    }


}
