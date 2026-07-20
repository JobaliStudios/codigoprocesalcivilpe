package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Keeps activity content clear of the status bar, display cutouts and every
 * navigation mode (gesture navigation and the classic three-button bar).
 */
final class SystemBarInsets {

    private SystemBarInsets() {
    }

    static void install(@NonNull Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityPreCreated(
                    @NonNull Activity activity,
                    @Nullable Bundle savedInstanceState
            ) {
                // Use the same edge-to-edge behavior on every supported Android version.
                // Insets are then applied explicitly after the activity creates its content.
                WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
            }

            @Override
            public void onActivityCreated(
                    @NonNull Activity activity,
                    @Nullable Bundle savedInstanceState
            ) {
                applyToActivityContent(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(
                    @NonNull Activity activity,
                    @NonNull Bundle outState
            ) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    private static void applyToActivityContent(@NonNull Activity activity) {
        View content = activity.findViewById(android.R.id.content);
        if (content == null) {
            return;
        }

        int initialPaddingLeft = content.getPaddingLeft();
        int initialPaddingTop = content.getPaddingTop();
        int initialPaddingRight = content.getPaddingRight();
        int initialPaddingBottom = content.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
            Insets safeInsets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );

            view.setPadding(
                    initialPaddingLeft + safeInsets.left,
                    initialPaddingTop + safeInsets.top,
                    initialPaddingRight + safeInsets.right,
                    initialPaddingBottom + safeInsets.bottom
            );

            // Let descendants keep receiving IME and other inset information.
            return windowInsets;
        });

        ViewCompat.requestApplyInsets(content);
    }
}
