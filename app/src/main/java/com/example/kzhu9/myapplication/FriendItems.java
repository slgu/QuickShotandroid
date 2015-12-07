package com.example.kzhu9.myapplication;

/**
 * Created by kzhu9 on 11/18/15.
 */
public class FriendItems {
    String uid, name, email;
    int sex;
    String topicList, friendList;


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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String tempEmail) {
        this.email = tempEmail;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int tempSex) {
        this.sex = tempSex;
    }

    public String getTopicList() {
        return this.topicList;
    }

    public void setTopicList(String tempTopicList) {
        this.topicList = tempTopicList;
    }

    public String getFriendList() {
        return this.friendList;
    }

    public void setFriendList(String tempFriendList) {
        this.friendList = tempFriendList;
    }

    @Override
    public String toString() {
        return "Name:" + name + ", Email:" + email + ", sex:" + String.valueOf(sex);
    }
}
