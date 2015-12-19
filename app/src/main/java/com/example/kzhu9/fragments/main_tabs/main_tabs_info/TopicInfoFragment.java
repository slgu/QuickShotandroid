package com.example.kzhu9.fragments.main_tabs.main_tabs_info;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.example.kzhu9.myapplication.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jinliang on 12/2/15.
 */
public class TopicInfoFragment extends Fragment {
    View rootview;
    public static final String ARG_TITLE = "title";
    public static final String ARG_DESCRIPTION = "description";
    public static final String ARG_LIKE = "like";
    public static final String ARG_VIDEO = "video";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final String topicid = "1";

        final Button btComment;
        final EditText etComment;
        btComment = (Button) rootview.findViewById(R.id.btComment);
        etComment = (EditText) rootview.findViewById(R.id.etComment);
        final String comment = etComment.getText().toString();

        btComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btComment:
                        String requestURL = Config.REQUESTURL + "/user/comment";

                        RequestBody formBody = new FormEncodingBuilder()
                                .add("topic_uid", topicid)
                                .add("comment", comment)
                                .build();
                        Request request = new Request.Builder()
                                .url(requestURL)
                                .post(formBody)
                                .build();

                        OkHttpSingleton.getInstance().getClient(getActivity()).newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException throwable) {
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                String responseStr = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseStr);
                                    if (jsonObject.getInt("status") == 0) {
                                        // comment add successfully
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity().getApplicationContext(), "Comment Added!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        // Invalid User
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity().getApplicationContext(), "Invalid User!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Headers responseHeaders = response.headers();
                                for (int i = 0; i < responseHeaders.size(); i++) {
                                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }
                            }
                        });
                        break;
                }
            }
        });
        //******************* End of Add Comment
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_topic_info, container, false);
        return rootview;
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
