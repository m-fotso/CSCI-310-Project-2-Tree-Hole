package com.example.csci310project2treehole;

import android.app.AlertDialog;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton backButton;
    private ImageView profileImageView;
    private EditText nameEditText, uscidEditText;
    private Spinner roleSpinner;
    private Button editProfileButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private User currentUser;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }
        String userId = firebaseUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupSpinner();
        setupClickListeners();
        loadUserData();
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
            Toast.makeText(this, "Profile image change feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
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

                    // Load profile image
                    if (currentUser.getProfileImageUrl().equals("default_profile_image_url")) {
                        profileImageView.setImageResource(R.drawable.ic_default_profile);
                    } else {
                        Glide.with(ProfileActivity.this)
                                .load(currentUser.getProfileImageUrl())
                                .placeholder(R.drawable.ic_default_profile)
                                .into(profileImageView);
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

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newUscid)) {
            Toast.makeText(this, "Name and USC ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newUscid.length() != 10 || !TextUtils.isDigitsOnly(newUscid)) {
            Toast.makeText(this, "Invalid USC ID format", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> subscriptions = currentUser.getSubscriptions() != null ?
                currentUser.getSubscriptions() : new HashMap<>();

        User updatedUser = new User(newName, currentUser.getEmail(), newUscid,
                newRole, currentUser.getProfileImageUrl(), subscriptions);

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
}

