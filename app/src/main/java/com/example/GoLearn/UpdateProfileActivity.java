package com.example.GoLearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etName, etOldPassword, etNewPassword, etConfirmPassword;
    private ImageView imgProfile;
    private Button btnChooseImage, btnUpdate;
    private TextInputLayout tilName, tilOldPassword, tilNewPassword, tilConfirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private Uri selectedImageUri = null;
    private String profileUrl;

    private static final String DEFAULT_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/golearn-1b2e5.appspot.com/o/GoLearn%2Fimages%2Fdefault%20user%20image.jpg?alt=media&token=074fc395-c57a-4b0d-8efe-ccbaa28c997a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initViews();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            finish(); // Can't proceed without user
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        loadUserData();

        btnChooseImage.setOnClickListener(v -> pickImageFromGallery());
        btnUpdate.setOnClickListener(v -> validateAndUpdate());
    }

    private void initViews() {
        etName = findViewById(R.id.etUpdateName);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgProfile = findViewById(R.id.imgUpdateProfile);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpdate = findViewById(R.id.btnUpdate);

        tilName = findViewById(R.id.tilUpdateName);
        tilOldPassword = findViewById(R.id.tilOldPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
    }

    private void loadUserData() {
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etName.setText(snapshot.child("name").getValue(String.class));
                profileUrl = snapshot.child("profileUrl").getValue(String.class);
                if (profileUrl == null || profileUrl.isEmpty()) profileUrl = DEFAULT_IMAGE_URL;

                Glide.with(this)
                        .load(profileUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_person_black).circleCrop())
                        .into(imgProfile);
            }
        }).addOnFailureListener(e -> tilName.setError("Failed to load profile"));
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }

    private void validateAndUpdate() {
        String name = etName.getText().toString().trim();
        String oldPwd = etOldPassword.getText().toString();
        String newPwd = etNewPassword.getText().toString();
        String confirmPwd = etConfirmPassword.getText().toString();

        tilName.setError(null);
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (name.isEmpty()) {
            tilName.setError("Name cannot be empty");
            return;
        }

        if (!newPwd.isEmpty()) {
            if (oldPwd.isEmpty()) {
                tilOldPassword.setError("Enter old password");
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                tilConfirmPassword.setError("Passwords do not match");
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPwd);
            currentUser.reauthenticate(credential).addOnSuccessListener(unused -> {
                currentUser.updatePassword(newPwd).addOnSuccessListener(unused1 -> updateProfileData());
            }).addOnFailureListener(e -> tilOldPassword.setError("Incorrect old password"));
        } else {
            updateProfileData();
        }
    }

    private void updateProfileData() {
        String name = Objects.requireNonNull(etName.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name cannot be empty");
            return;
        }

        if (currentUser == null) {
            tilName.setError("User not authenticated");
            return;
        }

        tilName.setError(null); // Clear previous errors

        if (selectedImageUri != null) {
            Log.d("PROFILE_UPDATE", "Selected image URI: " + selectedImageUri);

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("GoLearn/images/" + currentUser.getUid() + ".jpg");

            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("PROFILE_UPDATE", "Image uploaded successfully");

                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    profileUrl = uri.toString();
                                    Log.d("PROFILE_UPDATE", "Image download URL: " + profileUrl);
                                    saveDataToDatabase(name);
                                })
                                .addOnFailureListener(e -> {
                                    tilName.setError("Failed to get image URL: " + e.getMessage());
                                    Log.e("PROFILE_UPDATE", "getDownloadUrl failed", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        tilName.setError("Image upload failed: " + e.getMessage());
                        Log.e("PROFILE_UPDATE", "Image upload failed", e);
                    });

        } else {
            Log.d("PROFILE_UPDATE", "No image selected, updating only name");
            saveDataToDatabase(name);
        }
    }

    private void saveDataToDatabase(String name) {
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("GoLearn/Users")
                .child(currentUser.getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        if (profileUrl != null) {
            map.put("profileUrl", profileUrl);
        }

        userRef.updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                tilName.setError("Failed to update profile");
                Log.e("PROFILE_UPDATE", "Database update failed", task.getException());
            }
        });
    }
}