package com.example.csci310project2treehole;
import android.app.Activity;
import android.view.View;

import com.example.csci310project2treehole.AnimUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AnimUtilsTest {
    private Activity activity;
    private View testView;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(Activity.class);
        testView = new View(activity);
    }

    @Test
    public void testButtonClickAnimation() {
        final boolean[] completeCalled = {false};
        AnimUtils.buttonClickAnimation(testView, () -> completeCalled[0] = true);

        // Verify initial scale
        assertEquals(0.95f, testView.getScaleX(), 0.01f);
        assertEquals(0.95f, testView.getScaleY(), 0.01f);

        // Fast forward animation
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify final scale and callback
        assertEquals(1.0f, testView.getScaleX(), 0.01f);
        assertEquals(1.0f, testView.getScaleY(), 0.01f);
        assertTrue(completeCalled[0]);
    }

    @Test
    public void testFadeIn() {
        testView.setVisibility(View.GONE);
        AnimUtils.fadeIn(testView, 300);

        // Verify initial state
        assertEquals(View.VISIBLE, testView.getVisibility());
        assertEquals(0f, testView.getAlpha(), 0.01f);

        // Fast forward animation
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify final state
        assertEquals(1.0f, testView.getAlpha(), 0.01f);
    }

    @Test
    public void testFadeOut() {
        final boolean[] completeCalled = {false};
        AnimUtils.fadeOut(testView, 300, () -> completeCalled[0] = true);

        // Verify initial state
        assertEquals(1.0f, testView.getAlpha(), 0.01f);

        // Fast forward animation
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify final state
        assertEquals(View.GONE, testView.getVisibility());
        assertEquals(0f, testView.getAlpha(), 0.01f);
        assertTrue(completeCalled[0]);
    }
}