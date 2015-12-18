package com.example.kzhu9.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.OkHttpSingleton;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicInfo;
import com.example.kzhu9.myapplication.TopicItemClickListener;
import com.example.kzhu9.myapplication.TopicItemLongClickListener;
import com.example.kzhu9.myapplication.TopicList;
import com.example.kzhu9.myapplication.TopicListAdapter;
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

/**
 * Created by jinliang on 11/15/15.
 */

public class TopicListFragment extends Fragment implements TopicItemClickListener, TopicItemLongClickListener {
    private RecyclerView recyclerView;
    private TopicListAdapter adapter;
    final ArrayList<TopicList.TopicEntity> topiList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        adapter = new FriendListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_listview, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.topicList);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new TopicListAdapter(getActivity().getApplicationContext());

        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

//
//        recyclerView.addItemDecoration(
//                new HorizontalDividerItemDecoration.Builder(getActivity())
//                        .color(Color.RED)
//                        .sizeResId(R.dimen.divider)
//                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
//                        .build());

        recyclerView.setAdapter(adapter);

        Log.i("CCcCCCCCCC", "cccc");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setHasFixedSize(true);

        getTopicUidList();

    }


    private void getTopicUidList() {

//        String url = "https://api.myjson.com/bins/3woab";

//        String url = "https://api.myjson.com/bins/4f5er";

        Log.i("CCcCCCCCCC", "ccccddddddddd");


        String requestURL =  Config.REQUESTURL+"/user/get";

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

                    JSONObject user = new JSONObject(responseStr);
                    System.out.print(user);
                    JSONObject info = user.getJSONObject("info");
                    JSONArray topicsList = info.getJSONArray("topics_list");

                    ArrayList<String> uidList = new ArrayList<>();

                    for (int i = 0; i < topicsList.length(); i++) {

                        uidList.add(topicsList.getString(i));
                    }
                    getTopicList(uidList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
            }
        });
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

                        JSONObject topic = new JSONObject(responseStr);
                        System.out.println("111111111111111");
                        System.out.println(topic);

                        JSONObject info = topic.getJSONObject("info");

                        System.out.println("siaudfho");
                        System.out.println(info);

                        topicEntity.setTitle(info.getString("title"));
                        topicEntity.setDescription(info.getString("desc"));
                        topicEntity.setVideo_uid(info.getString("video_uid"));

                        topiList.add(topicEntity);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (topiList.size() == size) {
                                    adapter.setList(topiList);
                                }
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        Log.i("KKKKKKKKK", "" + position);
        Log.i("KKKKKKKKK", "" + topiList.get(position).getTitle());
        Intent intent = new Intent(getActivity(), TopicInfo.class);

        intent.putExtra("TITLE", topiList.get(position).getTitle());
        intent.putExtra("DESCRIPTION", topiList.get(position).getDescription());
        intent.putExtra("LIKE", topiList.get(position).getLike());
        intent.putExtra("VIDEO", topiList.get(position).getVideo_uid());

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.i("KKKKKKKKK--------------", "" + position);

    }
}