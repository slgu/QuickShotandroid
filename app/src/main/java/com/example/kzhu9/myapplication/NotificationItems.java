package com.example.kzhu9.myapplication;

/**
 * Created by kzhu9 on 11/18/15.
 */
public class NotificationItems {
    String uid, name;

    public String getUid() {
        return this.uid;
    }

    public void setUid(String tempUid) {
        this.uid = tempUid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String tempName) {
        this.name = tempName;
    }

    @Override
    public String toString() {
        return "Name:" + name;
    }
}