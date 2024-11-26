package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.anything;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CategoryTest {
    @Rule
    public ActivityScenarioRule<CategoryActivity> activityRule =
            new ActivityScenarioRule<>(CategoryActivity.class);

    @Test
    public void testNavigateToPostListActivity_Academic() {
        // Simulate clicking the first item ("Academic") in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.category_list_view))
                .atPosition(0) // First item: "Academic"
                .perform(click());

        // Verify the navigation to PostListActivity
        intended(hasComponent(PostListActivity.class.getName()));

        // Verify that the intent carries the correct category
        intended(hasExtra("category", "Academic"));
    }

    @Test
    public void testNavigateToPostListActivity_Life() {
        // Simulate clicking the second item ("Life") in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.category_list_view))
                .atPosition(1) // Second item: "Life"
                .perform(click());

        // Verify the navigation to PostListActivity
        intended(hasComponent(PostListActivity.class.getName()));

        // Verify the intent carries the correct category
        intended(hasExtra("category", "Life"));
    }

    @Test
    public void testNavigateToPostListActivity_Event() {
        // Simulate clicking the third item ("Event") in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.category_list_view))
                .atPosition(2) // Third item: "Event"
                .perform(click());

        // Verify the navigation to PostListActivity
        intended(hasComponent(PostListActivity.class.getName()));

        // Verify the intent carries the correct category
        intended(hasExtra("category", "Event"));
    }

    @Test
    public void testBackButtonNavigation() {
        // Simulate clicking the back button
        onView(withId(R.id.back_button))
                .perform(click());

        // Verify the navigation to MainActivity
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testInvalidListViewItemSelection() {
        // Simulate clicking a non-existent item (out of range)
        onData(anything())
                .inAdapterView(withId(R.id.category_list_view))
                .atPosition(10) // Invalid position
                .perform(click());
    }

}
