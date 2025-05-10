package com.example.GoLearn;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GoLearn.adapter.AssignmentSubmissionAdapter;
import com.example.GoLearn.model.AssignmentSubmissionItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AssignmentSubmissionAdapter adapter;
    private List<AssignmentSubmissionItem> submissionList;
    private FirebaseFirestore db;

    TextView assignmentTitle, assignmentDescription, assignmentPoints;
    Button btnUpdate, btnDelete;

    private String classId, assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");

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

        assignmentTitle.setText(getIntent().getStringExtra("title"));
        assignmentDescription.setText(getIntent().getStringExtra("description"));
        assignmentPoints.setText("Points: " + getIntent().getIntExtra("points", 0));

        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        submissionList = new ArrayList<>();
        adapter = new AssignmentSubmissionAdapter(this, submissionList, classId, assignmentId);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadSubmissions();
    }

    private void loadSubmissions() {
        db.collection("classes")
                .document(classId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    submissionList.clear();

                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    if (docs.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    final int[] loadedCount = {0};
                    for (DocumentSnapshot doc : docs) {
                        String userId = doc.getId();
                        String fileUrl = doc.getString("fileUrl");
                        long points = doc.contains("points") ? doc.getLong("points") : 0;
                        String grade = (points > 0) ? String.valueOf(points) : "Not Graded";
                        String fileName = extractFileName(fileUrl);

                        db.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String studentName = userDoc.getString("name");
                                    submissionList.add(new AssignmentSubmissionItem(
                                            studentName != null ? studentName : "Unknown",
                                            fileName,
                                            fileUrl,
                                            grade
                                    ));

                                    loadedCount[0]++;
                                    if (loadedCount[0] == docs.size()) {
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    loadedCount[0]++;
                                    if (loadedCount[0] == docs.size()) {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading submissions", Toast.LENGTH_SHORT).show());
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
