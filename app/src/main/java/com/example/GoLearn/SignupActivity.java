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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    private GoogleSignInClient googleSignInClient;
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 100;

    private static final String DEFAULT_PROFILE_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/golearn-1b2e5.firebasestorage.app/o/GoLearn%2Fimages%2Fdefault%20user%20image.jpg?alt=media&token=074fc395-c57a-4b0d-8efe-ccbaa28c997a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // UI
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

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignup.setOnClickListener(v -> handleSignup());
        btnGoToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        });

        setupPasswordVisibilityToggle(etSignupPassword);
        setupPasswordVisibilityToggle(etSignupCPassword);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                String name = account.getDisplayName();
                                String email = account.getEmail();
                                String profileUrl = account.getPhotoUrl() != null
                                        ? account.getPhotoUrl().toString()
                                        : DEFAULT_PROFILE_IMAGE_URL;

                                saveUserToDatabase(uid, name, email, profileUrl, () -> {
                                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, DashboardActivity.class));
                                    finish();
                                });
                            }
                        } else {
                            Toast.makeText(this, "Google Sign-In failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String confirmPassword = etSignupCPassword.getText().toString().trim();

        tilName.setError(null);
        tilSignupEmail.setError(null);
        tilSignupPassword.setError(null);
        tilSignupCPassword.setError(null);

        boolean isValid = true;

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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user.getUid(), name, email, DEFAULT_PROFILE_IMAGE_URL, () -> {
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, DashboardActivity.class));
                                finish();
                            });
                        }
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 15) return false;
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasAlpha = password.matches(".*[a-zA-Z].*");
        return hasDigit && hasAlpha;
    }

    private void saveUserToDatabase(String uid, String name, String email, String profileUrl, Runnable onSuccess) {
        DatabaseReference userRef = mDatabase.getReference("users").child(uid);

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileUrl", profileUrl);
        userData.put("enrolledClasses", new HashMap<>());  // Keep format consistent with your DB schema

        userRef.setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User data saved.");
                onSuccess.run();
            } else {
                Log.e(TAG, "Failed to save user data", task.getException());
                Toast.makeText(this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupPasswordVisibilityToggle(TextInputEditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2;
                if (passwordField.getCompoundDrawables()[drawableEnd] != null &&
                        event.getRawX() >= (passwordField.getRight() -
                                passwordField.getCompoundDrawables()[drawableEnd].getBounds().width())) {
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
