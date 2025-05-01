package com.example.mad_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.R;
import com.example.mad_project.adapter.AssignmentAdapter;
import com.example.mad_project.model.AssignmentItem;

import java.util.ArrayList;

public class ManageHomeFragment extends Fragment {

    private TextView classTitleTextView, classDescTextView, classStatusTextView;
    private Button addAssignmentButton;
    private RecyclerView assignmentsRecyclerView;
    private AssignmentAdapter assignmentAdapter;
    private ArrayList<AssignmentItem> assignmentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_home, container, false);

        classTitleTextView = view.findViewById(R.id.manage_class_title);
        classDescTextView = view.findViewById(R.id.manage_class_desc);
        classStatusTextView = view.findViewById(R.id.manage_class_status);
        addAssignmentButton = view.findViewById(R.id.btn_add_assignment);
        assignmentsRecyclerView = view.findViewById(R.id.recycler_manage_assignments);

        // Dummy data – you'd pass real class info via arguments or ViewModel
        classTitleTextView.setText("AI Advanced");
        classDescTextView.setText("Deep Learning Project");
        classStatusTextView.setText("Status: Active");

        // Sample assignments
        assignmentList = new ArrayList<>();
        assignmentList.add(new AssignmentItem("Assign 1", "2023-10-01", "Research topic submission", "10 points"));
        assignmentList.add(new AssignmentItem("Assign 2", "2023-10-15", "Model Architecture Upload", "20 points"));

        assignmentAdapter = new AssignmentAdapter(getContext(), assignmentList, item -> {
            // TODO: Open view of submitted assignments and grading
            Toast.makeText(getContext(), "Open: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });

        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsRecyclerView.setAdapter(assignmentAdapter);

        addAssignmentButton.setOnClickListener(v -> {
            // TODO: Open Create Assignment dialog/activity
            Toast.makeText(getContext(), "Add assignment clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}