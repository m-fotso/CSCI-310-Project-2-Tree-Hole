package com.example.csci310project2treehole;

public class Notification {
    private String notificationId;
    private String title;
    private String message;

    public Notification() {
    }

    public Notification(String notificationId, String title, String message) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
    }

    // Getters and setters

    public String getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}

