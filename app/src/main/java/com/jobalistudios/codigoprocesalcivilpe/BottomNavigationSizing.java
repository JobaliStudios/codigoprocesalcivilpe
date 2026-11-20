package com.jobalistudios.codigoprocesalcivilpe;

import android.content.res.Resources;
import androidx.annotation.NonNull;

/**
 * Bottom bar from the approved prototype:
 * compact icon/label grouping at the normal Android font scale,
 * with space added only for larger accessibility font sizes.
 *
 * System navigation bar (gestures / 3 buttons) stays managed separately
 * by the existing window inset pipeline.
 */
final class BottomNavigationSizing {

    private BottomNavigationSizing() {
    }

    static int minimumHeightPx(@NonNull Resources resources) {
        int base = resources.getDimensionPixelSize(R.dimen.bottom_nav_min_height);
        int extraForLargeFont = resources.getDimensionPixelSize(
                R.dimen.bottom_nav_font_scale_extra_height);
        float scale = resources.getConfiguration().fontScale;
        if (!Float.isFinite(scale) || scale <= 1f) {
            return base;
        }
        return base + Math.round((scale - 1f) * extraForLargeFont);
    }
}
