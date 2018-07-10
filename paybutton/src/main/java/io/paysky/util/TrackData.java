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
        tracks = in.createStringArray();
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

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    String track2;
    String[] tracks;
    String CardNumber;
    String ExpiryData;


    public TrackData(String data) {
        tracks = data.split("\n");
        if (tracks.length == 0) {
            return;
        }
        track1 = tracks[0];
        if (tracks.length == 1) {
            return;
        }
        track2 = tracks[1]; // track 2
        String[] track2Spilt = track2.split("=");
        CardNumber = track2Spilt[0];
        ExpiryData = track2Spilt[1].substring(0, 4);
    }

    public TrackData(String data, boolean x) {
        tracks = data.split("d");
        if (tracks.length == 0) {
            return;
        }
        track1 = tracks[0];
        if (tracks.length == 1) {
            return;
        }
        CardNumber = tracks[0];
        ;
        ExpiryData = tracks[1].substring(0, 4);
    }


    public String getCardNumber() {
        return CardNumber;
    }

    public String getExpiryData() {
        return ExpiryData;
    }

    public int getTrackLenght() {
        return tracks.length;
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
        parcel.writeStringArray(tracks);
        parcel.writeString(CardNumber);
        parcel.writeString(ExpiryData);
    }
}
