package com.example.mad_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 1-10 from Chapter 5", 'A', 100, false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Write a report on renewable energy sources", 'B', 100, true, "20"));
        assignmentList.add(new AssignmentItem("History Essay", "2025-05-05", "Discuss the causes of World War II", 'C', 100, false, "15"));
        assignmentList.add(new AssignmentItem("Art Project", "2025-05-06", "Create a painting inspired by nature", 'A', 100, true, "25"));
        assignmentList.add(new AssignmentItem("Computer Science Project", "2025-05-07", "Develop a simple calculator app", 'B', 100, false, "30"));
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 11-20 from Chapter 5", 'C', 100, false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Prepare a presentation on climate change", 'A', 100, true, "20"));

        adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
