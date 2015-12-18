package com.example.kzhu9.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



/**
 * Created by jinliang on 12/5/15.
 */

public class TopicListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    TextView tvTitle;
    TextView tvDescribe;
    TextView tvLike;

    private TopicItemClickListener mListener;
    private TopicItemLongClickListener mLongClickListener;

    public TopicListViewHolder(View itemView, TopicItemClickListener listener, TopicItemLongClickListener longClickListener) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDescribe = (TextView) itemView.findViewById(R.id.tvDescribe);
        tvLike = (TextView) itemView.findViewById(R.id.tvLike);
        this.mListener = listener;
        this.mLongClickListener = longClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(mLongClickListener != null){
            mLongClickListener.onItemLongClick(v, getAdapterPosition());
        }
        return true;
    }
}
