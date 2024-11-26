package com.example.csci310project2treehole;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Login before tests
        onView(withId(R.id.email))
                .perform(typeText("test@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Wait for login to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProfileImageUpload() {
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
    }

    @Test
    public void testProfileInfoUpdate() {
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_name))
                .perform(clearText(), typeText("New Name"), closeSoftKeyboard());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_name)).check(matches(withText("New Name")));
    }

    @Test
    public void testInvalidUSCID() {
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_uscid))
                .perform(clearText(), typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withText(R.string.invalid_usc_id)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogout() {
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.logout_button)).perform(click());
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testRoleSelection() {
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_role_spinner)).perform(click());
        onView(withText("Graduate Student")).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.profile_role_spinner)).check(matches(withSpinnerText("Graduate Student")));
    }
}