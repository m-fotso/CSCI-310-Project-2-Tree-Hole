package com.example.csci310project2treehole;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String CHANNEL_ID = "usc_tree_hole_notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Get message data
        String title = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getTitle() : "New Notification";
        String message = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getBody() : "Check your app for updates";

        // Create notification
        createNotification(title, message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Store new token in Firebase for the current user
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
            com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("fcmToken")
                    .setValue(token);
        }
    }

    private void createNotification(String title, String message) {
        // Create an intent to open the app
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify(0, builder.build());
        } catch (SecurityException e) {
            // Handle the security exception if notification permission is not granted
        }
    }
}