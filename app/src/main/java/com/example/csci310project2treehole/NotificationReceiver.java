package com.example.csci310project2treehole;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();

                // Re-initialize notification settings
                FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("subscriptions")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                        String category = categorySnapshot.getKey();
                                        Boolean isSubscribed = categorySnapshot.getValue(Boolean.class);

                                        if (isSubscribed != null && isSubscribed) {
                                            Log.d(TAG, "Re-subscribing to category: " + category);
                                            setupCategoryListener(context, userId, category);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.e(TAG, "Failed to re-initialize subscriptions", error.toException());
                            }
                        });
            }
        }
    }

    private void setupCategoryListener(Context context, String userId, String category) {
        FirebaseDatabase.getInstance()
                .getReference("posts")
                .child(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // Handle new posts
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if (post != null && post.getTimestamp() > System.currentTimeMillis() - 300000) { // Last 5 minutes
                                NotificationHelper.createNotification(
                                        context,
                                        "New Post in " + category,
                                        post.getTitle(),
                                        postSnapshot.getKey(),
                                        category
                                );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to listen for new posts", error.toException());
                    }
                });
    }
}