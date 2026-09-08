package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
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

    static void applyToActivityContent(@NonNull Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
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
