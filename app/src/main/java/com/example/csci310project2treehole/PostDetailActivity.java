package com.example.csci310project2treehole;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private TextView postTitleTextView, postContentTextView, postAuthorTextView;
    private ListView repliesListView;
    private EditText replyEditText;
    private Button sendReplyButton, anonymousReplyButton;
    private String category, postId;
    private DatabaseReference postRef;
    private FirebaseAuth mAuth;
    private String userId;
    private String userName;
    private boolean isAnonymous = false;

    private List<Reply> replyList;
    private ReplyAdapter replyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        category = getIntent().getStringExtra("category");
        postId = getIntent().getStringExtra("postId");

        postTitleTextView = findViewById(R.id.post_title_textview);
        postContentTextView = findViewById(R.id.post_content_textview);
        postAuthorTextView = findViewById(R.id.post_author_textview);
        repliesListView = findViewById(R.id.replies_list_view);
        replyEditText = findViewById(R.id.reply_edittext);
        sendReplyButton = findViewById(R.id.send_reply_button);
        anonymousReplyButton = findViewById(R.id.anonymous_reply_button);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("posts").child(category).child(postId);

        // Fetch the user's name from the database
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userName = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(PostDetailActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Load post details
        loadPostDetails();

        // Initialize replies list and adapter
        replyList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(this, replyList);
        repliesListView.setAdapter(replyAdapter);

        // Load replies
        loadReplies();

        // Handle send reply button click
        sendReplyButton.setOnClickListener(v -> sendReply());

        // Handle anonymous reply button click
        anonymousReplyButton.setOnClickListener(v -> {
            isAnonymous = !isAnonymous;
            anonymousReplyButton.setText(isAnonymous ? "Anonymous Reply On" : "Anonymous Reply Off");
        });
    }

    private void loadPostDetails() {
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    postTitleTextView.setText(post.getTitle());
                    postContentTextView.setText(post.getContent());
                    postAuthorTextView.setText("By: " + post.getAuthorName());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, "Failed to load post details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReplies() {
        postRef.child("replies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                replyList.clear();
                for (DataSnapshot replySnapshot : snapshot.getChildren()) {
                    Reply reply = replySnapshot.getValue(Reply.class);
                    replyList.add(reply);
                }
                replyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, "Failed to load replies.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReply() {
        String content = replyEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Reply cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String replyId = postRef.child("replies").push().getKey();
        long timestamp = System.currentTimeMillis();
        String authorName = isAnonymous ? "Anonymous" : userName;

        Reply newReply = new Reply(replyId, userId, authorName, content, timestamp, isAnonymous);

        postRef.child("replies").child(replyId).setValue(newReply)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostDetailActivity.this, "Reply sent.", Toast.LENGTH_SHORT).show();
                        replyEditText.setText("");
                    } else {
                        Toast.makeText(PostDetailActivity.this, "Failed to send reply.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
