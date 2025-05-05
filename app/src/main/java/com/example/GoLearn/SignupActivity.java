package com.example.GoLearn;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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

    // UI elements
    private TextInputEditText etName, etSignupEmail, etSignupPassword, etSignupCPassword;
    private TextInputLayout tilName, tilSignupEmail, tilSignupPassword, tilSignupCPassword;
    private Button btnSignup, btnGoToLogin;
    private SignInButton btnGoogleSignIn;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    // Google Sign-In Client
    private GoogleSignInClient googleSignInClient;

    // Default profile image if user doesn't have one
    private static final String DEFAULT_PROFILE_IMAGE_URL = "gs://golearn-1b2e5.appspot.com/GoLearn/images/default user image.jpg";

    // Request code to identify Google Sign-In intent result
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Logging startup
        Log.d(TAG, "onCreate: SignupActivity started");

        // Initialize UI elements
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

        // Firebase authentication and database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Google sign-in configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up listeners for buttons
        btnSignup.setOnClickListener(v -> handleSignup());
        btnGoToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        });

        // Toggle password visibility
        setupPasswordVisibilityToggle(etSignupPassword);
        setupPasswordVisibilityToggle(etSignupCPassword);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        // Handle Google Sign-In result
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    String userId = FirebaseAuth.getInstance().getUid(); // Firebase UID
                    String name = account.getDisplayName();
                    String email = account.getEmail();
                    String profileUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : DEFAULT_PROFILE_IMAGE_URL;

                    // Save user data to Firebase DB
                    saveUserToDatabase(userId, name, email, profileUrl);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignup() {
        Log.d(TAG, "handleSignup: Signup button clicked");

        String name = etName.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String confirmPassword = etSignupCPassword.getText().toString().trim();

        // Input validation
        boolean isValid = true;
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

        if (!isValid) return;

        // Create Firebase auth account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save user data to DB
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

    // Saves user to Firebase using email signup
    private void saveUserToDatabase(String uid, String name, String email) {
        Log.d(TAG, "saveUserToDatabase: Saving user data to database. UserId=" + uid);

        DatabaseReference userRef = mDatabase.getReference("users").child(uid);
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileUrl", DEFAULT_PROFILE_IMAGE_URL);

        // ⚠️ Set enrolledClasses to empty list (not null) to initialize
        userData.put("enrolledClasses", new ArrayList<>());

        userRef.setValue(userData);
    }

    // Saves user to Firebase using Google sign-in
    private void saveUserToDatabase(String userId, String name, String email, String profileUrl) {
        DatabaseReference userRef = mDatabase.getReference("users").child(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", userId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileUrl", profileUrl);

        // ⚠️ Set enrolledClasses to empty list (same as above)
        userData.put("enrolledClasses", new ArrayList<>());

        userRef.setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveUserToDatabase: User data saved successfully");
                        Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "saveUserToDatabase: Failed to save user data", task.getException());
                        Toast.makeText(this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Validates password length and content
    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 8) return false;
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasAlpha = password.matches(".*[a-zA-Z].*");
        return hasDigit && hasAlpha;
    }

    // Setup password visibility eye icon toggle
    private void setupPasswordVisibilityToggle(TextInputEditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Right-side drawable index
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
