package com.example.kzhu9.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.kzhu9.myapplication.R;

/**
 * Created by jinliang on 12/2/15.
 */
public class TopicInfoFragment extends Fragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_DESCRIPTION = "description";
    public static final String ARG_LIKE = "like";
    public static final String ARG_VIDEO = "video";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_info, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        ((TextView) getActivity().findViewById(R.id.title)).setText(args.getString(ARG_TITLE));
        ((TextView) getActivity().findViewById(R.id.describe)).setText(args.getString(ARG_DESCRIPTION));
        ((TextView) getActivity().findViewById(R.id.like)).setText(args.getString(ARG_LIKE));
        ((TextView) getActivity().findViewById(R.id.video)).setText(args.getString(ARG_VIDEO));

        VideoView videoView = (VideoView) getActivity().findViewById(R.id.videoView);

        String vidAddress = args.getString(ARG_VIDEO);
        Uri vidUri = Uri.parse(vidAddress);
        videoView.setVideoURI(vidUri);

        MediaController mediaController = new
                MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

    }
}
