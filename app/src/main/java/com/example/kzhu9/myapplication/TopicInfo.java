package com.example.kzhu9.myapplication;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kzhu9.fragments.main_tabs.main_tabs_info.TopicInfoFragment;

public class TopicInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_info);

        String name = getIntent().getExtras().getString("TITLE");
        String age = getIntent().getExtras().getString("DESCRIPTION");
        String email = getIntent().getExtras().getString("LIKE");
        String video = getIntent().getExtras().getString("VIDEO");


        System.out.println("HHHHHHHHH -----" + name);

        TopicInfoFragment topicInfoFragment = new TopicInfoFragment();

        Bundle args = new Bundle();
        args.putString(TopicInfoFragment.ARG_TITLE, name);
        args.putString(TopicInfoFragment.ARG_DESCRIPTION, age);
        args.putString(TopicInfoFragment.ARG_LIKE, email);
        args.putString(TopicInfoFragment.ARG_VIDEO, video);

        topicInfoFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.topicInfoContainer, topicInfoFragment, "TAG");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
