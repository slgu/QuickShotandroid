package com.example.kzhu9.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by jinliang on 12/2/15.
 */

public class LikedTopicListAdapter extends RecyclerView.Adapter<TopicListViewHolder> {

    private ArrayList<TopicList.TopicEntity> list;

    private TopicItemClickListener topicItemClickListener;
    private TopicItemLongClickListener topicItemLongClickListener;

    public LikedTopicListAdapter(ArrayList<TopicList.TopicEntity> data) {
        list = data;
    }

    public LikedTopicListAdapter(Context context) {
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
        TopicListViewHolder holder = new TopicListViewHolder(view, topicItemClickListener, topicItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(TopicListViewHolder holder, int position) {
        TopicList.TopicEntity topic = list.get(position);

        holder.tvTitle.setText(topic.getTitle());
        holder.tvDescribe.setText(topic.getDescription());
        holder.tvLike.setText(String.valueOf(topic.getLike()));
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

