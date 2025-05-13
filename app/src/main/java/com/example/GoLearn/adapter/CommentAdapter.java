package com.example.GoLearn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.model.CommentItem;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private List<CommentItem> commentList;

    public CommentAdapter(List<CommentItem> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position).isSentByMe() ? TYPE_SENT : TYPE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = (viewType == TYPE_SENT) ? R.layout.item_comment_sent : R.layout.item_comment_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MessageViewHolder) holder).bind(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, senderText, timeText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderText = itemView.findViewById(R.id.sender_name);
            timeText = itemView.findViewById(R.id.message_time);
        }

        public void bind(CommentItem comment) {
            messageText.setText(comment.getMessage());
            senderText.setText(comment.getSender());
            timeText.setText(comment.getTime());
        }
    }
}
