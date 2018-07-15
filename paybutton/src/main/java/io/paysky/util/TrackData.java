package io.paysky.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PaySky-3 on 8/13/2017.
 */

public class TrackData implements Parcelable {
    String data;
    String track1;

    protected TrackData(Parcel in) {
        data = in.readString();
        track1 = in.readString();
        track2 = in.readString();
        CardNumber = in.readString();
        ExpiryData = in.readString();
    }

    public static final Creator<TrackData> CREATOR = new Creator<TrackData>() {
        @Override
        public TrackData createFromParcel(Parcel in) {
            return new TrackData(in);
        }

        @Override
        public TrackData[] newArray(int size) {
            return new TrackData[size];
        }
    };

    public String getTrack2() {
        return track2;
    }


    public String track2;
    public String CardNumber;
    public String ExpiryData;


    TrackData() {

    }


    public String getCardNumber() {
        return CardNumber;
    }

    public String getExpiryData() {
        return ExpiryData;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeString(track1);
        parcel.writeString(track2);
        parcel.writeString(CardNumber);
        parcel.writeString(ExpiryData);
    }
}
