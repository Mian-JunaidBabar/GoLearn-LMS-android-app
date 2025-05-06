package com.example.GoLearn.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.AddAssignmentActivity;
import com.example.GoLearn.R;
import com.example.GoLearn.adapter.AssignmentAdapter;
import com.example.GoLearn.model.AssignmentItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageHomeFragment extends Fragment {

    private TextView classTitleTextView, classDescTextView, classStatusTextView, classCodeLabel;
    private ImageButton btnCopyCode, btnShareCode;
    private Button addAssignmentButton;
    private RecyclerView assignmentsRecyclerView;
    private AssignmentAdapter assignmentAdapter;
    private ArrayList<AssignmentItem> assignmentList;
    private String classId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_home, container, false);

        // Get classId from arguments
        classId = getArguments() != null ? getArguments().getString("classId") : null;
        if (classId == null) {
            Toast.makeText(getContext(), "Error: classId not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize views
        classTitleTextView = view.findViewById(R.id.manage_class_title);
        classDescTextView = view.findViewById(R.id.manage_class_desc);
        classStatusTextView = view.findViewById(R.id.manage_class_status);
        classCodeLabel = view.findViewById(R.id.class_code_label);
        btnCopyCode = view.findViewById(R.id.btn_copy_code);
        btnShareCode = view.findViewById(R.id.btn_share_code);
        addAssignmentButton = view.findViewById(R.id.btn_add_assignment);
        assignmentsRecyclerView = view.findViewById(R.id.recycler_manage_assignments);

        // Setup RecyclerView
        assignmentList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(getContext(), assignmentList);
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsRecyclerView.setAdapter(assignmentAdapter);

        loadClassDetails();
        loadAssignments();

        // Copy class code
        btnCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Class Code", classId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Class code copied", Toast.LENGTH_SHORT).show();
        });

        // Share class code
        btnShareCode.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my class using this code: " + classId);
            startActivity(Intent.createChooser(shareIntent, "Share Class Code"));
        });

        addAssignmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddAssignmentActivity.class);
            intent.putExtra("classId", classId); // Pass classId to AddAssignmentActivity
            startActivity(intent);
        });

        return view;
    }

    private void loadClassDetails() {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                String desc = snapshot.child("description").getValue(String.class);
                String classCode = snapshot.child("classCode").getValue(String.class); // Fetch class code
                Long createdAt = snapshot.child("createdAt").getValue(Long.class);

                classTitleTextView.setText(title != null ? title : "N/A");
                classDescTextView.setText(desc != null ? desc : "N/A");
                classCodeLabel.setText("Code: " + (classCode != null ? classCode : "N/A")); // Display class code

                // Update button listeners with classCode
                btnCopyCode.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Class Code", classCode);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Class code copied", Toast.LENGTH_SHORT).show();
                });

                btnShareCode.setOnClickListener(v -> {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my class using this code: " + classCode);
                    startActivity(Intent.createChooser(shareIntent, "Share Class Code"));
                });

                if (createdAt != null) {
                    String formattedDate = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", createdAt).toString();
                    classStatusTextView.setText("Created At: " + formattedDate);
                } else {
                    classStatusTextView.setText("Created At: Unknown");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load class details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignments() {
        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference("assignments");
        assignRef.orderByChild("classId").equalTo(classId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignmentList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AssignmentItem item = snap.getValue(AssignmentItem.class);
                    if (item != null) {
                        assignmentList.add(item);
                    }
                }
                assignmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load assignments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
