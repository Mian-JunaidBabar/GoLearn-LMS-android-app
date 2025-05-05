package com.example.GoLearn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class CreateClassActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription;
    private Button btnCreateClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        // Initialize views
        etTitle = findViewById(R.id.editTextClassTitle);
        etDescription = findViewById(R.id.editTextClassDescription);
        btnCreateClass = findViewById(R.id.buttonCreateClass);

        // Set button click listener
        btnCreateClass.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Enter title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new class item
            String fakeClassId = UUID.randomUUID().toString();
            int defaultIconResId = R.drawable.ic_class; // Replace with your default icon resource
            String defaultStatus = "Active"; // Default status for the class

            // Pass each field separately
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", fakeClassId);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("description", desc);
            resultIntent.putExtra("iconResId", defaultIconResId);
            resultIntent.putExtra("status", defaultStatus);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}