package com.example.kzhu9.myapplication;

/**
 * Created by kzhu9 on 11/25/15.
 */
public class TopicItems {
    String name;
    String description;
    double longitude, latitude;

    // how to implement audio and video

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

    public double getLongitude() { return this.longitude; }
    public void setLongitude(double tempLongitude) { this.longitude = tempLongitude;}


    public double getLatitude() { return this.latitude; }
    public void setLatitude(double tempLatitude) { this.longitude = tempLatitude;}
}
