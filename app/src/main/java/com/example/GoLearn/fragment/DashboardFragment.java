package com.example.GoLearn.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.ClassActivity;
import com.example.GoLearn.CreateClassActivity;
import com.example.GoLearn.R;
import com.example.GoLearn.adapter.ClassAdapter;
import com.example.GoLearn.model.ClassItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private static final int CREATE_CLASS_REQUEST = 1;
    private List<ClassItem> classList;
    private ClassAdapter classAdapter;
    private DatabaseReference db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classList = new ArrayList<>();
        classAdapter = new ClassAdapter(getContext(), classList, classItem -> {
            Intent intent = new Intent(getContext(), ClassActivity.class);
            intent.putExtra("classId", classItem.getId());
            intent.putExtra("classTitle", classItem.getTitle());
            intent.putExtra("classDesc", classItem.getDescription());
            intent.putExtra("classIcon", classItem.getIconResId());
            intent.putExtra("classTeacher", classItem.getTeacherName());
            startActivity(intent);
        });
        recyclerView.setAdapter(classAdapter);

        db = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadEnrolledClasses();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_class);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Action")
                    .setItems(new CharSequence[]{"Join Class", "Create Class"}, (dialog, which) -> {
                        if (which == 0) {
                            showJoinClassDialog();
                        } else if (which == 1) {
                            Intent intent = new Intent(getActivity(), CreateClassActivity.class);
                            startActivityForResult(intent, CREATE_CLASS_REQUEST);
                        }
                    })
                    .show();
        });

        return view;
    }

    private void showJoinClassDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(requireContext())
                .setTitle("Enter Class Code")
                .setView(input)
                .setPositiveButton("Join", (dialog, which) -> {
                    String classCode = input.getText().toString().trim();
                    if (!classCode.isEmpty()) {
                        findAndJoinClassByCode(classCode);
                    } else {
                        Toast.makeText(getContext(), "Class code cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void findAndJoinClassByCode(String classCode) {
        db.child("classes").orderByChild("classCode").equalTo(classCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot classSnap : snapshot.getChildren()) {
                                String classId = classSnap.getKey();
                                String teacherId = classSnap.child("teacherId").getValue(String.class);

                                // Check if the current user is the teacher of the class
                                if (Objects.equals(currentUser.getUid(), teacherId)) {
                                    Toast.makeText(getContext(), "Error: Teachers cannot join their own class.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Check if the user is already a member of the class
                                if (classSnap.child("members").hasChild(currentUser.getUid())) {
                                    Toast.makeText(getContext(), "You are already in this class.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String title = classSnap.child("title").getValue(String.class);
                                String description = classSnap.child("description").getValue(String.class);

                                DatabaseReference memberRef = db.child("classes").child(classId).child("members").child(currentUser.getUid());
                                memberRef.child("uid").setValue(currentUser.getUid());
                                memberRef.child("name").setValue(currentUser.getDisplayName());
                                memberRef.child("role").setValue("student");
                                memberRef.child("joinedAt").setValue(System.currentTimeMillis());

                                Toast.makeText(getContext(), "Joined class successfully", Toast.LENGTH_SHORT).show();
                                loadEnrolledClasses();
                            }
                        } else {
                            Toast.makeText(getContext(), "Class code not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadEnrolledClasses() {
        if (currentUser == null) return;

        db.child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classList.clear();
                for (DataSnapshot classSnap : snapshot.getChildren()) {
                    String classId = classSnap.getKey();
                    if (classSnap.child("members").hasChild(currentUser.getUid())) {
                        String role = classSnap.child("members").child(currentUser.getUid()).child("role").getValue(String.class);
                        if ("student".equals(role)) { // Only include classes where the user is a student
                            String title = classSnap.child("title").getValue(String.class);
                            String desc = classSnap.child("description").getValue(String.class);
                            String teacherId = classSnap.child("teacherId").getValue(String.class);

                            // Fetch teacher name from the users node
                            db.child("users").child(teacherId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot teacherSnap) {
                                    String teacherName = teacherSnap.getValue(String.class);
                                    classList.add(new ClassItem(classId, title, desc, R.drawable.ic_class, teacherName));
                                    classAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Failed to load teacher name", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_CLASS_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            loadEnrolledClasses();
        }
    }
}
