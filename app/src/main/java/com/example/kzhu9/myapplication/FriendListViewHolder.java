package com.example.kzhu9.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jinliang on 12/2/15.
 */
class FriendListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    TextView nameText;
    TextView sexView;
    private FriendItemClickListener mListener;
    private FriendItemLongClickListener mLongClickListener;

    public FriendListViewHolder(View itemView, FriendItemClickListener listener, FriendItemLongClickListener longClickListener) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.listText);
        sexView = (TextView) itemView.findViewById(R.id.item_sex);

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