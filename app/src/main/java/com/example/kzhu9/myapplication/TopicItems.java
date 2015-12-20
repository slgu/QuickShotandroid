package com.example.kzhu9.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kzhu9 on 11/25/15.
 */
public class TopicItems implements Parcelable {
    String uid;
    String title;
    String description;
    String longitude, latitude;

    // how to implement audio and video

    public TopicItems() {

    }

    protected TopicItems(Parcel in) {
        uid = in.readString();
        title = in.readString();
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


    public String getUid() {
        return this.uid;
    }

    public void setUid(String tempUid) {
        this.uid = tempUid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String tempTitle) {
        this.title = tempTitle;
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
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }
}