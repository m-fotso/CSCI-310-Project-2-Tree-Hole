package com.example.csci310project2treehole;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csci310project2treehole.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText nameEditText, emailEditText, passwordEditText, uscidEditText;
    private Spinner roleSpinner;
    private Button signupButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Connect UI components
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.semail);
        passwordEditText = findViewById(R.id.spassword);
        uscidEditText = findViewById(R.id.sid);
        roleSpinner = findViewById(R.id.role_spinner);
        signupButton = findViewById(R.id.signup_button);
        backToLoginButton = findViewById(R.id.back_to_login_button);

        // Set up the spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Handle sign-up button click
        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String uscid = uscidEditText.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (validateInputs(name, email, password, uscid)) {
                addUser(name, email, password, uscid, role);
            }
        });

        // Handle back to login button click
        backToLoginButton.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password, String uscid) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(uscid)) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate USC email
        if (!email.endsWith("@usc.edu")) {
            Toast.makeText(this, getString(R.string.please_use_usc_email), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate USC ID (assuming it's a 10-digit number)
        if (uscid.length() != 10 || !TextUtils.isDigitsOnly(uscid)) {
            Toast.makeText(this, getString(R.string.invalid_usc_id), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate password length (Firebase requires at least 6 characters)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addUser(String name, String email, String password, String uscid, String role) {
        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get user ID
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Log.d(TAG, "createUserWithEmail:success, userId: " + userId);

                            // Use a default profile image URL (replace with actual URL or handle image upload later)
                            String profileImageUrl = "default_profile_image_url";

                            // Initialize subscriptions
                            Map<String, Boolean> subscriptions = new HashMap<>();
                            subscriptions.put("Academic", false);
                            subscriptions.put("Life", false);
                            subscriptions.put("Event", false);

                            // Create a user object
                            User newUser = new User(name, email, uscid, role, profileImageUrl, subscriptions);

                            // Save user to Firebase Realtime Database
                            databaseReference.child("users").child(userId).setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User data saved successfully.");

                                            // Redirect to MainActivity
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Failed to save user data.", task1.getException());
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign Up failed: User is null.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Sign Up failed: User is null.");
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Toast.makeText(SignUpActivity.this, "Sign Up failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Sign Up failed", exception);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign Up failed: Unknown error.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}


