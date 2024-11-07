package com.example.csci310project2treehole;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private Toolbar toolbar;
    private ImageButton backButton;
    private ImageView profileImageView;
    private EditText nameEditText, uscidEditText;
    private Spinner roleSpinner;
    private Button editProfileButton, logoutButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private User currentUser;
    private String userId;
    private boolean isEditing = false;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private static final String PROFILE_IMAGES_FOLDER = "profile_images";
    private StorageReference profileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Storage reference for profile images
        storageReference = FirebaseStorage.getInstance().getReference();
        profileImagesRef = storageReference.child(PROFILE_IMAGES_FOLDER);

        // Rest of initialization
        initializeFirebase();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
            userRef = databaseReference.child("users").child(userId);

            // Check if storage is properly initialized
            if (storageReference != null) {
                Log.d(TAG, "Firebase initialized for user: " + userId);
                Log.d(TAG, "Storage bucket: " + storageReference.getBucket());
            } else {
                Log.e(TAG, "Storage reference is null, reinitializing...");
                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference();
            }
        } else {
            Log.e(TAG, "Firebase User is null.");
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
                        selectedImageUri = uri;
                        Log.d(TAG, "Image selected: " + uri.toString());

                        // Show the selected image immediately
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);

                        // Upload the image
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

    private void uploadProfileImage(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "uploadProfileImage: imageUri is null.");
            return;
        }

        // Use the storage reference from BaseActivity
        if (storageReference == null) {
            Log.e(TAG, "Storage reference is null");
            return;
        }

        Toast.makeText(this, "Starting upload...", Toast.LENGTH_SHORT).show();

        try {
            // Create filename with timestamp and user ID
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = "profile_" + userId + "_" + timestamp + ".jpg";

            // Create reference to the file using the base storageReference
            StorageReference fileRef = storageReference.child(PROFILE_IMAGES_FOLDER).child(filename);

            // Start upload
            UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, String.format("Upload is %.2f%% done", progress));
                    })
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateProfileImageUrl(imageUrl);
                                Toast.makeText(ProfileActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Upload failed", e);
                        Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in upload process", e);
            Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOldProfileImage(String oldImageUrl) {
        if (oldImageUrl != null && !oldImageUrl.equals("default_profile_image_url")) {
            try {
                // Extract the old file path from the URL
                StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                oldImageRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Old profile image deleted successfully");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error deleting old profile image", e);
                        });
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Invalid old image URL: " + oldImageUrl);
            }
        }
    }

    private void updateProfileImageUrl(String newImageUrl) {
        if (userRef == null || newImageUrl == null) {
            Log.e(TAG, "updateProfileImageUrl: Invalid parameters.");
            return;
        }

        // Store old image URL for deletion after successful update
        String oldImageUrl = currentUser != null ? currentUser.getProfileImageUrl() : null;

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageUrl", newImageUrl);

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    if (currentUser != null) {
                        // Delete old image after successful update
                        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
                            deleteOldProfileImage(oldImageUrl);
                        }
                        currentUser.setProfileImageUrl(newImageUrl);
                    }
                    Toast.makeText(ProfileActivity.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update profile URL", e);
                    Toast.makeText(ProfileActivity.this,
                            "Failed to update database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserData() {
        if (userRef == null) {
            Log.e(TAG, "loadUserData: userRef is null.");
            Toast.makeText(this, "User reference is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    nameEditText.setText(currentUser.getName());
                    uscidEditText.setText(currentUser.getUscid());

                    int spinnerPosition = ((ArrayAdapter) roleSpinner.getAdapter())
                            .getPosition(currentUser.getRole());
                    roleSpinner.setSelection(spinnerPosition);

                    String imageUrl = currentUser.getProfileImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default_profile_image_url")) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_default_profile);
                    }

                    enableEditing(false);
                } else {
                    Log.e(TAG, "loadUserData: currentUser is null.");
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "loadUserData:onCancelled", error.toException());
                Toast.makeText(ProfileActivity.this, "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                imagePickerLauncher.launch("image/*");
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                imagePickerLauncher.launch("image/*");
            }
        }
    }

    private void enableEditing(boolean enable) {
        isEditing = enable;
        nameEditText.setEnabled(enable);
        uscidEditText.setEnabled(enable);
        roleSpinner.setEnabled(enable);
        editProfileButton.setText(enable ? "Save" : "Edit Profile");
        profileImageView.setAlpha(enable ? 0.8f : 1.0f);
    }

    private void saveProfileChanges() {
        String newName = nameEditText.getText().toString().trim();
        String newUscid = uscidEditText.getText().toString().trim();
        String newRole = roleSpinner.getSelectedItem().toString();

        if (!validateInputs(newName, newUscid)) {
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("uscid", newUscid);
        updates.put("role", newRole);

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    enableEditing(false);
                    loadUserData();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "saveProfileChanges: Failed to update profile", e);
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                Toast.makeText(this, "Permission required to change profile photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}