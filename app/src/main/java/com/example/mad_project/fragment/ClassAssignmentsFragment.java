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
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", true, "20"));
        assignmentList.add(new AssignmentItem("History Essay", "2025-05-05", false, "15"));
        assignmentList.add(new AssignmentItem("Art Project", "2025-05-06", true, "25"));
        assignmentList.add(new AssignmentItem("Computer Science Project", "2025-05-07", false, "30"));
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", true, "20"));

        adapter = new AssignmentAdapter(assignmentList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
