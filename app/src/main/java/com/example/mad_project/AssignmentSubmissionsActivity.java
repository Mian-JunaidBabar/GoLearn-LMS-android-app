package com.example.mad_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.adapter.AssignmentSubmissionAdapter;
import com.example.mad_project.model.AssignmentSubmissionItem;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionsActivity extends AppCompatActivity {

    private RecyclerView submissionsRecyclerView;
    private AssignmentSubmissionAdapter adapter;
    private List<AssignmentSubmissionItem> submissionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        // Initialize RecyclerView
        submissionsRecyclerView = findViewById(R.id.submissions_recycler_view);

        // Initialize the list of submissions (dummy data for now)
        submissionsList = new ArrayList<>();
        submissionsList.add(new AssignmentSubmissionItem("Ali", "assignment1.pdf", "https://example.com/assignment1.pdf", ""));
        submissionsList.add(new AssignmentSubmissionItem("Sana", "image.png", "https://example.com/image.png", ""));
        submissionsList.add(new AssignmentSubmissionItem("Bilal", "document.docx", "https://example.com/doc.docx", "10"));

        // Initialize the adapter and set it to the RecyclerView
        adapter = new AssignmentSubmissionAdapter(this, submissionsList);
        submissionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        submissionsRecyclerView.setAdapter(adapter);
    }
}