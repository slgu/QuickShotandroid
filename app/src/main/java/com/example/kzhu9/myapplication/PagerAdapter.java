package com.example.kzhu9.myapplication;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kzhu9.fragments.main_tabs.FriendListFragment;
import com.example.kzhu9.fragments.main_tabs.LikedTopicListFragment;
import com.example.kzhu9.fragments.main_tabs.TopicListFragment;


/**
 * Created by jinliang on 12/9/15.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment tab1 = new FriendListFragment();
                return tab1;
            case 1:
                Fragment tab2 = new TopicListFragment();
                return tab2;
            case 2:
                Fragment tab3 = new LikedTopicListFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
