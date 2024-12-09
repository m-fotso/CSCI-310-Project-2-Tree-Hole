package com.example.csci310project2treehole;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * White-box unit tests written by Zach Dodson
 */
public class NotificationTest {

    @Test
    public void testNotificationCreation() {
        String notificationId = "test_id";
        String title = "Test Title";
        String message = "Test Message";

        Notification notification = new Notification(notificationId, title, message);

        assertEquals(notificationId, notification.getNotificationId());
        assertEquals(title, notification.getTitle());
        assertEquals(message, notification.getMessage());
        assertFalse(notification.isRead());
    }

    @Test
    public void testNotificationWithPostDetails() {
        Notification notification = new Notification(
                "notif_id",
                "New Post",
                "New post in Academic",
                "post_id",
                "Academic"
        );

        assertEquals("post_id", notification.getPostId());
        assertEquals("Academic", notification.getCategory());
    }

    @Test
    public void testNotificationTimestamp() {
        Notification notification = new Notification("id", "title", "message");
        assertTrue(notification.getTimestamp() > 0);
        assertTrue(notification.getTimestamp() <= System.currentTimeMillis());
    }

    @Test
    public void testNotificationReadStatus() {
        Notification notification = new Notification("id", "title", "message");
        assertFalse(notification.isRead());

        notification.setRead(true);
        assertTrue(notification.isRead());
    }

    @Test
    public void testNotificationUpdate() {
        Notification notification = new Notification("id", "title", "message");
        String newTitle = "Updated Title";
        notification.setTitle(newTitle);
        assertEquals(newTitle, notification.getTitle());
    }
}
