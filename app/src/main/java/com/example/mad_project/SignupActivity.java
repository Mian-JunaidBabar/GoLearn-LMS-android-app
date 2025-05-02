package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etSignupEmail, etSignupPassword, etSignupCPassword;
    private TextInputLayout tilSignupPassword, tilSignupCPassword;
    private Button btnSignup, btnGoToLogin;
    private SignInButton btnGoogleSignIn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private static final String DEFAULT_PROFILE_IMAGE_URL = "gs://golearn-1b2e5.firebasestorage.app/GoLearn/images/default user image.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etName = findViewById(R.id.etName);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etSignupCPassword = findViewById(R.id.etSignupCPassword);
        tilSignupPassword = findViewById(R.id.tilSignupPassword);
        tilSignupCPassword = findViewById(R.id.tilSignupCPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Set button listeners
        btnSignup.setOnClickListener(v -> handleSignup());
        btnGoToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        // Add password visibility toggle
        setupPasswordVisibilityToggle(etSignupPassword);
        setupPasswordVisibilityToggle(etSignupCPassword);
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String confirmPassword = etSignupCPassword.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etSignupEmail.setError("Enter a valid email");
            return;
        }

        if (!isValidPassword(password)) {
            tilSignupPassword.setError("Password must be 6–8 chars, include letters and numbers");
            return;
        } else {
            tilSignupPassword.setError(null); // Clear error
        }

        if (!password.equals(confirmPassword)) {
            tilSignupCPassword.setError("Passwords do not match");
            return;
        } else {
            tilSignupCPassword.setError(null); // Clear error
        }

        // Create user in Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user.getUid(), name, email);
                            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DashboardActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String uid, String name, String email) {
        DatabaseReference userRef = mDatabase.getReference("users").child(uid);
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileUrl", DEFAULT_PROFILE_IMAGE_URL); // Default image
        userData.put("enrolledClasses", new ArrayList<>()); // Empty list for enrolled classes

        userRef.setValue(userData);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 8) return false;
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasAlpha = password.matches(".*[a-zA-Z].*");
        return hasDigit && hasAlpha;
    }

    private void setupPasswordVisibilityToggle(TextInputEditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[2].getBounds().width())) {
                    if (passwordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    passwordField.setSelection(passwordField.getText().length()); // Move cursor to the end
                    return true;
                }
            }
            return false;
        });
    }
}