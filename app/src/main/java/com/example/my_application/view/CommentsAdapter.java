package com.example.my_application.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_application.Log;
import com.example.my_application.R;
import com.example.my_application.db.CommentEntity;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<CommentEntity> comments;

    public CommentsAdapter(ArrayList<CommentEntity> comments){
        this.comments = comments;
        for (CommentEntity comment : comments) {
            Log.debug(comment.toString());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvComment;
        TextView tvName;
        TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvComment = itemView.findViewById(R.id.tv_comment_body);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvDate = itemView.findViewById(R.id.tv_comment_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentEntity comment = comments.get(position);
        holder.tvComment.setText(comment.comment);
        holder.tvDate.setText(comment.date);
        holder.tvName.setText(comment.commentPhone);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
