package com.example.GoLearn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        // From intent
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
                        assignmentFileUrl = documentSnapshot.getString("fileUrl"); // e.g. PDF uploaded by teacher

                        titleText.setText(title);
                        descriptionText.setText(description);
                        pointsText.setText("Total Points: " + (points != null ? points : "N/A"));
                        obtainedPointsText.setText("Obtained Points: Not submitted");

                        loadStudentSubmission();
                    }
                });
    }

    private void loadStudentSubmission() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("classes")
                .document(classId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Long obtained = snapshot.getLong("points");
                        if (obtained != null) {
                            obtainedPointsText.setText("Obtained Points: " + obtained);
                        } else {
                            obtainedPointsText.setText("Submitted (Not yet graded)");
                        }
                    }
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types or use "application/pdf" for specific type
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
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveSubmissionToFirestore(String fileUrl) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("classes")
                .document(classId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .document(userId)
                .set(new Submission(fileUrl, null)) // null points (not yet graded)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                    obtainedPointsText.setText("Submitted (Not yet graded)");
                });
    }

    // Submission model class
    public static class Submission {
        public String fileUrl;
        public Long points;

        public Submission() {
        }

        public Submission(String fileUrl, Long points) {
            this.fileUrl = fileUrl;
            this.points = points;
        }
    }
}
