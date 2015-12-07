package com.example.kzhu9.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kzhu9.myapplication.R;

/**
 * Created by jinliang on 12/2/15.
 */
public class FriendInfoFragment extends Fragment {

    public static final String ARG_NAME = "position";
    public static final String ARG_AGE = "age";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_ADDRESS = "address";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_info, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        ((TextView) getActivity().findViewById(R.id.name)).setText(args.getString(ARG_NAME));
        ((TextView) getActivity().findViewById(R.id.age)).setText(String.valueOf(args.getInt(ARG_AGE)));
        ((TextView) getActivity().findViewById(R.id.email)).setText(args.getString(ARG_EMAIL));
        ((TextView) getActivity().findViewById(R.id.address)).setText(args.getString(ARG_ADDRESS));

    }
}
