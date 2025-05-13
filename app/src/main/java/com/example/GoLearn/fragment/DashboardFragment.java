package com.example.GoLearn.fragment;

        import android.app.AlertDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.text.InputType;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.TextView;
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
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;


        public class DashboardFragment extends Fragment {

            private List<ClassItem> classList;
            private ClassAdapter classAdapter;
            private DatabaseReference db;
            private FirebaseUser currentUser;
            private TextView noClassesText;

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

                RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                classList = new ArrayList<>();

                classAdapter = new ClassAdapter(getContext(), classList);
                classAdapter.setOnItemClickListener(classItem -> {
                    Intent intent = new Intent(getContext(), ClassActivity.class);
                    intent.putExtra("classId", classItem.getId());
                    startActivity(intent);
                });

                recyclerView.setAdapter(classAdapter);

                db = FirebaseDatabase.getInstance().getReference();
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                noClassesText = view.findViewById(R.id.no_classes_text);

                if (currentUser != null) {
                    loadUserEnrolledClasses();
                }

                FloatingActionButton fab = view.findViewById(R.id.fab_add_class);
                fab.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Select Action")
                            .setItems(new CharSequence[]{"Join Class", "Create Class"}, (dialog, which) -> {
                                if (which == 0) {
                                    showJoinClassDialog();
                                } else {
                                    Intent intent = new Intent(getActivity(), CreateClassActivity.class);
                                    startActivityForResult(intent, 1);
                                }
                            })
                            .show();
                });

                return view;
            }

            private void loadUserEnrolledClasses() {
                classList.clear();
                classAdapter.notifyDataSetChanged();

                db.child("users").child(currentUser.getUid()).child("enrolledClasses")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot enrolledSnapshot) {
                                if (!enrolledSnapshot.exists()) {
                                    noClassesText.setVisibility(View.VISIBLE);
                                    return;
                                }

                                if (!enrolledSnapshot.hasChildren()) {
                                    noClassesText.setVisibility(View.VISIBLE);
                                    return;
                                }

                                noClassesText.setVisibility(View.GONE);

                                for (DataSnapshot classIdSnap : enrolledSnapshot.getChildren()) {
                                    String classId = classIdSnap.getValue(String.class);
                                    if (classId == null) continue;

                                    db.child("classes").child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot classSnap) {
                                            if (!classSnap.exists()) return;

                                            String title = classSnap.child("title").getValue(String.class);
                                            String description = classSnap.child("description").getValue(String.class);
                                            String teacherId = classSnap.child("teacherId").getValue(String.class);

                                            if (teacherId == null) return;

                                            db.child("users").child(teacherId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot teacherSnap) {
                                                    String teacherName = teacherSnap.getValue(String.class);
                                                    classList.add(new ClassItem(classId, title, description, R.drawable.ic_class, teacherName));
                                                    classAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("Dashboard", "Failed to load teacher name");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Dashboard", "Failed to load class data");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to fetch enrolled classes", Toast.LENGTH_SHORT).show();
                            }
                        });
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
                                if (!snapshot.exists()) {
                                    Toast.makeText(getContext(), "Class not found", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                for (DataSnapshot classSnap : snapshot.getChildren()) {
                                    String classId = classSnap.getKey();
                                    String teacherId = classSnap.child("teacherId").getValue(String.class);

                                    if (Objects.equals(teacherId, currentUser.getUid())) {
                                        Toast.makeText(getContext(), "Teachers can't join their own class", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (classSnap.child("members").hasChild(currentUser.getUid())) {
                                        Toast.makeText(getContext(), "Already in this class", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    DatabaseReference memberRef = db.child("classes").child(classId).child("members").child(currentUser.getUid());
                                    memberRef.child("uid").setValue(currentUser.getUid());
                                    memberRef.child("name").setValue(currentUser.getDisplayName());
                                    memberRef.child("role").setValue("student");
                                    memberRef.child("joinedAt").setValue(System.currentTimeMillis());

                                    // Add classId to user's enrolledClasses
                                    db.child("users").child(currentUser.getUid()).child("enrolledClasses").push().setValue(classId);

                                    Toast.makeText(getContext(), "Joined class successfully", Toast.LENGTH_SHORT).show();
                                    loadUserEnrolledClasses();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error joining class", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
                    loadUserEnrolledClasses();
                }
            }
            public interface OnItemClickListener {
                void onItemClick(ClassItem classItem);
            }
        }