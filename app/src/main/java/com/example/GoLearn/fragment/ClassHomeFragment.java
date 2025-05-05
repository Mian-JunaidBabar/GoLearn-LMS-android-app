package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.StudentAssignmentAdapter;
import com.example.GoLearn.model.AssignmentItem;
import com.example.GoLearn.model.StudentAssignmentItem;

import java.util.ArrayList;
import java.util.List;

public class ClassHomeFragment extends Fragment {

    private RecyclerView rvAssignments;
    private TextView submittedSummary, classDesc, teacherName, totalMembers;
    private List<AssignmentItem> assignmentList;

    public ClassHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        submittedSummary = view.findViewById(R.id.submitted_summary);
        classDesc = view.findViewById(R.id.class_description);
        teacherName = view.findViewById(R.id.teacher_name);
        totalMembers = view.findViewById(R.id.total_members);
        rvAssignments = view.findViewById(R.id.rv_assignments);

        // Static details
        classDesc.setText("Course: Introduction to AI and ML");
        teacherName.setText("Teacher: Sir Adeel Mughal");
        totalMembers.setText("Total Members: 30");
        submittedSummary.setText("Submitted Assignments: 3");

        // Initialize assignment list
        assignmentList = new ArrayList<>();
        assignmentList.add(new StudentAssignmentItem("Math Homework", "2025-05-03", "Complete exercises 1-10 from Chapter 5", "10 points", true, 8));
        assignmentList.add(new StudentAssignmentItem("Science Report", "2025-05-04", "Write a report on renewable energy sources", "20 points", false, 0));

        // Set up RecyclerView
        StudentAssignmentAdapter studentAdapter = new StudentAssignmentAdapter(getContext(), (List<StudentAssignmentItem>) (List<?>) assignmentList);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAssignments.setAdapter(studentAdapter);
    }
}