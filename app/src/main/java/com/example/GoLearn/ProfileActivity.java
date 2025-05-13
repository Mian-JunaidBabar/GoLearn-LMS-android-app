package com.example.GoLearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvEnrolledClasses;
    private ImageView ivProfilePic;
    private ProgressBar progressBar;
    Button btnUpdate;
    private LinearLayout contentLayout;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvEnrolledClasses = findViewById(R.id.tvEnrolledClasses);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        btnUpdate = findViewById(R.id.updateProfileButton);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        // Load user data from Realtime Database
        usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Set name and email from Realtime Database data
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String profileUrl = dataSnapshot.child("profileUrl").getValue(String.class);

                    tvName.setText(name != null ? name : "No Name");
                    tvEmail.setText(email != null ? email : "No Email");

                    // Load profile image if available
                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(profileUrl).into(ivProfilePic);
                    } else {
                        // Optionally set a default image if no profile URL
                        Glide.with(ProfileActivity.this).load(R.drawable.ic_launcher_background).into(ivProfilePic); // Replace with your default image
                    }

                    // After loading user data, load enrolled classes
                    loadEnrolledClasses(user.getUid());
                } else {
                    progressBar.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        });

        btnUpdate.setOnClickListener(v -> {
            // Handle update button click
            startActivity(new Intent(this, UpdateProfileActivity.class));
        });
    }

    private void loadEnrolledClasses(String userId) {
        usersRef.child(userId).child("enrolledClasses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfClasses = dataSnapshot.getChildrenCount();

                // Update UI with the number of enrolled classes
                displayEnrolledClassesCount(numberOfClasses);

                // Hide loading, show content
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load enrolled classes.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayEnrolledClassesCount(long count) {
        tvEnrolledClasses.setText("Enrolled in " + count + " classes");
    }
}