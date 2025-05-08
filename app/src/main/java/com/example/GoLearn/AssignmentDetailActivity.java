package com.example.GoLearn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AssignmentDetailActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1001;

    private TextView titleText, descriptionText, pointsText, obtainedPointsText, selectedFileText;
    private Button submitButton, pickFileButton, viewFileButton;

    private Uri fileUri;

    private String assignmentId;
    private String classId;
    private String assignmentFileUrl;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DatabaseReference realtimeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_detail);

        titleText = findViewById(R.id.assignment_title);
        descriptionText = findViewById(R.id.assignment_description);
        pointsText = findViewById(R.id.assignment_points);
        obtainedPointsText = findViewById(R.id.assignment_obtained_points);
        selectedFileText = findViewById(R.id.selected_file_text);
        submitButton = findViewById(R.id.submit_assignment_button);
        pickFileButton = findViewById(R.id.pick_file_button);
        viewFileButton = findViewById(R.id.view_assignment_file_button);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        realtimeDb = FirebaseDatabase.getInstance().getReference();

        assignmentId = getIntent().getStringExtra("assignmentId");
        classId = getIntent().getStringExtra("classId");

        loadAssignmentData();

        pickFileButton.setOnClickListener(v -> openFilePicker());

        submitButton.setOnClickListener(v -> {
            if (fileUri != null) {
                uploadFileToFirebase(fileUri);
            } else {
                Toast.makeText(this, "Please choose a file first", Toast.LENGTH_SHORT).show();
            }
        });

        viewFileButton.setOnClickListener(v -> {
            if (assignmentFileUrl != null && !assignmentFileUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(assignmentFileUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No assignment file uploaded by teacher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignmentData() {
        db.collection("classes")
                .document(classId)
                .collection("assignments")
                .document(assignmentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        Long points = documentSnapshot.getLong("points");
                        assignmentFileUrl = documentSnapshot.getString("fileUrl");

                        titleText.setText(title);
                        descriptionText.setText(description);
                        pointsText.setText("Total Points: " + (points != null ? points : "N/A"));
                        obtainedPointsText.setText("Obtained Points: Not submitted");

                        checkRealtimeSubmission();
                    }
                });
    }

    private void checkRealtimeSubmission() {
        String userId = auth.getCurrentUser().getUid();

        DatabaseReference submissionRef = realtimeDb
                .child("classes")
                .child(classId)
                .child("assignments")
                .child(assignmentId)
                .child("submissions")
                .child(userId);

        submissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("fileUrl").getValue(String.class) != null) {
                    Long points = snapshot.child("points").getValue(Long.class);
                    obtainedPointsText.setText(points != null ?
                            "Obtained Points: " + points :
                            "Submitted (Not yet graded)");

                    pickFileButton.setEnabled(false);
                    pickFileButton.setAlpha(0.5f);
                    submitButton.setEnabled(false);
                    submitButton.setAlpha(0.5f);
                    selectedFileText.setText("File already submitted.");
                } else {
                    obtainedPointsText.setText("Not submitted");
                    pickFileButton.setEnabled(true);
                    pickFileButton.setAlpha(1.0f);
                    submitButton.setEnabled(true);
                    submitButton.setAlpha(1.0f);
                    selectedFileText.setText("No file selected.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AssignmentDetailActivity.this, "Error checking submission", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            selectedFileText.setText("Selected File: " + fileUri.getLastPathSegment());
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        String userId = auth.getCurrentUser().getUid();
        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(fileUri));
        String filename = "submission_" + userId + "." + ext;

        StorageReference ref = storage.getReference()
                .child("GoLearn/assignments/submittedAssignments/" + classId + "/" + assignmentId + "/" + filename);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            saveSubmissionToFirestore(uri.toString());
                            saveSubmissionToRealtime(uri.toString(), userId);
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveSubmissionToFirestore(String fileUrl) {
        String userId = auth.getCurrentUser().getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());

        db.collection("classes")
                .document(classId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .document(userId)
                .set(new Submission(fileUrl, 0.0, timestamp))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                    obtainedPointsText.setText("Submitted (Not yet graded)");
                });
    }

    private void saveSubmissionToRealtime(String fileUrl, String userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference ref = realtimeDb
                .child("classes")
                .child(classId)
                .child("assignments")
                .child(assignmentId)
                .child("submissions")
                .child(userId);

        Submission submission = new Submission(fileUrl, 0.0, timestamp);
        ref.setValue(submission);
    }

    public static class Submission {
        public String fileUrl;
        public double points;
        public String timestamp;

        public Submission() {
        }

        public Submission(String fileUrl, double points, String timestamp) {
            this.fileUrl = fileUrl;
            this.points = points;
            this.timestamp = timestamp;
        }
    }
}
