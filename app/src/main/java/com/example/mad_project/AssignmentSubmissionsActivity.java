package com.example.mad_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.adapter.StudentSubmissionAdapter;
import com.example.mad_project.model.StudentSubmissionItem;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionsActivity extends AppCompatActivity {

    private RecyclerView submissionsRecyclerView;
    private StudentSubmissionAdapter adapter;
    private List<StudentSubmissionItem> submissionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        // Initialize RecyclerView
        submissionsRecyclerView = findViewById(R.id.submissions_recycler_view);

        // Initialize the list of submissions (dummy data for now)
        submissionsList = new ArrayList<>();
        submissionsList.add(new StudentSubmissionItem("John Doe", "12/05/2025", "file.pdf"));
        submissionsList.add(new StudentSubmissionItem("Jane Smith", "11/05/2025", "file2.docx"));

        // Initialize the adapter and set it to the RecyclerView
        adapter = new StudentSubmissionAdapter(submissionsList);
        submissionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        submissionsRecyclerView.setAdapter(adapter);
    }
}