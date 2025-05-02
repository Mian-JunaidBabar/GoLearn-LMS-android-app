package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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

    private TextView tvName, tvEmail;
    private ImageView profileImage;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        profileImage = findViewById(R.id.profileImage);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // Set user email
        tvEmail.setText(currentUser.getEmail());

        // Fetch user data from Firebase Realtime Database
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String profileUrl = snapshot.child("profileUrl").getValue(String.class);

                // Set user name
                tvName.setText(name != null ? name : "No Name");

                // Load profile image using Glide
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_android_black); // Default image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout button click listener
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            redirectToLogin(); // Redirect to login page
        });
    }

    // Redirect to LoginActivity
    private void redirectToLogin() {
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish(); // Close the current activity
    }
}