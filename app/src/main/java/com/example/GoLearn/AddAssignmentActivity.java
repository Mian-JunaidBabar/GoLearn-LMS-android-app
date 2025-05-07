package com.example.GoLearn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate, etPoints;
    private Button btnUploadFile, btnCreateAssignment;
    private TextView tvFileName;

    private Uri fileUri;
    private String classId;
    private String teacherId;

    private StorageReference storageRef;
    private DatabaseReference assignmentsRef;

    private static final int PICK_FILE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_assignment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.et_assignment_title);
        etDescription = findViewById(R.id.et_assignment_description);
        etDueDate = findViewById(R.id.et_due_date);
        etPoints = findViewById(R.id.et_points);
        btnUploadFile = findViewById(R.id.btn_upload_file);
        btnCreateAssignment = findViewById(R.id.btn_create_assignment);
        tvFileName = findViewById(R.id.tv_file_name);

        teacherId = FirebaseAuth.getInstance().getUid();
        classId = getIntent().getStringExtra("classId");

        // ✅ Database: class-specific assignments
        assignmentsRef = FirebaseDatabase.getInstance().getReference("classes")
                .child(classId)
                .child("assignments");

        // ✅ Storage: fixed path GoLearn/assignments/
        storageRef = FirebaseStorage.getInstance().getReference("GoLearn").child("assignments");

        etDueDate.setOnClickListener(v -> showDatePickerDialog());
        btnUploadFile.setOnClickListener(v -> openFilePicker());
        btnCreateAssignment.setOnClickListener(v -> {
            if (validateFields()) {
                if (fileUri != null) {
                    uploadFileThenSaveAssignment();
                } else {
                    saveAssignment(null);
                }
            }
        });
    }

    private boolean validateFields() {
        return !TextUtils.isEmpty(etTitle.getText()) &&
                !TextUtils.isEmpty(etDescription.getText()) &&
                !TextUtils.isEmpty(etDueDate.getText()) &&
                !TextUtils.isEmpty(etPoints.getText());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            tvFileName.setText(getFileName(fileUri));
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        return result != null ? result : uri.getLastPathSegment();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> etDueDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void uploadFileThenSaveAssignment() {
        String fileName = classId + "_" + System.currentTimeMillis();
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    saveAssignment(downloadUri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "File upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAssignment(@Nullable String fileUrl) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDateStr = etDueDate.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();

        int points = pointsStr.isEmpty() ? 0 : Integer.parseInt(pointsStr);

        long dueTimestamp = parseDateToMillis(dueDateStr);

        String assignmentId = assignmentsRef.push().getKey();
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("assignmentId", assignmentId);
        assignmentData.put("classId", classId);
        assignmentData.put("title", title);
        assignmentData.put("description", description);
        assignmentData.put("dueDate", dueTimestamp);
        assignmentData.put("createdAt", ServerValue.TIMESTAMP);
        assignmentData.put("createdBy", teacherId);
        assignmentData.put("filePath", fileUrl != null ? fileUrl : "");
        assignmentData.put("points", points);

        assignmentsRef.child(assignmentId).setValue(assignmentData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Assignment added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private long parseDateToMillis(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }
}
