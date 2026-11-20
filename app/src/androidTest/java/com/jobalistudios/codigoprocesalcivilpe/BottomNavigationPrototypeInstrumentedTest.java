package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Regression for the actual rendered icon/label layout from the approved prototype.
 * Checks the view geometry and typefaces after switching among all four destinations,
 * rather than merely checking XML or test-view configuration.
 */
@RunWith(AndroidJUnit4.class)
public class BottomNavigationPrototypeInstrumentedTest {

    @Test
    public void fourDestinationsHaveSansSerifLabelsAndBalancedVerticalComposition() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            int[] ids = {R.id.navigation_home, R.id.navigation_busqueda,
                    R.id.navigation_favoritos, R.id.navigation_configuracion};

            for (int destination : ids) {
                scenario.onActivity(activity -> {
                    BottomNavigationView bar = activity.findViewById(R.id.nav_view);
                    assertNotNull(bar);
                    assertTrue(bar instanceof PrototypeBottomNavigationView);
                    bar.setSelectedItemId(destination);
                });
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();

                scenario.onActivity(activity -> {
                    BottomNavigationView bar = activity.findViewById(R.id.nav_view);
                    ViewGroup menu = (ViewGroup) bar.getChildAt(0);
                    assertEquals(4, menu.getChildCount());

                    int gapPx = activity.getResources()
                            .getDimensionPixelSize(R.dimen.bottom_nav_visual_gap);
                    float tolerance = activity.getResources().getDisplayMetrics().density * 2f;
                    for (int i = 0; i < menu.getChildCount(); i++) {
                        View item = menu.getChildAt(i);
                        View icon = item.findViewById(
                                com.google.android.material.R.id.navigation_bar_item_icon_container);
                        View labels = item.findViewById(
                                com.google.android.material.R.id.navigation_bar_item_labels_group);
                        TextView small = item.findViewById(
                                com.google.android.material.R.id.navigation_bar_item_small_label_view);
                        TextView large = item.findViewById(
                                com.google.android.material.R.id.navigation_bar_item_large_label_view);
                        assertNotNull(icon);
                        assertNotNull(labels);
                        assertNotNull(small);
                        assertNotNull(large);
                        assertTrue(item.getHeight() > 0);

                        // Material's internal anchor layout must be corrected visually.
                        float actualGap = labels.getY() - (icon.getY() + icon.getHeight());
                        assertEquals("Pill-to-label gap tab " + i, gapPx, actualGap, tolerance);

                        float topWhitespace = icon.getY();
                        float bottomWhitespace = item.getHeight()
                                - (labels.getY() + labels.getHeight());
                        assertEquals("Unbalanced vertical composition tab " + i,
                                topWhitespace, bottomWhitespace, tolerance);

                        assertEquals("Inactive label must use actual sans-serif font",
                                Typeface.create("sans-serif", Typeface.NORMAL), small.getTypeface());
                        assertEquals("Active label must use actual bold sans-serif font",
                                Typeface.create("sans-serif", Typeface.BOLD), large.getTypeface());
                        assertEquals(1, small.getMaxLines());
                        assertEquals(1, large.getMaxLines());
                    }

                    assertEquals("System navigation inset must not add bottom padding",
                            0, bar.getPaddingBottom());
                });
            }
        }
    }
}
