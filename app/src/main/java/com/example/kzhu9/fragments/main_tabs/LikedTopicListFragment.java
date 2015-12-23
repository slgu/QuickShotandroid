package com.example.kzhu9.fragments.main_tabs;

/// /import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicInfo;
import com.example.kzhu9.myapplication.TopicItemClickListener;
import com.example.kzhu9.myapplication.TopicItemLongClickListener;
import com.example.kzhu9.myapplication.TopicList;
import com.example.kzhu9.myapplication.TopicListAdapter;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jinliang on 11/15/15.
 */

public class LikedTopicListFragment extends Fragment implements TopicItemClickListener, TopicItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private TopicListAdapter adapter;
    final ArrayList<TopicList.TopicEntity> topiList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_listview, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.topicList);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer_topic);
        try {
            Thread.sleep(500);
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    dosomething();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeContainer.setOnRefreshListener(this);

        adapter = new TopicListAdapter(getActivity().getApplicationContext());

        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setHasFixedSize(true);

        getTopicUidList();
    }

    public void getTopicUidList() {
        String requestURL = Config.REQUESTURL + "/user/like";

        RequestBody formBody = new FormEncodingBuilder()
                .add("uid", Config.user_id)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(formBody)
                .build();

        if (getActivity() == null)
            return;
        OkHttpSingleton.getInstance().getClient(getActivity().getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getActivity(), "Unable to connect to server, please try later", Toast.LENGTH_LONG).show();
                    }
                });
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Server is down!", Toast.LENGTH_LONG).show();
                        }
                    });

                    // need to re-login
                    throw new IOException("Unexpected code " + response);
                }

                String responseStr = response.body().string();

                if (!topiList.isEmpty())
                    topiList.clear();
                try {
                    JSONObject responseObj = new JSONObject(responseStr);
                    final JSONArray json_topic_list = responseObj.getJSONArray("info");
                    if (!topiList.isEmpty())
                        topiList.clear();
                    for (int i = 0; i < json_topic_list.length(); i++) {
                        JSONObject obj = json_topic_list.getJSONObject(i);
                        try {
                            TopicList.TopicEntity topicEntity = new TopicList.TopicEntity();
                            topicEntity.setUid(obj.getString("uid"));
                            topicEntity.setTitle(obj.getString("title"));
                            topicEntity.setDescription(obj.getString("desc"));
                            topicEntity.setVideo_uid(obj.getString("video_uid"));
                            topicEntity.setLat(obj.getString("lat"));
                            topicEntity.setLon(obj.getString("lon"));
                            topicEntity.setLike(obj.getInt("like"));
                            topicEntity.setImage_uid(obj.getString("img_uid"));
                            String commentStr = obj.getString("comment_list");
                            ArrayList<String> commentList = new ArrayList<String>(Arrays.asList(commentStr.split(",")));
                            topicEntity.setComments_list(commentList);
                            topiList.add(topicEntity);
                            if (getActivity() == null)
                                return;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(topiList);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void dosomething() {
        swipeContainer.setRefreshing(true);
        getTopicUidList();

        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getActivity(), "Liked Topics Refreshed!", Toast.LENGTH_LONG).show();
            }
        });

        swipeContainer.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        dosomething();
    }

    public void getTopicList(ArrayList<String> topicUidList) {
        final int size = topicUidList.size();
        for (String uid : topicUidList) {
            String requestURL = Config.REQUESTURL + "/topic/get";

            RequestBody formBody = new FormEncodingBuilder()
                    .add("uid", uid)
                    .build();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .post(formBody)
                    .build();

            if (getActivity() == null)
                return;

            OkHttpSingleton.getInstance().getClient(getActivity().getBaseContext()).newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException throwable) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), "Unable to connect to server server, please try later", Toast.LENGTH_LONG).show();
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

                        JSONObject responseObj = new JSONObject(responseStr);
                        JSONObject info = responseObj.getJSONObject("info");

                        topicEntity.setUid(info.getString("uid"));
                        topicEntity.setTitle(info.getString("title"));
                        topicEntity.setDescription(info.getString("desc"));
                        topicEntity.setVideo_uid(info.getString("video_uid"));
                        topicEntity.setLat(info.getString("lat"));
                        topicEntity.setLon(info.getString("lon"));
                        topicEntity.setLike(info.getInt("like"));
                        String commentStr = info.getString("comment_list");
                        ArrayList<String> commentList = new ArrayList<String>(Arrays.asList(commentStr.split(",")));

                        topicEntity.setComments_list(commentList);

                        topiList.add(topicEntity);

                        if (getActivity() == null)
                            return;

                        if (topiList.size() == size) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(topiList);
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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), TopicInfo.class);

        if (topiList == null)
            return;

        intent.putExtra("UID", topiList.get(position).getUid());
        intent.putExtra("TITLE", topiList.get(position).getTitle());
        intent.putExtra("DESCRIPTION", topiList.get(position).getDescription());
        intent.putExtra("LIKE", topiList.get(position).getLike());
        intent.putExtra("VIDEO", topiList.get(position).getVideo_uid());
        intent.putExtra("LAT", topiList.get(position).getLat());
        intent.putExtra("LON", topiList.get(position).getLon());
        intent.putExtra("COMMENTLIST", topiList.get(position).getComments_list());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getTopicUidList();
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }
}