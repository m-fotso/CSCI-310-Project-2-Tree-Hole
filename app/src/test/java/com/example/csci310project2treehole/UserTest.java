package com.example.csci310project2treehole;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class UserTest {
    private User user;
    private static final String TAG = "UserTest";

    @Before
    public void setUp() {
        System.out.println("Setting up test");
        user = new User();
    }

    @Test
    public void testProfileImageUrlValidation() {
        System.out.println("Running testProfileImageUrlValidation");
        String validUrl = "https://firebasestorage.googleapis.com/image.jpg";
        user.setProfileImageUrl(validUrl);
        assertEquals("Profile image URL should match", validUrl, user.getProfileImageUrl());
        // Add an intentional failure to verify test is running
        assertNotNull("User should not be null", user);
    }

    @Test
    public void testUscIdValidation() {
        System.out.println("Running testUscIdValidation");
        // Test invalid USC ID
        user.setUscid("123");
        assertFalse("Short USC ID should be invalid", isValidUscid(user.getUscid()));

        // Test valid USC ID
        user.setUscid("1234567890");
        assertTrue("10-digit USC ID should be valid", isValidUscid(user.getUscid()));
    }

    @Test
    public void testRoleUpdate() {
        System.out.println("Running testRoleUpdate");
        // Test all valid roles
        String[] validRoles = {
                "Undergraduate Student",
                "Graduate Student",
                "Faculty",
                "Staff"
        };

        for (String role : validRoles) {
            user.setRole(role);
            assertEquals("Role should be set correctly", role, user.getRole());
        }

        // Test invalid role
        user.setRole("Invalid Role");
        assertNotEquals("Invalid role should not be accepted", "Invalid Role", user.getRole());
    }

    @Test
    public void testUserSubscriptions() {
        System.out.println("Running testUserSubscriptions");
        Map<String, Boolean> subscriptions = new HashMap<>();
        subscriptions.put("Academic", true);
        subscriptions.put("Life", false);
        subscriptions.put("Event", true);

        user.setSubscriptions(subscriptions);
        assertTrue("Academic subscription should be true", user.getSubscriptions().get("Academic"));
        assertFalse("Life subscription should be false", user.getSubscriptions().get("Life"));
        assertTrue("Event subscription should be true", user.getSubscriptions().get("Event"));
    }

    @Test
    public void testUserEmailValidation() {
        System.out.println("Running testUserEmailValidation");
        // Test invalid email
        user.setEmail("test@gmail.com");
        assertFalse("Non-USC email should be invalid", isValidUscEmail(user.getEmail()));

        // Test valid USC email
        user.setEmail("test@usc.edu");
        assertTrue("USC email should be valid", isValidUscEmail(user.getEmail()));

        // Test null email
        user.setEmail(null);
        assertFalse("Null email should be invalid", isValidUscEmail(user.getEmail()));
    }

    private boolean isValidUscid(String uscid) {
        return uscid != null && uscid.length() == 10 && uscid.matches("\\d+");
    }

    private boolean isValidUscEmail(String email) {
        return email != null && email.endsWith("@usc.edu");
    }
}