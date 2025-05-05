package com.example.GoLearn;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AssignmentDetailActivity extends AppCompatActivity {

    TextView titleText, descriptionText, gradeText, totalGradeText, pointsText, submissionStatusText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Use setSupportActionBar for androidx Toolbar

        titleText = findViewById(R.id.assignment_title);
        descriptionText = findViewById(R.id.assignment_description);
        gradeText = findViewById(R.id.assignment_grade);
        totalGradeText = findViewById(R.id.assignment_total_grade);
        pointsText = findViewById(R.id.assignment_points);
        submissionStatusText = findViewById(R.id.assignment_submission_status);
        submitButton = findViewById(R.id.submit_button);

        // Get assignment data from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        boolean isSubmitted = getIntent().getBooleanExtra("isSubmitted", false);
        int grade = getIntent().getIntExtra("grade", 0);
        int totalGrade = getIntent().getIntExtra("totalGrade", 100);
        String points = getIntent().getStringExtra("points");

        // Set data to views
        titleText.setText(title);
        descriptionText.setText(description);
        pointsText.setText("Points: " + points);
        totalGradeText.setText("Total Grade: " + totalGrade);
        submissionStatusText.setText(isSubmitted ? "Submission Status: Submitted" : "Submission Status: Not Submitted");

        if (isSubmitted) {
            submitButton.setVisibility(Button.GONE);
            gradeText.setText("Grade: " + grade + "/" + totalGrade);
            gradeText.setVisibility(TextView.VISIBLE);
        } else {
            submitButton.setVisibility(Button.VISIBLE);
            gradeText.setVisibility(TextView.GONE);
        }

        // Submit logic
        submitButton.setOnClickListener(v -> {
            submitButton.setText("Submitted");
            submitButton.setEnabled(false);
        });
    }
}