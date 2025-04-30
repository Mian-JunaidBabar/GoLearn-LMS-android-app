package com.example.mad_project.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.adapter.CommentAdapter;
import com.example.mad_project.model.CommentItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassCommentsFragment extends Fragment {

    private List<CommentItem> commentList;
    private CommentAdapter adapter;
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageView sendButton;

    public ClassCommentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_comments, container, false);

        recyclerView = view.findViewById(R.id.commentRecyclerView);
        inputMessage = view.findViewById(R.id.editMessage);
        sendButton = view.findViewById(R.id.sendButton);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                String currentTime = DateFormat.format("hh:mm a", new Date()).toString();
                commentList.add(new CommentItem("You", message, currentTime));
                adapter.notifyItemInserted(commentList.size() - 1);
                inputMessage.setText("");
                recyclerView.scrollToPosition(commentList.size() - 1);
            }
        });

        return view;
    }
}
