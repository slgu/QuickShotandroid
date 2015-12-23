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
import java.util.Collections;
import java.util.Date;

/**
 * Created by jinliang on 11/15/15.
 */

public class TopicListFragment extends Fragment implements TopicItemClickListener, TopicItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
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

    public void dosomething() {
        swipeContainer.setRefreshing(true);
        getTopicUidList();

        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "My Topics Refreshed!", Toast.LENGTH_LONG).show();
            }
        });


        swipeContainer.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        dosomething();
    }

    private void getTopicUidList() {
        String requestURL = Config.REQUESTURL + "/user/get";

        RequestBody formBody = new FormEncodingBuilder()
                .add("uid", Config.user_id)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(formBody)
                .build();

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
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                String responseStr = response.body().string();

                try {
                    JSONObject responseObj = new JSONObject(responseStr);
                    JSONObject info = responseObj.getJSONObject("info");
                    JSONArray topicsList = info.getJSONArray("topics_list");
                    ArrayList<String> uidList = new ArrayList<>();

                    for (int i = 0; i < topicsList.length(); i++) {
                        uidList.add(topicsList.getString(i));

                    }
                    getTopicList(uidList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void getTopicList(final ArrayList<String> topicUidList) {
        final int size = topicUidList.size();
        topiList.clear();

        for (String uid : topicUidList) {
            System.out.println(uid);
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
                    System.out.println(responseStr);

                    try {
                        TopicList.TopicEntity topicEntity = new TopicList.TopicEntity();

                        JSONObject responseObj = new JSONObject(responseStr);

                        JSONObject info = responseObj.getJSONObject("info");
                        Date createAt = new Date(info.getString("createAt"));

                        topicEntity.setUid(info.getString("uid"));
                        topicEntity.setTitle(info.getString("title"));
                        topicEntity.setDescription(info.getString("desc"));
                        topicEntity.setVideo_uid(info.getString("video_uid"));
                        topicEntity.setImage_uid(info.getString("img_uid"));
                        topicEntity.setLat(info.getString("lat"));
                        topicEntity.setLon(info.getString("lon"));
                        topicEntity.setLike(info.getInt("like"));
                        topicEntity.setCreateAt(createAt);
                        String commentStr = info.getString("comment_list");
                        ArrayList<String> commentList = new ArrayList<String>(Arrays.asList(commentStr.split(",")));
                        topicEntity.setComments_list(commentList);
                        topiList.add(topicEntity);

                        if (getActivity() == null)
                            return;

                        if (size == topiList.size()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //sort according to timestamp
                                    Collections.sort(topiList);
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