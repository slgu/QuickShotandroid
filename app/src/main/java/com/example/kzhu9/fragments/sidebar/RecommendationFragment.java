package com.example.kzhu9.fragments.sidebar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.kzhu9.myapplication.MainActivity;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicInfo;
import com.example.kzhu9.myapplication.TopicItems;
import com.example.kzhu9.myapplication.TopicList;
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
import java.util.Arrays;

/**
 * Created by kzhu9 on 11/7/15.
 */

public class RecommendationFragment extends Fragment {
    final ArrayList<TopicList.TopicEntity> topiList = new ArrayList<>();
    ListView searchResults;
    View rootview;
    ArrayList<TopicItems> topicResults = new ArrayList<TopicItems>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        String requestURL;
        final ProgressDialog pd;

        searchResults = (ListView) rootview.findViewById(R.id.listview_recommendations);
        searchResults.setVisibility(View.VISIBLE);

        // Step 1. pre execute show pd
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Searching...");
        pd.getWindow().setGravity(Gravity.CENTER);
        pd.show();

        // Step 2. Get data
        requestURL = Config.REQUESTURL + "/topic/recommend";

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
                JSONArray topicList;
                if (!response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Server is down!", Toast.LENGTH_LONG).show();
                        }
                    });

                    // need to re-login
                    throw new IOException("Unexpected code " + response);
                }

                String responseStr = response.body().string();
                System.out.println("topic find reponse format");
                System.out.println(responseStr);
                try {
                    JSONObject responseObj = new JSONObject(responseStr);
                    System.out.println("Topic Search List Fragment Get Data");
                    System.out.println(responseObj);
                    topicList = responseObj.getJSONArray("info");

                    TopicItems tempTopic;

                    ArrayList<String> uidList = new ArrayList<>();

                    if (!topicResults.isEmpty())
                        topicResults.clear();
                    for (int i = 0; i < topicList.length(); i++) {
                        tempTopic = new TopicItems();

                        JSONObject obj = topicList.getJSONObject(i);

                        tempTopic.setUid(obj.getString("uid"));
                        tempTopic.setTitle(obj.getString("title"));
                        tempTopic.setDescription(obj.getString("desc"));

                        uidList.add(obj.getString("uid"));

                        topicResults.add(tempTopic);
                    }

                    getTopicList(uidList);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            searchResults.setAdapter(new SearchResultsAdapter(getActivity(), topicResults));

                            searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(), TopicInfo.class);

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
    }

    public void getTopicList(ArrayList<String> topicUidList) {
        final int size = topicUidList.size();
        System.out.println("this is topicUidList size.");
        System.out.println(size);

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

                        JSONObject responseObj = new JSONObject(responseStr);
                        System.out.println("Recommendation Fragment Fragment Render Data");
                        System.out.println(responseObj);

                        JSONObject info = responseObj.getJSONObject("info");

                        System.out.println(info);

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_recommendations, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Recommendations");
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
        private ArrayList<TopicItems> topicDetails = new ArrayList<TopicItems>();

        //constructor method
        public SearchResultsAdapter(Context context, ArrayList<TopicItems> topic_details) {
            layoutInflater = LayoutInflater.from(context);
            this.topicDetails = topic_details;
            this.count = topic_details.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return topicDetails.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final TopicItems tempTopic = topicDetails.get(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.searchtopicresult, null);
                holder = new ViewHolder();
                holder.itself = (RelativeLayout) convertView.findViewById(R.id.topicView);
                holder.like_topic = (ImageView) convertView.findViewById(R.id.like_topic);
                holder.topic_title = (TextView) convertView.findViewById(R.id.topic_title);
                holder.topic_description = (TextView) convertView.findViewById(R.id.topic_description_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.topic_title.setText(tempTopic.getTitle());
            holder.topic_description.setText(tempTopic.getDescription());

            holder.like_topic.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    String uid = tempTopic.getUid();

                    String url = Config.REQUESTURL + "/user/addlike";

                    RequestBody formBody = new FormEncodingBuilder()
                            .add("tid", uid)
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
                                    resultStr = "Successfully like " + tempTopic.getTitle().toString();
                                    break;
                                case 1:
                                    resultStr = "Server restarted! Need to login again!";
                                    // terminate the app and relogin
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
            ImageView like_topic;
            TextView topic_title;
            TextView topic_description;
        }
    }
}

