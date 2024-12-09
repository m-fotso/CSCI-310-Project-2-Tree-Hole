package com.example.csci310project2treehole;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class PostDetailActivity extends BaseActivity {
    private TextView postTitleTextView, postContentTextView, postAuthorTextView;
    private ImageView postAuthorImageView;
    private RecyclerView repliesRecyclerView;
    private EditText replyEditText;
    private Button sendReplyButton, anonymousReplyButton, cancelReplyButton;
    private TextView replyingToText;
    private ImageButton editPostButton, deletePostButton;
    private String category, postId;
    private DatabaseReference postRef;
    private FirebaseAuth mAuth;
    private String userId;
    private String userName;
    private boolean isAnonymous = false;
    private Reply replyingTo = null;
    private Post currentPost;

    private List<Reply> replyList;
    private Map<String, List<Reply>> nestedReplies;
    private NestedReplyAdapter replyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        category = getIntent().getStringExtra("category");
        postId = getIntent().getStringExtra("postId");

        initializeViews();
        initializeFirebase();
        setupClickListeners();
        loadPostDetails();
        loadReplies();
    }

    private void initializeViews() {
        postTitleTextView = findViewById(R.id.post_title_textview);
        postContentTextView = findViewById(R.id.post_content_textview);
        postAuthorTextView = findViewById(R.id.post_author_textview);
        postAuthorImageView = findViewById(R.id.post_author_image);
        repliesRecyclerView = findViewById(R.id.replies_recycler_view);
        replyEditText = findViewById(R.id.reply_edittext);
        sendReplyButton = findViewById(R.id.send_reply_button);
        anonymousReplyButton = findViewById(R.id.anonymous_reply_button);
        cancelReplyButton = findViewById(R.id.cancel_reply_button);
        replyingToText = findViewById(R.id.replying_to_text);
        editPostButton = findViewById(R.id.edit_post_button);
        deletePostButton = findViewById(R.id.delete_post_button);

        replyList = new ArrayList<>();
        nestedReplies = new HashMap<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        repliesRecyclerView.setLayoutManager(layoutManager);

        replyingToText.setVisibility(View.GONE);
        cancelReplyButton.setVisibility(View.GONE);
        editPostButton.setVisibility(View.GONE);
        deletePostButton.setVisibility(View.GONE);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        postRef = databaseReference.child("posts").child(category).child(postId);

        replyAdapter = new NestedReplyAdapter(this, replyList, nestedReplies, this::startReplyToReply, postRef);
        repliesRecyclerView.setAdapter(replyAdapter);

        databaseReference.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userName = snapshot.child("name").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        showToast("Failed to load user data.");
                    }
                });
    }

    private void loadAuthorProfileImage(String authorId, boolean isAnonymous) {
        if (isAnonymous) {
            postAuthorImageView.setImageResource(R.drawable.ic_default_profile);
            return;
        }

        databaseReference.child("users").child(authorId).child("profileImageUrl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String imageUrl = snapshot.getValue(String.class);
                            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default_profile_image_url")) {
                                Glide.with(PostDetailActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_default_profile)
                                        .error(R.drawable.ic_default_profile)
                                        .into(postAuthorImageView);
                            } else {
                                postAuthorImageView.setImageResource(R.drawable.ic_default_profile);
                            }
                        } else {
                            postAuthorImageView.setImageResource(R.drawable.ic_default_profile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        postAuthorImageView.setImageResource(R.drawable.ic_default_profile);
                    }
                });
    }

    private void setupClickListeners() {
        sendReplyButton.setOnClickListener(v -> sendReply());

        anonymousReplyButton.setOnClickListener(v -> {
            isAnonymous = !isAnonymous;
            anonymousReplyButton.setText(isAnonymous ? "Anonymous Reply On" : "Anonymous Reply Off");
            checkAnonymousStatus();
        });

        cancelReplyButton.setOnClickListener(v -> cancelReply());
        editPostButton.setOnClickListener(v -> showEditPostDialog());
        deletePostButton.setOnClickListener(v -> showDeletePostDialog());
    }

    private void loadPostDetails() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentPost = snapshot.getValue(Post.class);
                if (currentPost != null) {
                    postTitleTextView.setText(currentPost.getTitle());
                    postContentTextView.setText(currentPost.getContent());

                    String authorDisplay = currentPost.getDisplayNameForUser(currentPost.getAuthorId(), currentPost.getAuthorName());
                    postAuthorTextView.setText("By: " + authorDisplay);

                    loadAuthorProfileImage(currentPost.getAuthorId(), currentPost.isAnonymous());

                    boolean isAuthor = currentPost.getAuthorId().equals(userId);
                    editPostButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
                    deletePostButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

                    checkAnonymousStatus();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showToast("Failed to load post details.");
            }
        });
    }

    private void loadReplies() {
        postRef.child("replies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                replyList.clear();
                nestedReplies.clear();
                Map<String, Reply> allReplies = new HashMap<>();

                // First pass: collect all replies
                for (DataSnapshot replySnapshot : snapshot.getChildren()) {
                    Reply reply = replySnapshot.getValue(Reply.class);
                    if (reply != null) {
                        reply.setReplyId(replySnapshot.getKey());
                        allReplies.put(reply.getReplyId(), reply);
                    }
                }

                // Second pass: organize replies into parent-child relationships
                for (Reply reply : allReplies.values()) {
                    if (reply.getParentReplyId() == null) {
                        replyList.add(reply);
                    } else {
                        nestedReplies.computeIfAbsent(reply.getParentReplyId(), k -> new ArrayList<>())
                                .add(reply);
                    }
                }

                // Sort replies by timestamp
                replyList.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
                for (List<Reply> nested : nestedReplies.values()) {
                    nested.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));
                }

                replyAdapter.notifyDataSetChanged();
                checkAnonymousStatus();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showToast("Failed to load replies.");
            }
        });
    }

    private void checkAnonymousStatus() {
        if (currentPost != null && !currentPost.getUserAnonymousStatus().containsKey(userId)) {
            // User hasn't participated yet, allow them to choose
            anonymousReplyButton.setEnabled(true);
        } else if (currentPost != null) {
            // Lock in the user's anonymous status
            Boolean userStatus = currentPost.getUserAnonymousStatus().get(userId);
            if (userStatus != null) {
                isAnonymous = userStatus;
                anonymousReplyButton.setText(isAnonymous ? "Anonymous Reply On" : "Anonymous Reply Off");
                anonymousReplyButton.setEnabled(false);
            }
        }
    }

    private void startReplyToReply(Reply parentReply) {
        replyingTo = parentReply;
        replyingToText.setText("Replying to: " + parentReply.getDisplayName());
        replyingToText.setVisibility(View.VISIBLE);
        cancelReplyButton.setVisibility(View.VISIBLE);
        replyEditText.requestFocus();
    }

    private void cancelReply() {
        replyingTo = null;
        replyingToText.setVisibility(View.GONE);
        cancelReplyButton.setVisibility(View.GONE);
        replyEditText.setText("");
    }

    private void showEditPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_post, null);

        EditText titleEdit = dialogView.findViewById(R.id.edit_title);
        EditText contentEdit = dialogView.findViewById(R.id.edit_content);

        titleEdit.setText(currentPost.getTitle());
        contentEdit.setText(currentPost.getContent());

        builder.setView(dialogView)
                .setTitle("Edit Post")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = titleEdit.getText().toString().trim();
                    String newContent = contentEdit.getText().toString().trim();

                    if (!TextUtils.isEmpty(newTitle) && !TextUtils.isEmpty(newContent)) {
                        updatePost(newTitle, newContent);
                    } else {
                        showToast("Title and content cannot be empty");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeletePostDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This will also delete all replies.")
                .setPositiveButton("Delete", (dialog, which) -> deletePost())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePost(String newTitle, String newContent) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", newTitle);
        updates.put("content", newContent);

        postRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> showToast("Post updated successfully"))
                .addOnFailureListener(e -> showToast("Failed to update post"));
    }

    private void deletePost() {
        postRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    showToast("Post deleted successfully");
                    finish();
                })
                .addOnFailureListener(e -> showToast("Failed to delete post"));
    }

    private void sendReply() {
        final String content = replyEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("Reply cannot be empty.");
            return;
        }

        String replyId = postRef.child("replies").push().getKey();
        if (replyId == null) {
            showToast("Error creating reply.");
            return;
        }

        if (!currentPost.getUserAnonymousStatus().containsKey(userId)) {
            Map<String, Object> statusUpdate = new HashMap<>();
            statusUpdate.put("userAnonymousStatus/" + userId, isAnonymous);
            postRef.updateChildren(statusUpdate);
        }

        Reply newReply = new Reply(
                replyId,
                userId,
                userName,
                content,
                System.currentTimeMillis(),
                isAnonymous,
                replyingTo != null ? replyingTo.getReplyId() : null,
                replyingTo != null ? replyingTo.getDisplayName() : null
        );

        if (isAnonymous) {
            String anonymousName = currentPost.getAnonymousNameForUser(userId);
            newReply.setAnonymousName(anonymousName);

            Map<String, Object> updates = new HashMap<>();
            updates.put("anonymousUsers", currentPost.getAnonymousUsers());
            updates.put("nextAnonymousNumber", currentPost.getNextAnonymousNumber());
            postRef.updateChildren(updates);
        }

        postRef.child("replies").child(replyId).setValue(newReply)
                .addOnSuccessListener(aVoid -> {
                    showToast("Reply sent.");
                    replyEditText.setText("");
                    cancelReply();
                })
                .addOnFailureListener(e -> showToast("Failed to send reply."));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}