package com.example.kzhu9.fragments.sidebar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.kzhu9.myapplication.MapActivity;
import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicInfo;
import com.example.kzhu9.myapplication.TopicItems;
import com.example.kzhu9.myapplication.TopicList;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

/**
 * Created by kzhu9 on 11/7/15.
 */

public class SearchTopicsFragment extends Fragment implements OnMapReadyCallback {
    final ArrayList<TopicList.TopicEntity> topiList = new ArrayList<>();
    SearchView search;
    MenuItem locationItem, searchItem;
    ListView searchResults;
    FloatingActionButton fab;
    View rootview;
    ArrayList<TopicItems> topicResults = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_search, menu);
        inflater.inflate(R.menu.main_location, menu);

        searchItem = menu.findItem(R.id.action_search);
        search = (SearchView) searchItem.getActionView();
        locationItem = menu.findItem(R.id.action_location);
        search.setSubmitButtonEnabled(true);

        search.setQueryHint("Search Topics...");
//        search.setIconifiedByDefault(false);

        searchResults = (ListView) rootview.findViewById(R.id.listview_searchtopics);
        fab = (FloatingActionButton) rootview.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<TopicItems> arrayList = new ArrayList<TopicItems>();
                System.out.println(topiList.toString());
                for (TopicList.TopicEntity topicEntity: topiList) {
                    TopicItems topicItems = new TopicItems();
                    topicItems.setLatitude(topicEntity.getLat());
                    topicItems.setLongitude(topicEntity.getLon());
                    topicItems.setTitle(topicEntity.getTitle());
                    topicItems.setDescription(topicEntity.getDescription());
                    System.out.println("{}{}{}{}{}{}{}{}");
                    System.out.println(topicItems.getLatitude());
                    System.out.println(topicItems.getLongitude());
                    arrayList.add(topicItems);
                }
                System.out.println("Now printing arraylist size!");
                System.out.println(arrayList.size());

                if (arrayList.size() != 0) {
                    Intent intent = new Intent(getActivity(), MapActivity.class);

                    intent.putParcelableArrayListExtra("123", (ArrayList<? extends Parcelable>) arrayList);

                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Nothing to show", Toast.LENGTH_LONG).show();
                }
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        System.out.println("search item is closed");
                        setItemsVisibility(menu, searchItem, true);
                        return true;  // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        System.out.println("search item is clicked");
                        setItemsVisibility(menu, searchItem, false);
                        return true;  // Return true to expand action view
                    }
                }
        );

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
                        if (!topiList.isEmpty())
                            topiList.clear();
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
                                    search.clearFocus();

                                    searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getActivity(), TopicInfo.class);
                                            Collections.sort(topiList);

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
                    }
                });

                System.out.println("on query submit: " + newText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResults.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
                System.out.println("on text chnge text: " + newText);
                return true;
            }
        });
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                if (!topiList.isEmpty())
                    topiList.clear();
                String requestURL;
                final ProgressDialog pd;

                searchResults.setVisibility(View.VISIBLE);

                // Step 1. pre execute show pd
                pd = new ProgressDialog(getActivity());
                pd.setCancelable(false);
                pd.setMessage("Searching...");
                pd.getWindow().setGravity(Gravity.CENTER);
                pd.show();

                // Step 1.1 get Location information

                LocationManager lm = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this.getActivity(),
                            PERMISSIONS_LOCATION,
                            REQUEST_EXTERNAL_LOCATION
                    );
//                    return true;
                }
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Step 2. Get data
                requestURL = Config.REQUESTURL + "/topic/find";
                RequestBody formBody = new FormEncodingBuilder()
                        .add("lat", String.valueOf(latitude))
                        .add("lon", String.valueOf(longitude))
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
                                    pd.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), "Server is down!", Toast.LENGTH_LONG).show();
                                }
                            });

                            // need to re-login
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseStr = response.body().string();
                        System.out.println("Location button clicked");

                        try {
                            JSONArray topicList;
                            JSONObject responseObj = new JSONObject(responseStr);
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

                                System.out.println("uid " +obj.getString("uid")+ " title "+obj.getString("title"));

                                uidList.add(obj.getString("uid"));

                                topicResults.add(tempTopic);
                            }

                            getTopicList(uidList);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    // things shown after location button is clicked
//                                    searchResults.setAdapter(new SearchResultsAdapter(getActivity(), topicResults));
                                    search.clearFocus();

                                    // onclick on each item clicked
                                    searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getActivity(), TopicInfo.class);
//                                            Collections.sort(topiList);

                                            System.out.println(topiList.get(position).getDescription()+" is clicked");
                                            intent.putExtra("UID", topiList.get(position).getUid());
                                            intent.putExtra("TITLE", topiList.get(position).getTitle());
                                            intent.putExtra("DESCRIPTION", topiList.get(position).getDescription());
                                            intent.putExtra("LIKE", topiList.get(position).getLike());
                                            intent.putExtra("VIDEO", topiList.get(position).getVideo_uid());
                                            intent.putExtra("LAT", topiList.get(position).getLat());
                                            intent.putExtra("LON", topiList.get(position).getLon());
                                            intent.putExtra("COMMENTLIST", topiList.get(position).getComments_list());

                                            startActivity(intent);
                                            fab.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }

                        pd.dismiss();
                    }
                });



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_searchtopics, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Search Topics");
        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getTopicList(ArrayList<String> topicUidList) {
        final int size = topicUidList.size();
//        System.out.println("this is topicUidList size.");
//        System.out.println(size);
        if (!topiList.isEmpty())
            topiList.clear();

        for (final String uid : topicUidList) {
//            System.out.println("first time");
//            System.out.println(uid);


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
//                        System.out.println(uid);

                        TopicList.TopicEntity topicEntity = new TopicList.TopicEntity();

                        JSONObject responseObj = new JSONObject(responseStr);
                        System.out.println("Search Location Topic List Fragment Render Data");
                        System.out.println(responseObj);

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

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (topiList.size() == size) {

                                    System.out.println("setAdapter called");
                                    // sort topiList

//                                    Collections.sort(topiList);
                                    searchResults.setAdapter(new SearchResultsAdapter(getActivity(), topicResults));
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }
                        });


                        System.out.println(topiList.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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

