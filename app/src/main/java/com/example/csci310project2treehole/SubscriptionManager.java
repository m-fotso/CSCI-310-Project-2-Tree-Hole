package com.example.csci310project2treehole;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionManager {
    private static final String CHANNEL_ID = "usc_tree_hole_notifications";
    private static final String CHANNEL_NAME = "USC Tree Hole Notifications";
    private static final String CHANNEL_DESC = "Notifications for new posts in subscribed categories";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private Context context;
    private DatabaseReference userRef;
    private String userId;

    public SubscriptionManager(Context context) {
        this.context = context;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(userId);
        }
        createNotificationChannel();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    public void toggleSubscription(String category, SubscriptionCallback callback) {
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        userRef.child("subscriptions").child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean currentValue = snapshot.exists() && snapshot.getValue(Boolean.class);
                        userRef.child("subscriptions").child(category)
                                .setValue(!currentValue)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String message = !currentValue ?
                                                "Subscribed to " + category :
                                                "Unsubscribed from " + category;
                                        sendNotification("Subscription Update", message);
                                        callback.onSuccess(!currentValue);
                                    } else {
                                        callback.onError("Failed to update subscription");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void checkSubscription(String category, SubscriptionCallback callback) {
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        userRef.child("subscriptions").child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean isSubscribed = snapshot.exists() && snapshot.getValue(Boolean.class);
                        callback.onSuccess(isSubscribed);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void getAllSubscriptions(SubscriptionsListCallback callback) {
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        userRef.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Boolean> subscriptions = new HashMap<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    subscriptions.put(child.getKey(), child.getValue(Boolean.class));
                }
                callback.onSuccess(subscriptions);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void sendNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            // Handle the security exception if notification permission is not granted
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public interface SubscriptionCallback {
        void onSuccess(boolean result);
        void onError(String error);
    }

    public interface SubscriptionsListCallback {
        void onSuccess(Map<String, Boolean> subscriptions);
        void onError(String error);
    }
}