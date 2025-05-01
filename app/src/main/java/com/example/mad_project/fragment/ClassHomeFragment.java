package com.example.mad_project.fragment;

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

import com.example.mad_project.R;
import com.example.mad_project.adapter.*;
import com.example.mad_project.model.AssignmentItem;

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

        assignmentList = new ArrayList<>();
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 1-10 from Chapter 5", 'A', 100, false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Write a report on renewable energy sources", 'B', 100, true, "20"));
        assignmentList.add(new AssignmentItem("History Essay", "2025-05-05", "Discuss the causes of World War II", 'C', 100, false, "15"));
        assignmentList.add(new AssignmentItem("Art Project", "2025-05-06", "Create a painting inspired by nature", 'A', 100, true, "25"));
        assignmentList.add(new AssignmentItem("Computer Science Project", "2025-05-07", "Develop a simple calculator app", 'B', 100, false, "30"));
        assignmentList.add(new AssignmentItem("Math Homework", "2025-05-03", "Complete exercises 11-20 from Chapter 5", 'C', 100, false, "10"));
        assignmentList.add(new AssignmentItem("Science Report", "2025-05-04", "Prepare a presentation on climate change", 'A', 100, true, "20"));

        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAssignments.setAdapter(adapter);
    }
}
