package com.example.csci310project2treehole;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class NewPostActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button postButton, cancelButton;
    private CheckBox anonymousCheckBox;
    private String category;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        category = getIntent().getStringExtra("category");
        setTitle("New Post in " + category);

        titleEditText = findViewById(R.id.post_title_edittext);
        contentEditText = findViewById(R.id.post_content_edittext);
        postButton = findViewById(R.id.post_button);
        cancelButton = findViewById(R.id.cancel_button);
        anonymousCheckBox = findViewById(R.id.anonymous_checkbox);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        if(category == null) {
            category = "Academic";
        }
        postsRef = FirebaseDatabase.getInstance().getReference("posts").child(category);

        // Fetch the user's name from the database
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userName = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(NewPostActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                });

        postButton.setOnClickListener(v -> createNewPost());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void createNewPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        boolean isAnonymous = anonymousCheckBox.isChecked();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Title and content cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String postId = postsRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        Post newPost = new Post(postId, userId, userName, title, content, timestamp, isAnonymous);
        newPost.setCategory(category);

        if (isAnonymous) {
            // The Post constructor will automatically set up the first anonymous name
            String anonymousName = newPost.getAnonymousNameForUser(userId);
        }

        postsRef.child(postId).setValue(newPost)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewPostActivity.this, "Post created successfully.", Toast.LENGTH_SHORT).show();
                        notifySubscribers(newPost);
                        finish();
                    } else {
                        Toast.makeText(NewPostActivity.this, "Failed to create post.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void notifySubscribers(Post post) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    if (!uid.equals(userId)) {
                        Boolean isSubscribed = userSnapshot.child("subscriptions")
                                .child(category).getValue(Boolean.class);
                        if (isSubscribed != null && isSubscribed) {
                            DatabaseReference notificationsRef = usersRef.child(uid).child("notifications");
                            String notificationId = notificationsRef.push().getKey();
                            String authorName = post.isAnonymous() ?
                                    post.getAnonymousNameForUser(userId) : userName;
                            Notification notification = new Notification(
                                    notificationId,
                                    "New post in " + category,
                                    "New post: " + post.getTitle() + " by " + authorName
                            );
                            notificationsRef.child(notificationId).setValue(notification);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}


