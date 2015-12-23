package com.example.kzhu9.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

public class FriendInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String name = getIntent().getExtras().getString("NAME");
        int age = getIntent().getExtras().getInt("AGE");
        String email = getIntent().getExtras().getString("EMAIL");
        String address = getIntent().getExtras().getString("ADDRESS");
        String topic_lists = getIntent().getExtras().getString("TOPIC_LISTS");
        String img_uid = getIntent().getExtras().getString("IMG_UID");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(name);

//        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.age)).setText(String.valueOf(age));
        ((TextView) findViewById(R.id.email)).setText(email);
        ((TextView) findViewById(R.id.address)).setText(address);
        ListView topics_list = (ListView) findViewById(R.id.topicLists);
    }
}
