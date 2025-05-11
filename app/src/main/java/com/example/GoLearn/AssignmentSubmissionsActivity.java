package com.example.GoLearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.adapter.AssignmentSubmissionAdapter;
import com.example.GoLearn.model.AssignmentSubmissionItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentSubmissionAdapter adapter;
    private List<AssignmentSubmissionItem> submissionList;
    private DatabaseReference dbRef;

    private TextView assignmentTitle, assignmentDescription, assignmentPoints;
    private Button btnDelete;

    private String classId, assignmentId;
    private int totalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");
        totalPoints = getIntent().getIntExtra("points", 0);

        if (classId == null || assignmentId == null) {
            Toast.makeText(this, "Error: classId or assignmentId not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.submissions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentTitle = findViewById(R.id.assignment_title);
        assignmentDescription = findViewById(R.id.assignment_description);
        assignmentPoints = findViewById(R.id.assignment_points);
        btnDelete = findViewById(R.id.btn_delete);

        assignmentTitle.setText(getIntent().getStringExtra("title"));
        assignmentDescription.setText(getIntent().getStringExtra("description"));
        assignmentPoints.setText("Points: " + totalPoints);

        submissionList = new ArrayList<>();
        adapter = new AssignmentSubmissionAdapter(this, submissionList, classId, assignmentId, totalPoints);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference();

        loadSubmissions();

        btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this assignment and all associated submissions? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteAllSubmissionsAndAssignment())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadSubmissions() {
        DatabaseReference submissionsRef = dbRef.child("classes").child(classId)
                .child("assignments").child(assignmentId).child("submissions");

        submissionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                submissionList.clear();
                List<DataSnapshot> snapshots = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    snapshots.add(snap);
                }

                if (snapshots.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    return;
                }

                final int[] counter = {0};
                final int total = snapshots.size();

                for (DataSnapshot snapshot : snapshots) {
                    String userId = snapshot.getKey();
                    long points = snapshot.hasChild("points") ? (long) snapshot.child("points").getValue() : 0;
                    String grade = (points > 0) ? points + " / " + totalPoints : "Not Graded";
                    String fileName = extractFileName(snapshot.child("fileUrl").getValue(String.class));

                    dbRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                            String studentName = userSnap.child("name").getValue(String.class);
                            submissionList.add(new AssignmentSubmissionItem(
                                    studentName != null ? studentName : "Unknown User",
                                    fileName,
                                    grade
                            ));
                            counter[0]++;
                            if (counter[0] == total) adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to load user name", error.toException());
                            submissionList.add(new AssignmentSubmissionItem(
                                    "Error Fetching Name",
                                    fileName,
                                    grade
                            ));
                            counter[0]++;
                            if (counter[0] == total) adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignmentSubmissionsActivity.this, "Failed to load submissions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAllSubmissionsAndAssignment() {
        DatabaseReference assignmentRef = dbRef.child("classes").child(classId).child("assignments").child(assignmentId);

        // First, delete all submissions
        deleteAllSubmissions(() -> {
            // After submissions are deleted, delete the assignment itself
            assignmentRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AssignmentSubmissionsActivity.this, "Assignment and submissions deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to the class activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AssignmentSubmissionsActivity.this, "Failed to delete assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void deleteAllSubmissions(Runnable onCompletion) {
        DatabaseReference submissionsRef = dbRef.child("classes").child(classId)
                .child("assignments").child(assignmentId).child("submissions");

        submissionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(AssignmentSubmissionsActivity.this, "No submissions to delete", Toast.LENGTH_SHORT).show();
                    onCompletion.run(); // Still call onCompletion to proceed with assignment deletion
                    return;
                }

                int total = (int) snapshot.getChildrenCount();
                final int[] deleted = {0};

                for (DataSnapshot child : snapshot.getChildren()) {
                    String userId = child.getKey();
                    String fileUrl = child.child("fileUrl").getValue(String.class);

                    if (fileUrl != null) {
                        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
                        fileRef.delete()
                                .addOnSuccessListener(aVoid -> Log.d("Delete", "Deleted file: " + fileUrl))
                                .addOnFailureListener(e -> Log.e("Delete", "Failed to delete file: " + fileUrl, e));
                    }

                    submissionsRef.child(userId).removeValue().addOnCompleteListener(task -> {
                        deleted[0]++;
                        if (deleted[0] == total) {
                            Toast.makeText(AssignmentSubmissionsActivity.this, "All submissions deleted", Toast.LENGTH_SHORT).show();
                            submissionList.clear();
                            adapter.notifyDataSetChanged();
                            onCompletion.run(); // Execute the completion callback
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignmentSubmissionsActivity.this, "Failed to delete submissions", Toast.LENGTH_SHORT).show();
                onCompletion.run(); // Execute the completion callback even on cancellation
            }
        });
    }

    private String extractFileName(String url) {
        if (url == null) return "No file";
        try {
            Uri uri = Uri.parse(url);
            return uri.getLastPathSegment();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}