package com.example.csci310project2treehole;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

/**
 * White-box unit tests written by Zach Dodson
 */
public class UserTest {

    @Test
    public void testUserCreation() {
        String name = "Test User";
        String email = "test@usc.edu";
        String uscid = "1234567890";
        String role = "Undergraduate Student";
        String profileImageUrl = "https://example.com/image.jpg";
        Map<String, Boolean> subscriptions = new HashMap<>();

        User user = new User(name, email, uscid, role, profileImageUrl, subscriptions);

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(uscid, user.getUscid());
        assertEquals(role, user.getRole());
        assertEquals(profileImageUrl, user.getProfileImageUrl());
    }

    @Test
    public void testUserProfileImage() {
        User user = new User();
        String imageUrl = "https://example.com/profile.jpg";
        user.setProfileImageUrl(imageUrl);
        assertEquals(imageUrl, user.getProfileImageUrl());
    }

    @Test
    public void testUserSubscriptions() {
        User user = new User();
        Map<String, Boolean> subscriptions = new HashMap<>();
        subscriptions.put("Academic", true);
        subscriptions.put("Life", false);

        user.setSubscriptions(subscriptions);
        assertEquals(subscriptions, user.getSubscriptions());
        assertTrue(user.getSubscriptions().get("Academic"));
        assertFalse(user.getSubscriptions().get("Life"));
    }

    @Test
    public void testDefaultProfileImage() {
        User user = new User();
        assertEquals("default_profile_image_url", user.getProfileImageUrl());
    }

    @Test
    public void testUserRoleUpdate() {
        User user = new User();
        String newRole = "Graduate Student";
        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
    }
}
