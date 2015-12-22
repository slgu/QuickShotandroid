package com.example.kzhu9.fragments.sidebar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.MainActivity;
import com.example.kzhu9.myapplication.NotificationItems;
import com.example.kzhu9.myapplication.R;
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

/**
 * Created by kzhu9 on 11/7/15.
 */

public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    ListView searchResults;
    View rootview;
    ArrayList<NotificationItems> notificationResults = new ArrayList<NotificationItems>();
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        searchResults = (ListView) rootview.findViewById(R.id.listview_notifications);
        searchResults.setVisibility(View.VISIBLE);

        getNotifications();
    }

    public void getNotifications() {
        String requestURL;
        final ProgressDialog pd;

        // Step 1. pre execute show pd
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Searching...");
        pd.getWindow().setGravity(Gravity.CENTER);
        pd.show();

        // Step 2. Get data
        requestURL = Config.REQUESTURL + "/user/notify";

        RequestBody formBody = new FormEncodingBuilder()
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
                    Gson gson = new Gson();
                    JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);

                    String info = responseJsonObject.get("info").getAsString();
                    // int status = Integer.parseInt(responseJsonObject.get("status").toString());
//                    JSONObject [] res = gson.fromJson(info, JSONObject[].class);
//                    for (JSONObject obj: res)
//                        System.out.println(obj);
                    JSONArray notificationList = new JSONArray(info);
                    NotificationItems tempNotification;

                    if (!notificationResults.isEmpty())
                        notificationResults.clear();
                    for (int i = 0; i < notificationList.length(); i++) {
                        tempNotification = new NotificationItems();

                        JSONObject obj = notificationList.getJSONObject(i);
                        System.out.println(obj.toString());

                        tempNotification.setUid(obj.getString("uid"));
                        tempNotification.setName(obj.getString("name"));

                        notificationResults.add(tempNotification);

                        System.out.println(tempNotification.toString());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            searchResults.setAdapter(new searchResultsAdapter(getActivity(), notificationResults));
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
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_notifications, container, false);
        swipeContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeContainer_notification);
        ((MainActivity) getActivity()).setActionBarTitle("Notifications");
        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeContainer.setOnRefreshListener(this);
    }

    public void dosomething() {
        swipeContainer.setRefreshing(true);
        getNotifications();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getActivity(), "Notification Refreshed!", Toast.LENGTH_LONG).show();
            }
        });

        swipeContainer.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        dosomething();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class searchResultsAdapter extends BaseAdapter {
        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<NotificationItems> notificationDetails = new ArrayList<>();

        //constructor method
        public searchResultsAdapter(Context context, ArrayList<NotificationItems> notification_details) {
            layoutInflater = LayoutInflater.from(context);
            this.notificationDetails = notification_details;
            this.count = notification_details.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return notificationDetails.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final NotificationItems tempNotification = notificationDetails.get(position);
            //System.out.println(tempNotification.toString());

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.shownotification, null);
                holder = new ViewHolder();
                holder.itself = (RelativeLayout) convertView.findViewById(R.id.notificationView);
                holder.accept_invitation = (ImageView) convertView.findViewById(R.id.accept_invitation);
                holder.inviting_friend_name = (TextView) convertView.findViewById(R.id.inviting_friend_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.inviting_friend_name.setText(tempNotification.getName() + " want to add you as friend.");

            holder.accept_invitation.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    String uid = tempNotification.getUid();

                    String url = Config.REQUESTURL + "/user/ack";

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

                            Gson gson = new Gson();
                            JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                            int status = Integer.parseInt(responseJsonObject.get("status").toString());

                            String resultStr = null;
                            switch (status) {
                                case 0:
                                    resultStr = "Successfully accept " + " as friend!";
                                    break;
                                case 1:
                                    // go back to login activity ???????????????
                                    break;
                                case 4:
                                    resultStr = "Already added this user";
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
            TextView inviting_friend_name;
            ImageView accept_invitation;
        }
    }
}

