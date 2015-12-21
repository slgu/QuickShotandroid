package com.example.kzhu9.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class TopicInfo extends AppCompatActivity {
    Button btComment;
    EditText edComment;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentListAdapter mAdapter;

//    MapView mapView;
//    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_info);

        String title = getIntent().getExtras().getString("TITLE");
        String description = getIntent().getExtras().getString("DESCRIPTION");
        int like = getIntent().getExtras().getInt("LIKE");
        String video = getIntent().getExtras().getString("VIDEO");
        System.out.println(video);
        String latitude = getIntent().getExtras().getString("LAT");
        String longitude = getIntent().getExtras().getString("LON");

//        ArrayList<String> com = getIntent().getExtras().getParcelableArrayList("COMMENTLIST");
        String commentList = getIntent().getExtras().getParcelableArrayList("COMMENTLIST").toString();
        String comments = commentList.substring(1, commentList.length() - 1);

        JSONArray temp = new JSONArray();

        ArrayList<CommentItem> commentsData = new ArrayList<>();

        try {
            temp = new JSONArray(comments);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject com = temp.getJSONObject(i);
                CommentItem comItem = new CommentItem();
                comItem.setName(com.getString("name"));
                comItem.setText(com.getString("text"));
                comItem.setTime(com.getString("time"));
                commentsData.add(comItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_list_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentListAdapter(commentsData);
        mRecyclerView.setAdapter(mAdapter);


//        double lat = Double.parseDouble(latitude);
//        double lon = Double.parseDouble(longitude);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
//
        Uri vidUri = Uri.parse(video);
        videoView.setVideoURI(vidUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

//        mapView = (MapView) findViewById(R.id.mapview_small);
//        mapView.onCreate(savedInstanceState);
//        map = mapView.getMap();
//        map.getUiSettings().setMyLocationButtonEnabled(true);
//        map.setMyLocationEnabled(true);
//        MapsInitializer.initialize(this);
//
//        System.out.println(latitude + " " + longitude);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory
//                .newLatLngZoom(new LatLng(lat, lon), 12);
//        map.animateCamera(cameraUpdate);

        btComment = (Button) findViewById(R.id.btComment);
        edComment = (EditText) findViewById(R.id.etComment);

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.describe)).setText(description);
        ((TextView) findViewById(R.id.like)).setText(String.valueOf(like));
        ((TextView) findViewById(R.id.video)).setText(video);
//        ((TextView) findViewById(R.id.commentContent)).setText(comments);

        final String topicId = getIntent().getExtras().getString("UID");
        btComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btComment:
                        String requestURL = Config.REQUESTURL + "/topic/comment";
                        System.out.println(topicId);

                        RequestBody formBody = new FormEncodingBuilder()
                                .add("tid", topicId)
                                .add("comment", edComment.getText().toString())
                                .build();
                        Request request = new Request.Builder()
                                .url(requestURL)
                                .post(formBody)
                                .build();

                        OkHttpSingleton.getInstance().getClient(getApplicationContext()).newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException throwable) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                String responseStr = response.body().string();
                                System.out.println(responseStr);

                                Gson gson = new Gson();
                                JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                                int status = Integer.parseInt(responseJsonObject.get("status").toString());

                                String resultStr = null;
                                switch (status) {
                                    case 0:
                                        resultStr = "Done adding comment!";
                                        // update comment list
                                        break;
                                    case 1:
                                        // go back to login activity ???????????????
                                        break;
                                    case 2:
                                        resultStr = "Invalid comment message!";
                                        break;
                                    case 3:
                                        resultStr = "No topic id information!";
                                        break;
                                }
                                if (resultStr != null) {
                                    final String tmp = resultStr;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
                                        }
                                    });
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
    }
}

