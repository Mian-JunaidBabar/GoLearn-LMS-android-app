package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.StudentAssignmentAdapter;
import com.example.GoLearn.model.StudentAssignmentItem;

import java.util.ArrayList;
import java.util.List;

public class ClassAssignmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAssignmentAdapter adapter;
    private List<StudentAssignmentItem> assignmentList;

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
        assignmentList.add(new StudentAssignmentItem("Math Homework", "2025-05-03", "Complete exercises 1-10 from Chapter 5", "10 points", true, 8));
        assignmentList.add(new StudentAssignmentItem("Science Report", "2025-05-04", "Write a report on renewable energy sources", "20 points", false, 0));
        assignmentList.add(new StudentAssignmentItem("History Essay", "2025-05-05", "Discuss the causes of World War II", "15 points", true, 12));
        assignmentList.add(new StudentAssignmentItem("Art Project", "2025-05-06", "Create a painting inspired by nature", "25 points", false, 0));
        assignmentList.add(new StudentAssignmentItem("Computer Science Project", "2025-05-07", "Develop a simple calculator app", "30 points", true, 28));

        adapter = new StudentAssignmentAdapter(getContext(), assignmentList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}