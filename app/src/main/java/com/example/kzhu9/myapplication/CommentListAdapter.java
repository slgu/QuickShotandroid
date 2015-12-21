package com.example.kzhu9.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
* Created by jinliang on 12/20/15.
*/
public class CommentListAdapter extends RecyclerView.Adapter<CommentListViewHolder> {

   private ArrayList<CommentItem> list;

   public CommentListAdapter(ArrayList<CommentItem> data) {
       list = data;
   }

   public CommentListAdapter(Context context) {
       list = new ArrayList<>();
   }

   //
   public void setList(ArrayList<CommentItem> data) {
//        list = data;
       list.clear();
       list.addAll(data);
//        notifyItemChanged(0, list.size());
       notifyDataSetChanged();
   }

   @Override
   public CommentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_comment_list_item, parent, false);
       CommentListViewHolder holder = new CommentListViewHolder(view);
       return holder;
   }

   @Override
   public void onBindViewHolder(CommentListViewHolder holder, int position) {
       CommentItem comment = list.get(position);

       holder.nameText.setText(comment.getName());
       holder.timeText.setText(comment.getTime());
       holder.commentText.setText(comment.getText());
   }

   @Override
   public int getItemCount() {
       return list.size();
   }

}
