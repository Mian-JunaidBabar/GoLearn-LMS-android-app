package com.example.GoLearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.adapter.ClassAdapter;
import com.example.GoLearn.model.ClassItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherClassesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private ArrayList<ClassItem> teacherClassList;
    private DatabaseReference classRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerTeacherClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        teacherClassList = new ArrayList<>();
        classAdapter = new ClassAdapter(this, teacherClassList);
        classAdapter.setOnItemClickListener(classItem -> {
            Intent intent = new Intent(this, ManageClassActivity.class);
            intent.putExtra("classId", classItem.getClassId());
            startActivity(intent);
        });
        recyclerView.setAdapter(classAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        classRef = FirebaseDatabase.getInstance().getReference("classes");

        loadTeacherClasses();
    }

    private void loadTeacherClasses() {
        classRef.orderByChild("teacherId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherClassList.clear();
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    String id = classSnapshot.getKey();
                    String title = classSnapshot.child("title").getValue(String.class);
                    String description = classSnapshot.child("description").getValue(String.class);
                    String teacherId = classSnapshot.child("teacherId").getValue(String.class);

                    // Fetch teacher name from the users node
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(teacherId);
                    userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot teacherSnap) {
                            String teacherName = teacherSnap.getValue(String.class);
                            ClassItem classItem = new ClassItem(id, title, description, R.drawable.ic_class, teacherName);
                            classItem.setClassId(classSnapshot.getKey());
                            teacherClassList.add(classItem);
                            classAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TeacherClassesActivity.this, "Failed to load teacher name", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherClassesActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}