package com.example.csci310project2treehole;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoryDetailActivity extends AppCompatActivity {

    private EditText categoryInput;  // Input field for the category name
    private Button addCategoryButton, cancelButton;

    private DatabaseReference categoryRef;  // Firebase reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // Initialize UI and Firebase
        initializeViews();
        initializeFirebase();

        // Add category button listener
        addCategoryButton.setOnClickListener(v -> {
            String categoryName = categoryInput.getText().toString().trim();
            addNewCategory(categoryName);
        });

        // Cancel button listener
        cancelButton.setOnClickListener(v -> finish());  // Close the activity
    }

    // Initialize UI components
    private void initializeViews() {
        categoryInput = findViewById(R.id.category_title_text);
        addCategoryButton = findViewById(R.id.add_category_button);
        cancelButton = findViewById(R.id.category_back_button);
    }

    // Initialize Firebase reference
    private void initializeFirebase() {
        categoryRef = FirebaseDatabase.getInstance().getReference("posts");  // 'posts' node
    }

    // Function to add a new category
    private void addNewCategory(String categoryName) {
        if (TextUtils.isEmpty(categoryName)) {
            Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the category already exists
        categoryRef.child(categoryName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(this, "Category already exists!", Toast.LENGTH_SHORT).show();
            } else {
                saveCategoryToFirebase(categoryName);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to check category: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    // Save the category to Firebase
    private void saveCategoryToFirebase(String categoryName) {
        // Placeholder post for the new category
        String postId = categoryRef.child(categoryName).push().getKey();

        if (postId != null) {
            Post examplePost = new Post(
                    postId,
                    "System",
                    "System",
                    "Welcome to the " + categoryName + " category!",
                    "This is a placeholder post for " + categoryName + ".",
                    System.currentTimeMillis(),
                    true
            );

            // Save the placeholder post to Firebase
            categoryRef.child(categoryName).child(postId).setValue(examplePost)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category '" + categoryName + "' created!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after success
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error creating category: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}

