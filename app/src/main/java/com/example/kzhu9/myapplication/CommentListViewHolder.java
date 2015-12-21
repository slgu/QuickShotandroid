package com.example.kzhu9.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
* Created by jinliang on 12/20/15.
*/
public class CommentListViewHolder extends RecyclerView.ViewHolder {

   TextView nameText;
   TextView timeText;
   TextView commentText;

   public CommentListViewHolder(View itemView) {
       super(itemView);

       nameText = (TextView) itemView.findViewById(R.id.commentUserName);
       timeText = (TextView) itemView.findViewById(R.id.commentTime);
       commentText = (TextView) itemView.findViewById(R.id.commentContent);

   }
}