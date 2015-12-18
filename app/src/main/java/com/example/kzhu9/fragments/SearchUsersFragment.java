package com.example.kzhu9.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.FriendInfo;
import com.example.kzhu9.myapplication.FriendItems;
import com.example.kzhu9.myapplication.OkHttpSingleton;
import com.example.kzhu9.myapplication.R;
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

/**
 * Created by kzhu9 on 11/7/15.
 */

public class SearchUsersFragment extends Fragment {
    SearchView search;
    ListView searchResults;
    View rootview;
    ArrayList<FriendItems> friendResults = new ArrayList<FriendItems>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);

        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setQueryHint("Search Users...");

        searchResults = (ListView) rootview.findViewById(R.id.listview_searchfriends);

        search.setOnQueryTextListener(new OnQueryTextListener() {
            JSONArray friendList;
            String requestURL;
            ProgressDialog pd;

            @Override
            public boolean onQueryTextSubmit(String newText) {
                searchResults.setVisibility(View.VISIBLE);

                // Step 1. pre execute show pd
                friendList = new JSONArray();
                pd = new ProgressDialog(getActivity());
                pd.setCancelable(false);
                pd.setMessage("Searching...");
                pd.getWindow().setGravity(Gravity.CENTER);
                pd.show();

                // Step 2. Get data
                requestURL = Config.REQUESTURL + "/user/find";

                RequestBody formBody = new FormEncodingBuilder()
                        .add("username", newText)
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
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Unable to connect to server, please try later", Toast.LENGTH_LONG).show();
                            }
                        });
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        pd.dismiss();

                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        String responseStr = response.body().string();
                        System.out.println(responseStr);
                        try {
                            friendList = new JSONArray(responseStr);
                            FriendItems tempFriend;

                            if (!friendResults.isEmpty())
                                friendResults.clear();
                            for (int i = 0; i < friendList.length(); i++) {
                                tempFriend = new FriendItems();

                                JSONObject obj = friendList.getJSONObject(i);

                                tempFriend.setUid(obj.getString("uid"));
                                tempFriend.setName(obj.getString("name"));
                                tempFriend.setEmail(obj.getString("email"));
                                tempFriend.setSex(obj.getInt("sex"));
                                tempFriend.setTopicList(obj.getString("topics_list"));
                                tempFriend.setFriendList(obj.getString("friends_list"));

                                // if the user is not friend yet

                                if (obj.getString("friendTag").equals("0"))
                                    friendResults.add(tempFriend);

                                System.out.println(tempFriend.toString());
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    searchResults.setAdapter(new SearchResultsAdapter(getActivity(), friendResults));
                                    search.clearFocus();


                                    searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Log.i("CCCC", "----------");

                                            Intent intent = new Intent(getActivity(), FriendInfo.class);

                                            intent.putExtra("NAME", friendResults.get(position).getName());
                                            intent.putExtra("EMAIL", friendResults.get(position).getEmail());

                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }

                        pd.dismiss();
                        Headers responseHeaders = response.headers();
                        for (int i = 0; i < responseHeaders.size(); i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                        }
                    }
                });

                System.out.println("on query submit: " + newText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResults.setVisibility(View.INVISIBLE);
                System.out.println("on text chnge text: " + newText);
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_searchfriends, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class SearchResultsAdapter extends BaseAdapter {
        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<FriendItems> friendDetails = new ArrayList<>();

        //constructor method
        public SearchResultsAdapter(Context context, ArrayList<FriendItems> friend_details) {
            layoutInflater = LayoutInflater.from(context);
            this.friendDetails = friend_details;
            this.count = friend_details.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return friendDetails.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final FriendItems tempFriend = friendDetails.get(position);
            //System.out.println(tempFriend.toString());

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.searchfriendresult, null);
                holder = new ViewHolder();
                holder.itself = (RelativeLayout) convertView.findViewById(R.id.friendView);
                holder.add_friend = (ImageView) convertView.findViewById(R.id.add_friend);
                holder.friend_name = (TextView) convertView.findViewById(R.id.friend_name);
                holder.friend_sex = (TextView) convertView.findViewById(R.id.friend_sex_value);
                holder.friend_email = (TextView) convertView.findViewById(R.id.friend_email_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.friend_name.setText(tempFriend.getName());
            String strSex = (tempFriend.getSex() == 0) ? "Male" : "female";
            holder.friend_sex.setText(strSex);
            holder.friend_email.setText(tempFriend.getEmail());

            holder.add_friend.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    String uid = tempFriend.getUid();

                    String url = Config.REQUESTURL + "/user/add";

                    RequestBody formBody = new FormEncodingBuilder()
                            .add("uid", uid)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
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
                            System.out.println(responseStr);

                            Gson gson = new Gson();
                            JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                            int status = Integer.parseInt(responseJsonObject.get("status").toString());
                            String resultStr = null;
                            switch (status) {
                                case 0:
                                    resultStr = "Successfully add " + tempFriend.getName().toString() + " as friend!";
                                    break;
                                case 1:
                                    resultStr = "Server restarted! Need to login again!";
                                    // terminate the app and relogin
                                    break;
                                case 4:
                                    resultStr = "Already sent this user";
                                    break;
                            }
                            if (resultStr != null) {
                                final String tmp = resultStr;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), tmp, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

            return convertView;
        }

        class ViewHolder {
            RelativeLayout itself;
            TextView friend_name;
            ImageView add_friend;
            TextView friend_sex;
            TextView friend_email;
        }
    }
}

