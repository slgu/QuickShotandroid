package com.example.kzhu9.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kzhu9 on 11/25/15.
 */
public class TopicItems implements Parcelable {
    String name;
    String description;
    String longitude, latitude;

    // how to implement audio and video

    public TopicItems() {

    }

    protected TopicItems(Parcel in) {
        name = in.readString();
        description = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    public static final Creator<TopicItems> CREATOR = new Creator<TopicItems>() {
        @Override
        public TopicItems createFromParcel(Parcel in) {
            return new TopicItems(in);
        }

        @Override
        public TopicItems[] newArray(int size) {
            return new TopicItems[size];
        }
    };

    public String getName() {
        return this.name;
    }

    public void setName(String tempName) {
        this.name = tempName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String tempDescription) {
        this.description = tempDescription;
    }

    public String getLongitude() { return this.longitude; }
    public void setLongitude(String tempLongitude) { this.longitude = tempLongitude;}


    public String getLatitude() { return this.latitude; }
    public void setLatitude(String tempLatitude) { this.latitude = tempLatitude;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }
}