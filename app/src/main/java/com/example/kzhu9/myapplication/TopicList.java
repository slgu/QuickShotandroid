package com.example.kzhu9.myapplication;

import java.util.ArrayList;

/**
 * Created by jinliang on 11/15/15.
 */
public class TopicList{
    private int status;

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

    public static class TopicEntity implements Comparable<TopicEntity> {
        private String uid;
        private String title;
        private String description;
        private String lat;
        private String lon;
        private int like;
        private String video_uid;
        private String icon_uid;
        private ArrayList<String> comments_list;

        @Override
        public int compareTo(TopicEntity topicEntity) {
            //write code here for compare name
            return topicEntity.description.compareTo(this.description);
        }

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

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
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

        public String getIcon_uid() {
            return icon_uid;
        }

        public void setIcon_uid(String icon_uid) {
            this.icon_uid = icon_uid;
        }

        public ArrayList<String> getComments_list() {
            return comments_list;
        }

        public void setComments_list(ArrayList<String> comments_list) {
            this.comments_list = comments_list;
        }

        @Override
        public String toString() {
            return "TopicEntity{" +
                    "uid='" + uid + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lon='" + lon + '\'' +
                    ", like=" + String.valueOf(like) +
                    ", video_uid='" + video_uid + '\'' +
                    ", icon_uid='" + icon_uid + '\'' +
                    ", comments_list=" + comments_list +
                    '}';
        }
    }
}

