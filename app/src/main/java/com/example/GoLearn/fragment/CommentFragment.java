package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.CommentAdapter;
import com.example.GoLearn.model.CommentItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private CommentAdapter adapter;
    private List<CommentItem> commentList;

    private DatabaseReference commentsRef;
    private String classId; // You should get this from intent or args

    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        recyclerView = view.findViewById(R.id.commentRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        classId = getArguments() != null ? getArguments().getString("classId") : null;

        if (classId == null) {
            Toast.makeText(getContext(), "Class ID is missing", Toast.LENGTH_SHORT).show();
            return view;
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not signed in", Toast.LENGTH_SHORT).show();
            return view;
        }

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList, currentUser.getUid());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        commentsRef = FirebaseDatabase.getInstance().getReference("classes")
                .child(classId).child("comments");

        loadCommentsRealtime();

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void loadCommentsRealtime() {
        commentsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentItem comment = snapshot.getValue(CommentItem.class);
                if (comment != null) {
                    commentList.add(comment);
                    adapter.notifyItemInserted(commentList.size() - 1);
                    recyclerView.scrollToPosition(commentList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) return;

        String commentId = commentsRef.push().getKey();
        String time = DateFormat.format("hh:mm a", new Date()).toString();

        // For demo, we'll use display name or fallback to email
        String senderName = currentUser.getDisplayName() != null ?
                currentUser.getDisplayName() : currentUser.getEmail();

        CommentItem comment = new CommentItem(commentId, currentUser.getUid(), senderName, message, time);

        if (commentId != null) {
            commentsRef.child(commentId).setValue(comment);
            messageInput.setText("");
        }
    }
}
