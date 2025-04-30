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

        // Static pending assignments
        assignmentList = new ArrayList<>();
        assignmentList.add(new AssignmentItem("AI Project Proposal", "1 May 2024", "Pending"));
        assignmentList.add(new AssignmentItem("ML Lab 1", "4 May 2024", "Pending"));
        assignmentList.add(new AssignmentItem("AI Quiz", "6 May 2024", "Pending"));

        AssignmentAdapter adapter = new AssignmentAdapter(assignmentList);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAssignments.setAdapter(adapter);
    }
}
