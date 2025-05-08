package com.example.GoLearn.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.GoLearn.model.AssignmentItem;
import com.example.GoLearn.model.StudentAssignmentItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassAssignmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAssignmentAdapter adapter;
    private List<StudentAssignmentItem> assignmentList;

    private String classId;
    private String currentUserId;
    private DatabaseReference classRef;

    public ClassAssignmentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_assignments, container, false);

        recyclerView = view.findViewById(R.id.assignmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentList = new ArrayList<>();
        adapter = new StudentAssignmentAdapter(getContext(), assignmentList);
        recyclerView.setAdapter(adapter);

        // Assume classId is passed via arguments
        classId = getArguments().getString("classId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);

        loadAssignments();

        return view;
    }

    private void loadAssignments() {
        classRef.child("assignments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot assignmentSnapshot) {
                classRef.child("members").child(currentUserId).child("submittedAssignments")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot submissionSnapshot) {
                                assignmentList.clear();

                                for (DataSnapshot assn : assignmentSnapshot.getChildren()) {
                                    AssignmentItem base = assn.getValue(AssignmentItem.class);
                                    StudentAssignmentItem studentItem = new StudentAssignmentItem();

                                    studentItem.setAssignmentId(base.getAssignmentId());
                                    studentItem.setClassId(base.getClassId());
                                    studentItem.setCreatedAt(base.getCreatedAt());
                                    studentItem.setCreatedBy(base.getCreatedBy());
                                    studentItem.setDescription(base.getDescription());
                                    studentItem.setDueDate(base.getDueDate());
                                    studentItem.setFilePath(base.getFilePath());
                                    studentItem.setPoints(base.getPoints());
                                    studentItem.setTitle(base.getTitle());

                                    if (submissionSnapshot.hasChild(base.getAssignmentId())) {
                                        DataSnapshot submittedData = submissionSnapshot.child(base.getAssignmentId());
                                        int obtained = submittedData.child("obtainedPoints").exists()
                                                ? submittedData.child("obtainedPoints").getValue(Integer.class)
                                                : -1;

                                        studentItem.setSubmitted(true);
                                        studentItem.setObtainedPoints(obtained >= 0 ? obtained : -1);
                                    } else {
                                        studentItem.setSubmitted(false);
                                        studentItem.setObtainedPoints(-1);
                                    }

                                    assignmentList.add(studentItem);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Submission fetch error: " + error.getMessage());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Assignment fetch error: " + error.getMessage());
            }
        });
    }
}
