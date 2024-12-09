package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostDetailActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginScreenShown() {
        onView(withId(R.id.login_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmailFieldDisplayed() {
        onView(withId(R.id.email))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordFieldDisplayed() {
        onView(withId(R.id.password))
                .check(matches(isDisplayed()));
    }
}