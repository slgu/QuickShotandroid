package com.example.kzhu9.myapplication;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kzhu9.fragments.FriendInfoFragment;

/**
 * Created by jinliang on 12/2/15.
 */

public class FriendInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        String name = getIntent().getExtras().getString("NAME");
        String age = getIntent().getExtras().getString("AGE");
        String email = getIntent().getExtras().getString("EMAIL");
        String address = getIntent().getExtras().getString("ADDRESS");


        System.out.println("HHHHHHHHH -----" + name);

        FriendInfoFragment friendInfoFragment = new FriendInfoFragment();

        Bundle args = new Bundle();
        args.putString(FriendInfoFragment.ARG_NAME, name);
        args.putString(FriendInfoFragment.ARG_AGE, age);
        args.putString(FriendInfoFragment.ARG_EMAIL, email);
        args.putString(FriendInfoFragment.ARG_ADDRESS, address);

        friendInfoFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.infoContainer, friendInfoFragment, "TAG");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
