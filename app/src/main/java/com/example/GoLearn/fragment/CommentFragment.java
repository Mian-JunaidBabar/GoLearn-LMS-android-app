package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.CommentAdapter;
import com.example.GoLearn.model.CommentItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private List<CommentItem> commentList;
    private CommentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerView = view.findViewById(R.id.commentRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        commentList = new ArrayList<>();
        // Sample static messages with time
        commentList.add(new CommentItem("Teacher", "Welcome to the class!", "10:00 AM", false));
        commentList.add(new CommentItem("Student", "Thank you, sir!", "10:05 AM", true));

        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                String currentTime = DateFormat.format("hh:mm a", new Date()).toString();
                commentList.add(new CommentItem("Student", message, currentTime, true));
                adapter.notifyItemInserted(commentList.size() - 1);
                recyclerView.scrollToPosition(commentList.size() - 1);
                messageInput.setText("");
            }
        });

        return view;
    }
}