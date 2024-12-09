package com.example.csci310project2treehole;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;

public class AnimUtils {
    public static void buttonClickAnimation(@NonNull View view, Runnable onComplete) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(onComplete)
                            .start();
                })
                .start();
    }

    public static void fadeIn(@NonNull View view, long duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .start();
    }

    public static void fadeOut(@NonNull View view, long duration, Runnable onComplete) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .start();
    }
}