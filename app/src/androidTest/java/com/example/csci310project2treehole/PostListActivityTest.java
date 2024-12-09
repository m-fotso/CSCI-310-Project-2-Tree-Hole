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
public class PostListActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginRequired() {
        // Verify we're on login screen when not authenticated
        onView(withId(R.id.login_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginScreen() {
        onView(withId(R.id.email))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .check(matches(isDisplayed()));
    }

    public void testPostAnonymous() {
        // Toggle anonymous mode
        onView(withId(R.id.anonymous_reply_button)).perform(click());

        // Add anonymous post
        onView(withId(R.id.fab_new_post)).perform(click());
        onView(withId(R.id.post_title_edittext))
                .perform(typeText("Test Academic Post"), closeSoftKeyboard());
        onView(withId(R.id.post_content_edittext))
                .perform(typeText("This is a test post content"), closeSoftKeyboard());
        onView(withId(R.id.post_button)).perform(click());

        // Verify anonymous identifier appears
        onView(withText("Anonymous 1")).check(matches(isDisplayed()));
    }


}