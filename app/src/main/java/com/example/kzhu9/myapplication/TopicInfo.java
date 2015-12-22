package com.example.kzhu9.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kzhu9.config.Config;
import com.example.kzhu9.myapplication.okhttp_singleton.OkHttpSingleton;
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

public class TopicInfo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Button btComment;
    EditText edComment;
    String topicUid;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommentListAdapter mAdapter;
    private ArrayList<CommentItem> commentsData = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_info);
        mLayoutManager = new LinearLayoutManager(this);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_comment);


        topicUid = getIntent().getExtras().getString("UID");
        String title = getIntent().getExtras().getString("TITLE");
        String description = getIntent().getExtras().getString("DESCRIPTION");
        int like = getIntent().getExtras().getInt("LIKE");
        String video = getIntent().getExtras().getString("VIDEO");
//        ArrayList<String> com = getIntent().getExtras().getParcelableArrayList("COMMENTLIST");
        String commentList = getIntent().getExtras().getParcelableArrayList("COMMENTLIST").toString();
        String comments = commentList.substring(1, commentList.length() - 1);

        JSONArray temp = new JSONArray();

//        final ArrayList<CommentItem> commentsData = new ArrayList<>();

        try {
            temp = new JSONArray(comments);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject com = temp.getJSONObject(i);
                CommentItem comItem = new CommentItem();
                comItem.setName(com.getString("name"));
                comItem.setText(com.getString("text"));
                comItem.setTime(com.getString("time"));
                commentsData.add(comItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_list_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

//        commentsData = new ArrayList<CommentItem>();
//        getCommentList();

//        System.out.println("oncreate");
//        System.out.println(commentsData.size());

        mAdapter = new CommentListAdapter(commentsData);
        mRecyclerView.setAdapter(mAdapter);

        swipeContainer.setOnRefreshListener(this);

//        try {
//            Thread.sleep(500);
//            swipeContainer.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    swipeContainer.setRefreshing(true);
//                    dosomething();
//                }
//            });
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        VideoView videoView = (VideoView) findViewById(R.id.videoView);
//
        Uri vidUri = Uri.parse(video);
        videoView.setVideoURI(vidUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
//        videoView.start();


        btComment = (Button) findViewById(R.id.btComment);
        edComment = (EditText) findViewById(R.id.etComment);

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.describe)).setText(description);
        ((TextView) findViewById(R.id.like)).setText(String.valueOf(like));
        ((TextView) findViewById(R.id.video)).setText(video);
//        ((TextView) findViewById(R.id.commentContent)).setText(comments);

        final String topicId = getIntent().getExtras().getString("UID");
        btComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btComment:
                        String requestURL = Config.REQUESTURL + "/topic/comment";
                        System.out.println(topicId);

                        RequestBody formBody = new FormEncodingBuilder()
                                .add("tid", topicId)
                                .add("comment", edComment.getText().toString())
                                .build();
                        Request request = new Request.Builder()
                                .url(requestURL)
                                .post(formBody)
                                .build();

                        final CommentItem item = new CommentItem();
                        item.setText(edComment.getText().toString());

                        OkHttpSingleton.getInstance().getClient(getApplicationContext()).newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException throwable) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                String responseStr = response.body().string();
//                                System.out.println(responseStr);

                                Gson gson = new Gson();
                                JsonObject responseJsonObject = gson.fromJson(responseStr, JsonObject.class);
                                int status = Integer.parseInt(responseJsonObject.get("status").toString());

                                String resultStr = null;
                                switch (status) {
                                    case 0:
                                        resultStr = "Done adding comment!";
                                        // update comment list
                                        commentsData.add(item);
                                        break;
                                    case 1:
                                        // go back to login activity ???????????????
                                        break;
                                    case 2:
                                        resultStr = "Invalid comment message!";
                                        break;
                                    case 3:
                                        resultStr = "No topic id information!";
                                        break;
                                }
                                if (resultStr != null) {
                                    final String tmp = resultStr;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

//                                Headers responseHeaders = response.headers();
//                                for (int i = 0; i < responseHeaders.size(); i++) {
//                                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("TopicInfo is resumed");
//        getCommentList();
//        mAdapter.setList(commentsData);
    }

    public void dosomething() {
        swipeContainer.setRefreshing(true);

        getCommentList();
        System.out.println("commentsData size");
        System.out.println(commentsData.size());

        try {
            Thread.sleep(2000);
            mAdapter.setList(commentsData);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        swipeContainer.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        dosomething();
    }

    public void getCommentList() {
        String requestURL = Config.REQUESTURL + "/topic/get";

        RequestBody formBody = new FormEncodingBuilder()
                .add("uid", topicUid)
                .build();
        Request request = new Request.Builder()
                .url(requestURL)
                .post(formBody)
                .build();

        if (this == null) {
            System.out.println("can't get activity");
            return;
        }

        OkHttpSingleton.getInstance().getClient(this.getBaseContext()).newCall(request).enqueue(new Callback() {

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

                    JSONObject responseObj = new JSONObject(responseStr);

                    JSONObject info = responseObj.getJSONObject("info");

//                    System.out.println("print comment_list");
                    JSONArray array = info.getJSONArray("comment_list");
//                    System.out.println(commentStr);
//                    ArrayList<String> commentList = new ArrayList<String>(Arrays.asList(commentStr.split(",")));
//                    System.out.println(commentList.toString());
//                    topicEntity.setComments_list(commentList);

//                    String comments = commentStr.substring(1, commentStr.length() - 1);

//                    JSONObject temp;
                    try {

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject com = array.getJSONObject(i);
                            CommentItem comItem = new CommentItem();
                            comItem.setName(com.getString("name"));
                            comItem.setText(com.getString("text"));
                            comItem.setTime(com.getString("time"));
                            commentsData.add(comItem);
                            System.out.println(com.getString("name") +" "+com.getString("text")+ " "+com.getString("time"));
                        }

//                        mAdapter.setList(commentsData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (this == null) {
                        System.out.println("can't get activity");
                        return;
                    }

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            if (topiList.size() == size) {
//                                System.out.println("adapter.setList(topiList); called");
//                                // sort topiList
//
//                                Collections.sort(topiList);
//                                adapter.setList(topiList);
//                            }
//                        }
//                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

