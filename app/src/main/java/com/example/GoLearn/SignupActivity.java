package com.example.GoLearn;

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
    private TextInputLayout tilName, tilSignupEmail, tilSignupPassword, tilSignupCPassword;
    private Button btnSignup, btnGoToLogin;
    private SignInButton btnGoogleSignIn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private static final String DEFAULT_PROFILE_IMAGE_URL = "gs://golearn-1b2e5.appspot.com/GoLearn/images/default user image.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etName = findViewById(R.id.etName);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etSignupCPassword = findViewById(R.id.etSignupCPassword);
        tilName = findViewById(R.id.nameInputLayout);
        tilSignupEmail = findViewById(R.id.signupEmailInputLayout);
        tilSignupPassword = findViewById(R.id.tilSignupPassword);
        tilSignupCPassword = findViewById(R.id.tilSignupCPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Listeners
        btnSignup.setOnClickListener(v -> handleSignup());
        btnGoToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        // Setup password visibility toggle
        setupPasswordVisibilityToggle(etSignupPassword);
        setupPasswordVisibilityToggle(etSignupCPassword);
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String confirmPassword = etSignupCPassword.getText().toString().trim();

        boolean isValid = true;

        // Clear previous errors
        tilName.setError(null);
        tilSignupEmail.setError(null);
        tilSignupPassword.setError(null);
        tilSignupCPassword.setError(null);

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            isValid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilSignupEmail.setError("Enter a valid email");
            isValid = false;
        }

        if (!isValidPassword(password)) {
            tilSignupPassword.setError("Password must be 6–8 chars, include letters and numbers");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            tilSignupCPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (!isValid) {
            return; // Stop further execution if validation fails
        }

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
        userData.put("profileUrl", DEFAULT_PROFILE_IMAGE_URL);
        userData.put("enrolledClasses", new ArrayList<>());

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
                int drawableEnd = 2; // index for end drawable
                if (passwordField.getCompoundDrawables()[drawableEnd] != null &&
                        event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[drawableEnd].getBounds().width())) {
                    if (passwordField.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    passwordField.setSelection(passwordField.getText().length());
                    return true;
                }
            }
            return false;
        });
    }
}