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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Black-box UI tests written by Zach Dodson
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostDetailActivityTest {

    @Rule
    public ActivityScenarioRule<PostDetailActivity> activityRule =
            new ActivityScenarioRule<>(PostDetailActivity.class);

    @Test
    public void testAddReply() {
        // Add a reply to a post
        onView(withId(R.id.reply_edittext))
                .perform(typeText("This is a test reply"), closeSoftKeyboard());
        onView(withId(R.id.send_reply_button)).perform(click());

        // Verify reply appears
        onView(withText("This is a test reply")).check(matches(isDisplayed()));
    }

    @Test
    public void testAnonymousReply() {
        // Toggle anonymous mode
        onView(withId(R.id.anonymous_reply_button)).perform(click());

        // Add anonymous reply
        onView(withId(R.id.reply_edittext))
                .perform(typeText("Anonymous reply test"), closeSoftKeyboard());
        onView(withId(R.id.send_reply_button)).perform(click());

        // Verify anonymous identifier appears
        onView(withText("Anonymous 1")).check(matches(isDisplayed()));
    }

}