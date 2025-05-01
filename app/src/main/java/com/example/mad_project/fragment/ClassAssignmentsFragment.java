package com.example.mad_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.AssignmentDetailActivity;
import com.example.mad_project.R;
import com.example.mad_project.adapter.*;
import com.example.mad_project.model.*;

import java.util.ArrayList;
import java.util.List;

public class ClassAssignmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<AssignmentItem> assignmentList;

    public ClassAssignmentsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_assignments, container, false);

        recyclerView = view.findViewById(R.id.assignmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        assignmentList = new ArrayList<>();
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 1-10 from Chapter 5", "10 points"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Write a report on renewable energy sources", "20 points"));
        assignmentList.add(new AssignmentItem("History Essay", "2025-05-05", "Discuss the causes of World War II", "15 points"));
        assignmentList.add(new AssignmentItem("Art Project", "2025-05-06", "Create a painting inspired by nature", "25 points"));
        assignmentList.add(new AssignmentItem("Computer Science Project", "2025-05-07", "Develop a simple calculator app", "30 points"));
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 11-20 from Chapter 5", "10 points"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Prepare a presentation on climate change", "20 points"));

        adapter = new AssignmentAdapter(getContext(), assignmentList, item -> {
            // Start a new activity and pass assignment details
            Intent intent = new Intent(getContext(), AssignmentDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("dueDate", item.getDueDate());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("points", item.getPoints());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
