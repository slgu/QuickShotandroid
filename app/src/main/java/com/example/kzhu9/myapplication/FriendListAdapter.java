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

import com.example.kzhu9.cache.ImgCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jinliang on 11/15/15.
 */


public class FriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder> {

    private ArrayList<FriendList.FriendEntity> list;
    private FriendItemClickListener friendItemClickListener;
    private FriendItemLongClickListener friendItemLongClickListener;
    FriendListViewHolder holder;

    public FriendListAdapter(ArrayList<FriendList.FriendEntity> data) {
        list = data;
    }

    public FriendListAdapter(Context context) {
        list = new ArrayList<>();
    }

    public void setList(ArrayList<FriendList.FriendEntity> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_friend_list_item, parent, false);
        holder = new FriendListViewHolder(view, friendItemClickListener, friendItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendListViewHolder holder, int position) {
        FriendList.FriendEntity friend = list.get(position);
        holder.nameText.setText(friend.getName());
        String sex = (friend.getSex() == 0) ? "Male" : "Female";
        holder.sexView.setText(sex);
        Bitmap cacheRes = ImgCache.single().get(friend.getImg_uid());
        if (cacheRes == null) {
            //async load
            new DownloadImage().setHolder(holder).setUrl(friend.getImg_uid()).execute(friend.getImg_uid());
        }
        else {
            holder.friendImage.setImageBitmap(cacheRes);
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        FriendListViewHolder holder;
        public DownloadImage setHolder(FriendListViewHolder holder) {
            this.holder = holder;
            return this;
        }
        String url;
        public DownloadImage setUrl(String url) {
            this.url = url;
            return this;
        }
        protected Bitmap  doInBackground(String... urls) {
            return getBitmapFromURL(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            ImageView img = this.holder.friendImage;
            //add to cache
            ImgCache.single().put(url, result);
            img.setImageBitmap(result);
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(FriendItemClickListener listener){
        this.friendItemClickListener = listener;
    }

    public void setOnItemLongClickListener(FriendItemLongClickListener listener){
        this.friendItemLongClickListener = listener;
    }

}
