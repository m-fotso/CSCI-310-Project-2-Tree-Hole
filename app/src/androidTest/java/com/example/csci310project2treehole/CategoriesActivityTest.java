package com.example.csci310project2treehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CategoriesActivityTest {
    @Rule
    public ActivityScenarioRule<CategoryActivity> activityRule =
            new ActivityScenarioRule<>(CategoryActivity.class);

    @Test
    public void testCategoryList() {
        // Verify categories are shown
        onView(withId(R.id.category_list_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddCategory() {
        // Verify button to add a new category is available
        onView(withId(R.id.fab_new_category))
                .check(matches(isDisplayed()));
    }
}
