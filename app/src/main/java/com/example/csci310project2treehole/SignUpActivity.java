package com.example.csci310project2treehole;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText, passwordEditText, uscidEditText;
    private Spinner roleSpinner;
    private Button selectPhotoButton, signupButton, backToLoginButton;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        initializeViews();
        setupSpinner();
        setupImagePicker();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profile_image);
        selectPhotoButton = findViewById(R.id.select_photo_button);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.semail);
        passwordEditText = findViewById(R.id.spassword);
        uscidEditText = findViewById(R.id.sid);
        roleSpinner = findViewById(R.id.role_spinner);
        signupButton = findViewById(R.id.signup_button);
        backToLoginButton = findViewById(R.id.back_to_login_button);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);
                    }
                }
        );
    }

    private void setupClickListeners() {
        selectPhotoButton.setOnClickListener(v -> checkPermissionAndPickImage());

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String uscid = uscidEditText.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (validateInputs(name, email, password, uscid)) {
                signupButton.setEnabled(false);
                addUser(name, email, password, uscid, role);
            }
        });

        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                imagePickerLauncher.launch("image/*");
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                imagePickerLauncher.launch("image/*");
            }
        }
    }

    private boolean validateInputs(String name, String email, String password, String uscid) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(uscid)) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!email.endsWith("@usc.edu")) {
            Toast.makeText(this, getString(R.string.please_use_usc_email), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (uscid.length() != 10 || !TextUtils.isDigitsOnly(uscid)) {
            Toast.makeText(this, getString(R.string.invalid_usc_id), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addUser(String name, String email, String password, String uscid, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            if (selectedImageUri != null) {
                                uploadImageAndCreateUser(selectedImageUri, userId, name, email, uscid, role);
                            } else {
                                createUserWithDefaultImage(userId, name, email, uscid, role);
                            }
                        }
                    } else {
                        signupButton.setEnabled(true);
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Unknown error";
                        Toast.makeText(SignUpActivity.this,
                                "Sign Up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadImageAndCreateUser(Uri imageUri, String userId,
                                          String name, String email, String uscid, String role) {
        String imagePath = "profile_images/" + userId + "_" + UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(imagePath);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String imageUrl = task.getResult().toString();
                createUserWithImage(userId, name, email, uscid, role, imageUrl);
            } else {
                signupButton.setEnabled(true);
                Toast.makeText(SignUpActivity.this,
                        "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserWithDefaultImage(String userId,
                                            String name, String email, String uscid, String role) {
        createUserWithImage(userId, name, email, uscid, role, "default_profile_image_url");
    }

    private void createUserWithImage(String userId, String name, String email,
                                     String uscid, String role, String imageUrl) {
        Map<String, Boolean> subscriptions = new HashMap<>();
        subscriptions.put("Academic", false);
        subscriptions.put("Life", false);
        subscriptions.put("Events", false);

        User newUser = new User(name, email, uscid, role, imageUrl, subscriptions);

        databaseReference.child("users").child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    signupButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                Toast.makeText(this, "Permission required for image selection",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

