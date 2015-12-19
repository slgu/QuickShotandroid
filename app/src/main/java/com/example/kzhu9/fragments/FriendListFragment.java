package com.example.kzhu9.fragments;

/// /import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.FriendInfo;
import com.example.kzhu9.myapplication.FriendItemClickListener;
import com.example.kzhu9.myapplication.FriendItemLongClickListener;
import com.example.kzhu9.myapplication.FriendList;
import com.example.kzhu9.myapplication.FriendListAdapter;
import com.example.kzhu9.myapplication.OkHttpSingleton;
import com.example.kzhu9.myapplication.R;
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
public class FriendListFragment extends Fragment implements FriendItemClickListener, FriendItemLongClickListener {
    private RecyclerView recyclerView;
    private FriendListAdapter adapter;

    final ArrayList<FriendList.FriendEntity> friList = new ArrayList<FriendList.FriendEntity>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        adapter = new FriendListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.friendList);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new FriendListAdapter(getActivity().getApplicationContext());

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

//        Log.i("CCcCCCCCCC", "cccc");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setHasFixedSize(true);

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
                    JSONArray friendsListObj = info.getJSONArray("friends_list");

                    ArrayList<String> uidList = new ArrayList<>();

                    for (int i = 0; i < friendsListObj.length(); i++) {
                        uidList.add(friendsListObj.getString(i));
                    }
                    getFriendList(uidList);

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

    public void getFriendList(ArrayList<String> friendUidList) {
        final int size = friendUidList.size();

        for (String uid : friendUidList) {
            String requestURL = Config.REQUESTURL + "/user/get";

            RequestBody formBody = new FormEncodingBuilder()
                    .add("uid", uid)
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
                    JSONObject friendList;
                    JSONObject info;

                    try {
                        FriendList.FriendEntity friendEntity = new FriendList.FriendEntity();

                        friendList = new JSONObject(responseStr);
                        //System.out.print(friendList);
                        info = friendList.getJSONObject("info");

                        friendEntity.setName(info.getString("name"));
                        friendEntity.setAge(info.getInt("age"));
                        friendEntity.setAddress(info.getString("address"));

                        friList.add(friendEntity);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (friList.size() == size) {
                                    adapter.setList(friList);
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
        Intent intent = new Intent(getActivity(), FriendInfo.class);

        intent.putExtra("NAME", friList.get(position).getName());
        intent.putExtra("AGE", friList.get(position).getAge());
        intent.putExtra("EMAIL", friList.get(position).getEmail());
        intent.putExtra("ADDRESS", friList.get(position).getAddress());

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        
    }
}
