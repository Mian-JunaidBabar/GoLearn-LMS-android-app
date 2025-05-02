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

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvRole, tvEnrolledClasses;
    private ImageView imgProfile;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind Views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvRole = findViewById(R.id.tvRole);
        tvEnrolledClasses = findViewById(R.id.tvEnrolledClasses);
        imgProfile = findViewById(R.id.imgProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Firebase Init
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Fetch user data
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);
                    String profileUrl = snapshot.child("profileUrl").getValue(String.class);

                    // Enrolled classes list
                    List<String> enrolledClassesList = (List<String>) snapshot.child("enrolledClasses").getValue();
                    int enrolledCount = 0;
                    if (enrolledClassesList != null) {
                        enrolledCount = enrolledClassesList.size();
                    }

                    tvName.setText("Name: " + (name != null ? name : "N/A"));
                    tvEmail.setText("Email: " + (email != null ? email : "N/A"));
                    tvRole.setText("Role: " + (role != null ? role : "N/A"));
                    tvEnrolledClasses.setText("Enrolled Classes: " + enrolledCount);

                    // Load profile image
                    String defaultUrl = "gs://golearn-1b2e5.firebasestorage.app/GoLearn/images/default user image.jpg";
                    Glide.with(ProfileActivity.this)
                            .load(profileUrl != null ? profileUrl : defaultUrl)
                            .into(imgProfile);

                    // Set click listener to open fullscreen image
                    imgProfile.setOnClickListener(v -> {
                        Intent intent = new Intent(ProfileActivity.this, FullscreenImageActivity.class);
                        intent.putExtra("image_uri", profileUrl != null ? profileUrl : defaultUrl);
                        startActivity(intent);
                    });

                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}