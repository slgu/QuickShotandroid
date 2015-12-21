package com.example.kzhu9.myapplication;

/**
 * Created by jinliang on 11/15/15.
 */
public class FriendList {


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

    private FriendEntity info;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setInfo(FriendEntity info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public FriendEntity getInfo() {
        return info;
    }

    public static class FriendEntity {
        private String uid;
        private String email;
        private String name;
        private String passwd;
        private int sex;
        private int age;
        private String address;
        private String friends_list;
        private String topics_list;

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setFriends_list(String friends_list) {
            this.friends_list = friends_list;
        }

        public void setTopics_list(String topics_list) {
            this.topics_list = topics_list;
        }

        public String getUid() {
            return uid;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getPasswd() {
            return passwd;
        }

        public int getSex() {
            return sex;
        }

        public int getAge() {
            return age;
        }

        public String getAddress() {
            return address;
        }

        public String getFriends_list() {
            return friends_list;
        }

        public String getTopics_list() {
            return topics_list;
        }
    }
}

