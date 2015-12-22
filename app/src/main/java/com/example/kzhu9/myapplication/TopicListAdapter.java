package com.example.kzhu9.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jinliang on 12/2/15.
 */

public class TopicListAdapter extends RecyclerView.Adapter<TopicListViewHolder> {

    private ArrayList<TopicList.TopicEntity> list;

    private TopicItemClickListener topicItemClickListener;
    private TopicItemLongClickListener topicItemLongClickListener;
    TopicListViewHolder holder;

    public TopicListAdapter(ArrayList<TopicList.TopicEntity> data) {
        list = data;
    }

    public TopicListAdapter(Context context) {
        list = new ArrayList<>();
    }

    public void setList(ArrayList<TopicList.TopicEntity> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public TopicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_topic_list_item, parent, false);
        holder = new TopicListViewHolder(view, topicItemClickListener, topicItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(TopicListViewHolder holder, int position) {
        TopicList.TopicEntity topic = list.get(position);

        holder.tvTitle.setText(topic.getTitle());

        new DownloadImage().setHolder(holder).execute(topic.getImage_uid());
        holder.tvDescribe.setText(topic.getDescription());
        holder.tvLike.setText(String.valueOf(topic.getLike()));
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        TopicListViewHolder holder;
        public DownloadImage setHolder(TopicListViewHolder holder) {
            this.holder = holder;
            return this;
        }
        protected Bitmap  doInBackground(String... urls) {
            return getBitmapFromURL(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ImageView img = holder.topicImage;
                img.setImageBitmap(result);
            }
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(TopicItemClickListener listener){
        this.topicItemClickListener = listener;
    }

    public void setOnItemLongClickListener(TopicItemLongClickListener listener){
        this.topicItemLongClickListener = listener;
    }

}

