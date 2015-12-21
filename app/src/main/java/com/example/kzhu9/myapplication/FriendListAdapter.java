package com.example.kzhu9.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jinliang on 11/15/15.
 */


public class FriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder> {

    private ArrayList<FriendList.FriendEntity> list;
    private FriendItemClickListener friendItemClickListener;
    private FriendItemLongClickListener friendItemLongClickListener;

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
        FriendListViewHolder holder = new FriendListViewHolder(view, friendItemClickListener, friendItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendListViewHolder holder, int position) {
        FriendList.FriendEntity friend = list.get(position);

        holder.nameText.setText(friend.getName());
        String sex = (friend.getSex() == 0) ? "Male" : "Female";
        holder.sexView.setText(sex);
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
