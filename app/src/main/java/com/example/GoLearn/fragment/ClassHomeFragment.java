package com.example.GoLearn.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.R;
import com.example.GoLearn.adapter.AssignmentAdapter;
import com.example.GoLearn.model.AssignmentItem;
import com.example.GoLearn.model.ClassItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassHomeFragment extends Fragment {

    private RecyclerView rvAssignments;
    private TextView tvClassTitle, tvClassDesc, tvTeacherName, tvMemberCount, tvClassCode, tvSubmissionSummary;
    private Button btnLeaveClass;
    private ImageButton btnCopyCode, btnShareClass;
    private String classId;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private String classCode;

    private AssignmentAdapter assignmentAdapter;
    private List<AssignmentItem> assignmentList = new ArrayList<>();

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

        // Initialize UI elements
        tvClassTitle = view.findViewById(R.id.text_class_title);
        tvClassDesc = view.findViewById(R.id.text_class_desc);
        tvTeacherName = view.findViewById(R.id.text_teacher_name);
        tvMemberCount = view.findViewById(R.id.text_member_count);
        tvClassCode = view.findViewById(R.id.text_class_code);
        tvSubmissionSummary = view.findViewById(R.id.text_submitted_summary);
        btnLeaveClass = view.findViewById(R.id.btn_leave_class);
        btnCopyCode = view.findViewById(R.id.btn_copy_code);
        btnShareClass = view.findViewById(R.id.btn_share_code);

        rvAssignments = view.findViewById(R.id.rv_assignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentAdapter = new AssignmentAdapter(getContext(), assignmentList);
        rvAssignments.setAdapter(assignmentAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        classId = getArguments() != null ? getArguments().getString("classId") : null;

        if (classId == null) {
            // Set dummy data if classId is not available
            setDummyData();
            return;
        }

        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);

        // 1. Load class description
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title = snapshot.child("title").getValue(String.class);
                tvClassTitle.setText(title != null ? title : "N/A");

                String description = snapshot.child("description").getValue(String.class);
                tvClassDesc.setText(description != null ? description : "N/A");

                classCode = snapshot.child("classCode").getValue(String.class);
                tvClassCode.setText(classCode != null ? classCode : "N/A");

                // Copy class code
                btnCopyCode.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Class Code", classCode);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Class code copied", Toast.LENGTH_SHORT).show();
                });

                // Share class code
                btnShareClass.setOnClickListener(v -> {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my class using this code: " + classCode);
                    startActivity(Intent.createChooser(shareIntent, "Share Class Code"));
                });


                String teacherId = snapshot.child("teacherId").getValue(String.class);
                if (teacherId != null) {
                    FirebaseDatabase.getInstance().getReference("users").child(teacherId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot teacherSnapshot) {
                                    String teacherNameValue = teacherSnapshot.child("name").getValue(String.class);
                                    tvTeacherName.setText("Teacher: " + (teacherNameValue != null ? teacherNameValue : "N/A"));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    tvTeacherName.setText("Teacher: N/A");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load class details", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Load total members
        classRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvMemberCount.setText("Total Members: " + (snapshot.getChildrenCount() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvMemberCount.setText("Total Members: N/A");
            }
        });

        // 3. Load assignments and calculate submission stats for current user
        classRef.child("assignments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignmentList.clear();
                for (DataSnapshot assignmentSnapshot : snapshot.getChildren()) {
                    AssignmentItem assignment = assignmentSnapshot.getValue(AssignmentItem.class);
                    assignmentList.add(assignment);
                }
                assignmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load assignments", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Leave class logic
        btnLeaveClass.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Leave Class")
                    .setMessage("Are you sure you want to leave this class?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Remove user from class members
                        DatabaseReference memberRef = classRef.child("members").child(currentUserId);
                        memberRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Remove class from user's enrolled classes
                                removeUserFromEnrolledClasses(classId);
                            } else {
                                Toast.makeText(getContext(), "Failed to leave the class. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void removeUserFromEnrolledClasses(String classId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("enrolledClasses");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String enrolledClassId = classSnapshot.getValue(String.class);
                    if (classId.equals(enrolledClassId)) {
                        classSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "You have left the class.", Toast.LENGTH_SHORT).show();
                                requireActivity().finish(); // Close activity
                            } else {
                                Toast.makeText(getContext(), "Failed to leave the class. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to leave the class. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDummyData() {
        tvClassTitle.setText("Intro to Computer Science");
        tvClassDesc.setText("Class description");
        tvTeacherName.setText("Teacher: Mr. Junaid Ahmad");
        tvMemberCount.setText("Total Members: 28");
    }
}