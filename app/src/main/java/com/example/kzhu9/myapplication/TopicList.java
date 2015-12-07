package com.example.kzhu9.myapplication;

import java.util.ArrayList;

/**
 * Created by jinliang on 11/15/15.
 */
public class TopicList {


    /**
     * status : 0
     * info : {"uid":"86893e12-a4aa-4415-a5c3-cbafb1da2e87","email":"song98@gmail.com","name":"songgao","passwd":"","friends_list":["101","102","103","104","105"],"topics_list":["topic_201","topic_202","topic_203","topic_204","topic_205"],"sex":0,"age":22,"address":"columbia university"}
     */

    private int status;
    /**
     * uid : 86893e12-a4aa-4415-a5c3-cbafb1da2e87
     * email : song98@gmail.com
     * name : songgao
     * passwd :
     * friends_list : ["101","102","103","104","105"]
     * topics_list : ["topic_201","topic_202","topic_203","topic_204","topic_205"]
     * sex : 0
     * age : 22
     * address : columbia university
     */

    private TopicEntity info;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setInfo(TopicEntity info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public TopicEntity getInfo() {
        return info;
    }

    public static class TopicEntity {
        private String uid;
        private String title;
        private String description;
        private double lat;
        private double lon;
        private int like;
        private String video_uid;
        private ArrayList<String> comments_list;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public String getVideo_uid() {
            return video_uid;
        }

        public void setVideo_uid(String video_uid) {
            this.video_uid = video_uid;
        }

        public ArrayList<String> getComments_list() {
            return comments_list;
        }

        public void setComments_list(ArrayList<String> comments_list) {
            this.comments_list = comments_list;
        }
    }
}

