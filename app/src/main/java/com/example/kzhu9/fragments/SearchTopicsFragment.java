package com.example.kzhu9.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.OkHttpSingleton;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicItems;
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

public class SearchTopicsFragment extends Fragment {
    SearchView search;
    View map;
    ListView searchResults;
    View rootview;
    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<TopicItems> topicResults = new ArrayList<TopicItems>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);
        inflater.inflate(R.menu.main_map, menu);

        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setQueryHint("Search Topics...");
        search.setIconifiedByDefault(false);

        searchResults = (ListView) rootview.findViewById(R.id.listview_searchtopics);

        search.setOnQueryTextListener(new OnQueryTextListener() {
            JSONArray topicList;
            String requestURL;
            ProgressDialog pd;

            @Override
            public boolean onQueryTextSubmit(String newText) {
                searchResults.setVisibility(View.VISIBLE);

                // Step 1. pre execute show pd
                topicList = new JSONArray();
                pd = new ProgressDialog(getActivity());
                pd.setCancelable(false);
                pd.setMessage("Searching...");
                pd.getWindow().setGravity(Gravity.CENTER);
                pd.show();

                // Step 2. Get data
                requestURL = Config.REQUESTURL + "/topic/find";
                RequestBody formBody = new FormEncodingBuilder()
                        .add("desc", newText)
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
                        System.out.println(responseStr);
                        try {
                            topicList = new JSONArray(responseStr);
                            System.out.print(topicList.length());
                            TopicItems tempTopic;

                            if (!topicResults.isEmpty())
                                topicResults.clear();
                            for (int i = 0; i < topicList.length(); i++) {
                                tempTopic = new TopicItems();

                                JSONObject obj = topicList.getJSONObject(i);

                                tempTopic.setName(obj.getString("title"));
                                tempTopic.setDescription(obj.getString("desc"));

                                topicResults.add(tempTopic);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    searchResults.setAdapter(new SearchResultsAdapter(getActivity(), topicResults));
                                    search.clearFocus();
                                }
                            });
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }

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

                System.out.println("on text chnge text: " + newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_map:
                // replace current fragment with map
                System.out.println("123");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_searchtopics, container, false);
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
            TopicItems tempTopic = topicDetails.get(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.searchtopicresult, null);
                holder = new ViewHolder();
                holder.itself = (RelativeLayout) convertView.findViewById(R.id.topicView);
                holder.topic_title = (TextView) convertView.findViewById(R.id.topic_title);
                holder.topic_description = (TextView) convertView.findViewById(R.id.topic_description_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.topic_title.setText(tempTopic.getName());
            holder.topic_description.setText(tempTopic.getDescription());

            return convertView;
        }

        class ViewHolder {
            RelativeLayout itself;
            TextView topic_title;
            TextView topic_description;
        }
    }
}

