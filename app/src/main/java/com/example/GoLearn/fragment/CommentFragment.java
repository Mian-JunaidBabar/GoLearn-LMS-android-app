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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private List<CommentItem> commentList;
    private CommentAdapter adapter;
    private DatabaseReference commentsRef;
    private String currentUserId, currentUserName, classId = "classId123"; // replace with actual dynamic classId

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerView = view.findViewById(R.id.commentRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        commentsRef = FirebaseDatabase.getInstance().getReference("classes").child(classId).child("comments");

        usersRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadComments();

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                String commentId = commentsRef.push().getKey();
                String currentTime = DateFormat.format("hh:mm a", new Date()).toString();
                CommentItem comment = new CommentItem(commentId, currentUserName, message, currentTime, true);

                commentsRef.child(commentId).setValue(comment);
                messageInput.setText("");
            }
        });

        return view;
    }

    private void loadComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CommentItem comment = data.getValue(CommentItem.class);
                    if (comment != null) {
                        comment.setSentByMe(comment.getSender().equals(currentUserName));
                        commentList.add(comment);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(commentList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
