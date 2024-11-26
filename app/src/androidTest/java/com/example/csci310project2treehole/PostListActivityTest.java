package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;

/**
 * Black-box UI tests written by Zach Dodson
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostListActivityTest {

    @Rule
    public ActivityScenarioRule<PostListActivity> activityRule =
            new ActivityScenarioRule<>(PostListActivity.class);

    @Test
    public void testCreateNewPost() {
        // Test creating a new post in Academic category
        onView(withId(R.id.fab_new_post)).perform(click());
        onView(withId(R.id.post_title_edittext))
                .perform(typeText("Test Academic Post"), closeSoftKeyboard());
        onView(withId(R.id.post_content_edittext))
                .perform(typeText("This is a test post content"), closeSoftKeyboard());
        onView(withId(R.id.post_button)).perform(click());

        // Verify post appears in list
        onView(withText("Test Academic Post")).check(matches(isDisplayed()));
    }

    @Test
    public void testCategoryNavigation() {
        // Test navigation between categories
        onView(withId(R.id.nav_academic)).perform(click());
        onView(withText("USC Tree Hole - Academic")).check(matches(isDisplayed()));

        onView(withId(R.id.nav_life)).perform(click());
        onView(withText("USC Tree Hole - Life")).check(matches(isDisplayed()));

        onView(withId(R.id.nav_events)).perform(click());
        onView(withText("USC Tree Hole - Event")).check(matches(isDisplayed()));
    }

    @Test
    public void testSubscriptionToggle() {
        // Test subscribing to a category
        onView(withId(R.id.nav_academic)).perform(click());
        onView(withId(R.id.subscription_chip)).perform(click());
        // Verify subscription status changed
        onView(withId(R.id.subscription_chip)).check(matches(isSelected()));
    }

    @Test
    public void testEmptyStateView() {
        // Test empty state view in a category
        onView(withId(R.id.nav_events)).perform(click());
        // Verify empty state view is shown when no posts exist
        onView(withId(R.id.empty_state_layout)).check(matches(isDisplayed()));
        onView(withText("No posts in Events")).check(matches(isDisplayed()));
    }

    @Test
    public void testPostFiltering() {
        // Test that posts are properly filtered by category
        onView(withId(R.id.nav_academic)).perform(click());

        // Create post in Academic category
        onView(withId(R.id.fab_new_post)).perform(click());
        onView(withId(R.id.post_title_edittext))
                .perform(typeText("Academic Post"), closeSoftKeyboard());
        onView(withId(R.id.post_content_edittext))
                .perform(typeText("Academic content"), closeSoftKeyboard());
        onView(withId(R.id.post_button)).perform(click());

        // Switch to Life category
        onView(withId(R.id.nav_life)).perform(click());

        // Verify academic post is not visible in Life category
        onView(withText("Academic Post")).check(doesNotExist());
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