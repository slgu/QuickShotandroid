package com.example.kzhu9.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendInfo extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TopicListAdapter mAdapter;
    private ArrayList<TopicList.TopicEntity> topicsData = new ArrayList<>();

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

        String [] arr = new Gson().fromJson(topic_lists, String[].class);

        List <String> topicUidList = java.util.Arrays.asList(arr);
        System.out.println("debuglist:" + topicUidList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(name);
        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.age)).setText(String.valueOf(age));
        ((TextView) findViewById(R.id.email)).setText(email);
        ((TextView) findViewById(R.id.address)).setText(address);
        mRecyclerView = (RecyclerView) findViewById(R.id.topicList_profile);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicListAdapter(topicsData);
        mAdapter.setOnItemClickListener(new TopicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getBaseContext().getApplicationContext(), TopicInfo.class);
                if (topicsData == null)
                    return;
                intent.putExtra("UID", topicsData.get(position).getUid());
                intent.putExtra("TITLE", topicsData.get(position).getTitle());
                intent.putExtra("DESCRIPTION", topicsData.get(position).getDescription());
                intent.putExtra("LIKE", topicsData.get(position).getLike());
                intent.putExtra("VIDEO", topicsData.get(position).getVideo_uid());
                intent.putExtra("LAT", topicsData.get(position).getLat());
                intent.putExtra("LON", topicsData.get(position).getLon());
                intent.putExtra("COMMENTLIST", topicsData.get(position).getComments_list());

                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getTopicList(topicUidList);
    }

    public void getTopicList(List <String> topicUidList) {
        final int size = topicUidList.size();
        System.out.println("debugshow:" + size);
        topicsData.clear();
        for (String uid : topicUidList) {
            System.out.println("debuguid:" + uid);
            String requestURL = Config.REQUESTURL + "/topic/get";

            RequestBody formBody = new FormEncodingBuilder()
                    .add("uid", uid)
                    .build();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .post(formBody)
                    .build();


            OkHttpSingleton.getInstance().getClient(getBaseContext()).newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException throwable) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Unable to connect to server server, please try later", Toast.LENGTH_LONG).show();
                        }
                    });
                    throwable.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseStr = response.body().string();

                    try {

                        TopicList.TopicEntity topicEntity = new TopicList.TopicEntity();
                        System.out.println("res:" + responseStr);
                        JSONObject responseObj = new JSONObject(responseStr);

                        JSONObject info = responseObj.getJSONObject("info");

                        topicEntity.setUid(info.getString("uid"));
                        topicEntity.setTitle(info.getString("title"));
                        topicEntity.setDescription(info.getString("desc"));
                        topicEntity.setVideo_uid(info.getString("video_uid"));
                        topicEntity.setImage_uid(info.getString("img_uid"));
                        topicEntity.setLat(info.getString("lat"));
                        topicEntity.setLon(info.getString("lon"));
                        topicEntity.setLike(info.getInt("like"));
                        String commentStr = info.getString("comment_list");
                        ArrayList<String> commentList = new ArrayList<String>(Arrays.asList(commentStr.split(",")));
                        topicEntity.setComments_list(commentList);
                        topicsData.add(topicEntity);
                        //get all data done
                        if (size == topicsData.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
