package com.example.mad_project.fragment;

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

import com.example.mad_project.AddAssignmentActivity;
import com.example.mad_project.R;
import com.example.mad_project.adapter.AssignmentAdapter;
import com.example.mad_project.model.AssignmentItem;

import java.util.ArrayList;

public class ManageHomeFragment extends Fragment {

    private TextView classTitleTextView, classDescTextView, classStatusTextView, classCodeLabel;
    private ImageButton btnCopyCode, btnShareCode;
    private Button addAssignmentButton;
    private RecyclerView assignmentsRecyclerView;
    private AssignmentAdapter assignmentAdapter;
    private ArrayList<AssignmentItem> assignmentList;
    private String classCode = "ABC123"; // Example class code

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_home, container, false);

        // Initialize views
        classTitleTextView = view.findViewById(R.id.manage_class_title);
        classDescTextView = view.findViewById(R.id.manage_class_desc);
        classStatusTextView = view.findViewById(R.id.manage_class_status);
        classCodeLabel = view.findViewById(R.id.class_code_label);
        btnCopyCode = view.findViewById(R.id.btn_copy_code);
        btnShareCode = view.findViewById(R.id.btn_share_code);
        addAssignmentButton = view.findViewById(R.id.btn_add_assignment);
        assignmentsRecyclerView = view.findViewById(R.id.recycler_manage_assignments);

        // Set class details
        classTitleTextView.setText("AI Advanced");
        classDescTextView.setText("Deep Learning Project");
        classStatusTextView.setText("Status: Active");
        classCodeLabel.setText("Code: " + classCode);

        // Copy class code to clipboard
        btnCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Class Code", classCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Class code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Share class code
        btnShareCode.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my class using this code: " + classCode);
            startActivity(Intent.createChooser(shareIntent, "Share Class Code"));
        });

        // Initialize assignment list
        assignmentList = new ArrayList<>();
        assignmentList.add(new AssignmentItem("Assignment 1", "2023-10-01", "Submit research topic", "10 points"));
        assignmentList.add(new AssignmentItem("Assignment 2", "2023-10-15", "Upload model architecture", "20 points"));

        // Set up RecyclerView
        assignmentAdapter = new AssignmentAdapter(getContext(), assignmentList);
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsRecyclerView.setAdapter(assignmentAdapter);

        // Add assignment button click listener
        addAssignmentButton.setOnClickListener(v -> {
            // Handle add assignment action
            Intent intent = new Intent(getContext(), AddAssignmentActivity.class);
            startActivity(intent);
        });

        return view;
    }
}