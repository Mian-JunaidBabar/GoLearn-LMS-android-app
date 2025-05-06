package com.example.GoLearn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateClassActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription;
    private Button btnCreateClass;

    private FirebaseAuth mAuth;
    private DatabaseReference classRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        etTitle = findViewById(R.id.editTextClassTitle);
        etDescription = findViewById(R.id.editTextClassDescription);
        btnCreateClass = findViewById(R.id.buttonCreateClass);

        mAuth = FirebaseAuth.getInstance();
        classRef = FirebaseDatabase.getInstance().getReference("classes");

        btnCreateClass.setOnClickListener(v -> createClass());
    }

    private void createClass() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Enter title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getUid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.child("name").get().addOnSuccessListener(dataSnapshot -> {
            final String teacherName = dataSnapshot.getValue(String.class) != null
                    ? dataSnapshot.getValue(String.class)
                    : "Unknown";

            String classId = UUID.randomUUID().toString();
            String classCode = generateClassCode();
            long timestamp = System.currentTimeMillis();

            // Class data
            Map<String, Object> classData = new HashMap<>();
            classData.put("title", title);
            classData.put("description", description);
            classData.put("teacherId", uid);
            classData.put("classCode", classCode);
            classData.put("createdAt", timestamp);

            // Write /classes/{classId}
            classRef.child(classId).setValue(classData).addOnSuccessListener(aVoid -> {
                // Add teacher as member
                Map<String, Object> memberData = new HashMap<>();
                memberData.put("uid", uid);
                memberData.put("role", "teacher");
                memberData.put("joinedAt", timestamp);
                memberData.put("name", teacherName);

                classRef.child(classId).child("members").child(uid).setValue(memberData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Class created", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CreateClassActivity.this, ManageClassActivity.class);
                            intent.putExtra("classId", classId);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to add teacher to class", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to create class", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Could not fetch user name", Toast.LENGTH_SHORT).show();
        });
    }


    private String generateClassCode() {
        // You can make this more sophisticated later (check for uniqueness, etc.)
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
