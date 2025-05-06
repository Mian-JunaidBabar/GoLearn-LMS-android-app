package com.example.GoLearn.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.StudentAdapter;
import com.example.GoLearn.model.PersonItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagePeopleFragment extends Fragment {

    private List<PersonItem> studentList;
    private StudentAdapter adapter;
    private TextView teacherName;
    private String classId;
    private DatabaseReference classMembersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_people, container, false);

        classId = getArguments() != null ? getArguments().getString("classId") : null;

        teacherName = view.findViewById(R.id.teacherName);
        RecyclerView recyclerView = view.findViewById(R.id.studentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase reference
        classMembersRef = FirebaseDatabase.getInstance().getReference("classes").child(classId).child("members");

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, student -> showDeleteConfirmationDialog(student));
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchClassMembers();

        return view;
    }

    private void fetchClassMembers() {
        classMembersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    String id = memberSnapshot.getKey();
                    String name = memberSnapshot.child("name").getValue(String.class);
                    String role = memberSnapshot.child("role").getValue(String.class);

                    if (role != null && role.equalsIgnoreCase("Teacher")) {
                        teacherName.setText(name); // Set teacher's name
                    } else {
                        studentList.add(new PersonItem(id, name, role));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load members: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(PersonItem student) {
        new AlertDialog.Builder(getContext())
                .setTitle("Remove Student")
                .setMessage("Are you sure you want to remove " + student.getName() + " from the class?")
                .setPositiveButton("Yes", (dialog, which) -> deleteStudentFromClass(student))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteStudentFromClass(PersonItem student) {
        classMembersRef.child(student.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    studentList.remove(student);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), student.getName() + " removed successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}