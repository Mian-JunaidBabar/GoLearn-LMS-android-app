package com.example.GoLearn;

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
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvEnrolledClasses;
    private ImageView imgProfile;
    private Button btnLogout, btnUpdateProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvEnrolledClasses = findViewById(R.id.tvEnrolledClasses);
        imgProfile = findViewById(R.id.imgProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);
                String profileUrl = snapshot.child("profileUrl").getValue(String.class);
                ArrayList<String> enrolled = snapshot.child("enrolledClasses").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                });
                int enrolledCount = enrolled != null ? enrolled.size() : 0;

                tvName.setText("Name: " + (name != null ? name : "N/A"));
                tvEmail.setText("Email: " + (email != null ? email : "N/A"));
                tvEnrolledClasses.setText("Enrolled Classes: " + enrolledCount);

                Glide.with(ProfileActivity.this)
                        .load(profileUrl != null ? profileUrl : R.drawable.ic_person_black)
                        .into(imgProfile);

                imgProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(ProfileActivity.this, FullscreenImageActivity.class);
                    intent.putExtra("image_uri", profileUrl);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdateProfile.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}
