package com.example.kzhu9.myapplication;

/**
 * Created by Song on 11/29/15.
 */
public class User {
    String name, username, password, email, emailVerificationCode, address, age, sex;

    public User(String username, String password, String name, String email, String emailVerficationCode, String address, String age, String sex) {
        this.address = address;
        this.age = age;
        this.email = email;
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.username = username;
        this.emailVerificationCode = emailVerficationCode;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.age = null;
        this.sex = null;
        this.address = null;
        this.email = null;
        this.name = null;
        this.emailVerificationCode = null;
    }
}
