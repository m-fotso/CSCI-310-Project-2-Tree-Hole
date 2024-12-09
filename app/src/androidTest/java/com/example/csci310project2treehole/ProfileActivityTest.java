package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * Black-box UI tests written by Zach Dodson
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<ProfileActivity> activityRule =
            new ActivityScenarioRule<>(ProfileActivity.class);

    @Test
    public void testProfileImageDisplay() {
        onView(withId(R.id.profile_image))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testProfileImageDefaultState() {
        // Verify default profile image is shown initially
        onView(withId(R.id.profile_image))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileButton() {
        onView(withId(R.id.edit_profile_button))
                .perform(click());
        // Should enable editing mode
        onView(withId(R.id.profile_image))
                .check(matches(isEnabled()));
    }

    @Test
    public void testProfileImageClickable() {
        onView(withId(R.id.edit_profile_button))
                .perform(click());
        onView(withId(R.id.profile_image))
                .check(matches(isClickable()));
    }
}