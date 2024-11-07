package com.example.csci310project2treehole;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;

    private Toolbar toolbar;
    private ImageButton backButton;
    private ImageView profileImageView;
    private EditText nameEditText, uscidEditText;
    private Spinner roleSpinner;
    private Button editProfileButton, logoutButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private User currentUser;
    private String userId;
    private boolean isEditing = false;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        initializeFirebase();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize UI and setup components
        initializeViews();
        setupToolbar();
        setupSpinner();
        setupImagePicker();
        setupClickListeners();
        loadUserData();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profile_images");
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.profile_toolbar);
        backButton = findViewById(R.id.back_button);
        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.profile_name);
        uscidEditText = findViewById(R.id.profile_uscid);
        roleSpinner = findViewById(R.id.profile_role_spinner);
        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
                        uploadProfileImage(uri);
                    }
                }
        );
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        editProfileButton.setOnClickListener(v -> {
            if (isEditing) {
                saveProfileChanges();
            } else {
                enableEditing(true);
            }
        });

        logoutButton.setOnClickListener(v -> logoutUser());

        profileImageView.setOnClickListener(v -> {
            if (isEditing) {
                checkPermissionAndPickImage();
            } else {
                Toast.makeText(this, "Enable edit mode to change photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    // Populate UI with user data
                    nameEditText.setText(currentUser.getName());
                    uscidEditText.setText(currentUser.getUscid());
                    int spinnerPosition = ((ArrayAdapter) roleSpinner.getAdapter())
                            .getPosition(currentUser.getRole());
                    roleSpinner.setSelection(spinnerPosition);

                    // Load profile image
                    String imageUrl = currentUser.getProfileImageUrl();
                    if (imageUrl != null && !imageUrl.equals("default_profile_image_url")) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_default_profile);
                    }

                    enableEditing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Failed to load user data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                imagePickerLauncher.launch("image/*");
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                imagePickerLauncher.launch("image/*");
            }
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        String imageName = userId + "_" + UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(imageName);

        imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().toString();
                        updateProfileImageUrl(downloadUrl);
                    } else {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfileImageUrl(String imageUrl) {
        userRef.child("profileImageUrl").setValue(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.setProfileImageUrl(imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);
                        Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enableEditing(boolean enable) {
        isEditing = enable;
        nameEditText.setEnabled(enable);
        uscidEditText.setEnabled(enable);
        roleSpinner.setEnabled(enable);
        editProfileButton.setText(enable ? "Save" : "Edit Profile");
    }

    private void saveProfileChanges() {
        String newName = nameEditText.getText().toString().trim();
        String newUscid = uscidEditText.getText().toString().trim();
        String newRole = roleSpinner.getSelectedItem().toString();

        if (!validateInputs(newName, newUscid)) {
            return;
        }

        Map<String, Boolean> subscriptions = currentUser.getSubscriptions() != null ?
                currentUser.getSubscriptions() : new HashMap<>();

        User updatedUser = new User(
                newName,
                currentUser.getEmail(),
                newUscid,
                newRole,
                currentUser.getProfileImageUrl(),
                subscriptions
        );

        userRef.setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                currentUser = updatedUser;
                enableEditing(false);
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String name, String uscid) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(uscid)) {
            Toast.makeText(this, "Name and USC ID cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (uscid.length() != 10 || !TextUtils.isDigitsOnly(uscid)) {
            Toast.makeText(this, "Invalid USC ID format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                Toast.makeText(this, "Permission required to change profile photo",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}