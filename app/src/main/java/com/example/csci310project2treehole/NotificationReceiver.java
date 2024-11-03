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

            // Re-initialize notification settings after device reboot
            SubscriptionManager subscriptionManager = new SubscriptionManager(context);

            // Check if user is logged in
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();

                // Re-check subscriptions and update notification settings
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
}