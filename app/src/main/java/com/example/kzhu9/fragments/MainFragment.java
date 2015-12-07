package com.example.kzhu9.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kzhu9.myapplication.R;

/**
 * Created by kzhu9 on 11/7/15.
 */
public class MainFragment extends Fragment {
    Fragment friendsFragment;
    Fragment topicsFragment;
    FragmentManager mManager;
    View rootview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        mManager = getFragmentManager();

        Button friendsButton = (Button) rootview.findViewById(R.id.friendsButton);
        final Button topicsButton = (Button) rootview.findViewById(R.id.topicsButton);

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsFragment = new FriendListFragment();

                FragmentTransaction fragmentTransaction = mManager.beginTransaction();

                Fragment fragmentA = mManager.findFragmentByTag("friendsFragment");
                Fragment fragmentB = mManager.findFragmentByTag("topicsFragment");
                if (fragmentA == null && fragmentB == null) {
                    fragmentTransaction.add(R.id.container, friendsFragment, "friendsFragment");
                }
                if (fragmentB != null && fragmentA == null) {
                    fragmentTransaction.replace(R.id.container, friendsFragment, "friendsFragment");
                }
                Log.i("TAG-A", "AAAAAAAAAAAA");
                fragmentTransaction.commit();
            }
        });

        topicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicsFragment = new TopicListFragment();
                FragmentTransaction fragmentTransaction = mManager.beginTransaction();

                Fragment fragmentA = mManager.findFragmentByTag("friendsFragment");
                Fragment fragmentB = mManager.findFragmentByTag("topicsFragment");

                if (fragmentA == null && fragmentB == null) {
                    fragmentTransaction.add(R.id.container, topicsFragment, "topicsFragment");
                }

                if (fragmentA != null && fragmentB == null) {
                    fragmentTransaction.replace(R.id.container, topicsFragment, "topicsFragment");
                }
                Log.i("TAG-B", "BBBBBBBBBBBB");
                fragmentTransaction.commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_main, container, false);
        return rootview;
    }
}
