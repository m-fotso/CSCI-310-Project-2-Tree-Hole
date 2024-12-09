package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationsActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginUIElements() {
        // Verify login screen elements
        onView(withId(R.id.email))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .check(matches(isDisplayed()));
        onView(withId(R.id.login_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSignupLink() {
        onView(withId(R.id.signup_text))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAppTitle() {
        onView(withId(R.id.app_name))
                .check(matches(isDisplayed()))
                .check(matches(withText("USC Tree Hole")));
    }
}