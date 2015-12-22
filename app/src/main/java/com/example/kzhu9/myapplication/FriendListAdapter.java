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
        new DownloadImage().setHolder(holder).execute(friend.getImg_uid());
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        FriendListViewHolder holder;
        public DownloadImage setHolder(FriendListViewHolder holder) {
            this.holder = holder;
            return this;
        }
        protected Bitmap  doInBackground(String... urls) {
            System.out.println("doinbackground "+ urls[0] );
            return getBitmapFromURL(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            ImageView img = this.holder.friendImage;
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

    public void setOnItemClickListener(FriendItemClickListener listener){
        this.friendItemClickListener = listener;
    }

    public void setOnItemLongClickListener(FriendItemLongClickListener listener){
        this.friendItemLongClickListener = listener;
    }

}
