package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Presentation-only BottomNavigationView for the provided prototype.
 *
 * Material's item layout anchors icons near the top and labels at the bottom,
 * which creates a large artificial gap when the navigation bar is given
 * balanced padding/height. Keep the same Material menu, NavigationUI handling,
 * indicator, keyboard/TalkBack support and full-width touch targets, but
 * center each icon+label pair as one visual group after its normal layout.
 */
public final class PrototypeBottomNavigationView extends BottomNavigationView {

    private static final Typeface LABEL_NORMAL = Typeface.create("sans-serif", Typeface.NORMAL);
    private static final Typeface LABEL_BOLD = Typeface.create("sans-serif", Typeface.BOLD);

    public PrototypeBottomNavigationView(@NonNull Context context) {
        super(context);
    }

    public PrototypeBottomNavigationView(@NonNull Context context,
                                         @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PrototypeBottomNavigationView(@NonNull Context context,
                                         @Nullable AttributeSet attrs,
                                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        enforceLabelTypography();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        enforceLabelTypography();
        alignIconAndLabelGroups();
    }

    private void enforceLabelTypography() {
        if (getChildCount() == 0 || !(getChildAt(0) instanceof ViewGroup)) {
            return;
        }
        ViewGroup menu = (ViewGroup) getChildAt(0);
        for (int i = 0; i < menu.getChildCount(); i++) {
            View item = menu.getChildAt(i);
            TextView inactive = item.findViewById(
                    com.google.android.material.R.id.navigation_bar_item_small_label_view);
            TextView active = item.findViewById(
                    com.google.android.material.R.id.navigation_bar_item_large_label_view);
            setTypefaceIfNeeded(inactive, LABEL_NORMAL);
            setTypefaceIfNeeded(active, LABEL_BOLD);
        }
    }

    private static void setTypefaceIfNeeded(@Nullable TextView label,
                                             @NonNull Typeface typeface) {
        if (label != null && !typeface.equals(label.getTypeface())) {
            label.setTypeface(typeface);
        }
    }

    private void alignIconAndLabelGroups() {
        if (getChildCount() == 0 || !(getChildAt(0) instanceof ViewGroup)) {
            return;
        }

        ViewGroup menu = (ViewGroup) getChildAt(0);
        int gap = getResources().getDimensionPixelSize(R.dimen.bottom_nav_visual_gap);

        for (int i = 0; i < menu.getChildCount(); i++) {
            View item = menu.getChildAt(i);
            View iconContainer = item.findViewById(
                    com.google.android.material.R.id.navigation_bar_item_icon_container);
            View labelGroup = item.findViewById(
                    com.google.android.material.R.id.navigation_bar_item_labels_group);

            if (iconContainer == null || labelGroup == null
                    || iconContainer.getHeight() <= 0 || labelGroup.getHeight() <= 0) {
                continue;
            }

            // Material centers its pill inside the icon container. Moving the
            // container (not the icon) preserves the indicator and its animation.
            int groupHeight = iconContainer.getHeight() + gap + labelGroup.getHeight();
            int groupTop = Math.max(0, (item.getHeight() - groupHeight) / 2);

            float iconOffset = groupTop - iconContainer.getTop();
            float labelOffset = groupTop + iconContainer.getHeight() + gap
                    - labelGroup.getTop();

            if (Math.abs(iconContainer.getTranslationY() - iconOffset) > 0.5f) {
                iconContainer.setTranslationY(iconOffset);
            }
            if (Math.abs(labelGroup.getTranslationY() - labelOffset) > 0.5f) {
                labelGroup.setTranslationY(labelOffset);
            }
        }
    }
}
