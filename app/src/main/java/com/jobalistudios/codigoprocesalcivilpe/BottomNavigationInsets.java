package com.jobalistudios.codigoprocesalcivilpe;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity already keeps its entire layout above system bars via
 * SystemBarInsets on android.R.id.content. Material's BottomNavigationView
 * registers a second inset listener which otherwise adds navigation-bar
 * padding to itself, changing the visible bar height after theme recreation.
 *
 * Replaces ONLY that inner listener, forwarding insets to descendants while
 * leaving the bottom bar's own padding and compact minHeight unchanged.
 */
final class BottomNavigationInsets {

    private BottomNavigationInsets() { }

    static void keepBarPaddingStable(@NonNull BottomNavigationView bar) {
        // This main bar has no design padding: its item padding and capsule
        // spacing are already managed by Material, NOT by the system inset.
        final int startLeft = bar.getPaddingLeft();
        final int startTop = bar.getPaddingTop();
        final int startRight = bar.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(bar, (view, insets) -> {
            // The outer content view owns the system-bar inset. Material's
            // implicit listener must not add it a second time on this view.
            if (view.getPaddingLeft() != startLeft
                    || view.getPaddingTop() != startTop
                    || view.getPaddingRight() != startRight
                    || view.getPaddingBottom() != 0) {
                view.setPadding(startLeft, startTop, startRight, 0);
            }
            return insets;
        });

        // Covers both first inflation and reinstalls after AppCompatDelegate
        // switches between light, dark, and follow-system modes.
        ViewCompat.requestApplyInsets(bar);
    }
}
