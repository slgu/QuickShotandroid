package com.example.kzhu9.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by jinliang on 12/2/15.
 */

public class FriendInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getExtras().getString("NAME");
        String age = getIntent().getExtras().getString("AGE");
        String email = getIntent().getExtras().getString("EMAIL");
        String address = getIntent().getExtras().getString("ADDRESS");

        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.age)).setText(age);
        ((TextView) findViewById(R.id.email)).setText(email);
        ((TextView) findViewById(R.id.address)).setText(address);
    }
}
