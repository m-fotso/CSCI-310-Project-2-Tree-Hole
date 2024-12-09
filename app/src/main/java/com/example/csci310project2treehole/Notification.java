package com.example.csci310project2treehole;

public class Notification {
    private String notificationId;
    private String title;
    private String message;
    private String postId;
    private String category;
    private long timestamp;
    private boolean isRead;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String notificationId, String title, String message) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Constructor with post details
    public Notification(String notificationId, String title, String message,
                        String postId, String category) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.postId = postId;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
