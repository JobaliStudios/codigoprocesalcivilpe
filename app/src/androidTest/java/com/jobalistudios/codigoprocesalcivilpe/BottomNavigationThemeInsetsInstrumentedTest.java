package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Theme recreation and repeated gesture/three-button inset regression for the home bar. */
@RunWith(AndroidJUnit4.class)
public class BottomNavigationThemeInsetsInstrumentedTest {

    private static BottomNavigationView bar(MainActivity activity) {
        BottomNavigationView view = activity.findViewById(R.id.nav_view);
        assertNotNull(view);
        return view;
    }

    @Test
    public void repeatedSystemInsetsApplyOnlyToOuterContentNotToBottomBar() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            final int[] originalHeight = {0};
            scenario.onActivity(activity -> {
                originalHeight[0] = bar(activity).getHeight();
                assertTrue(originalHeight[0] > 0);
            });
            // Device nav bar can be the gesture area or classic three-button area.
            for (int bottom : new int[]{24, 48, 24, 0, 48}) {
                scenario.onActivity(activity -> {
                    View content = activity.findViewById(android.R.id.content);
                    WindowInsetsCompat synthetic = new WindowInsetsCompat.Builder()
                            .setInsets(WindowInsetsCompat.Type.systemBars(),
                                    Insets.of(0, 24, 0, bottom))
                            .build();
                    ViewCompat.dispatchApplyWindowInsets(content, synthetic);
                    assertEquals("Outer content must dodge the system bar", bottom,
                            content.getPaddingBottom());
                    assertEquals("Bottom nav must not duplicate the inset", 0,
                            bar(activity).getPaddingBottom());
                });
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                scenario.onActivity(activity -> assertEquals(
                        "Bottom nav height changed when system bar inset changed",
                        originalHeight[0], bar(activity).getHeight()));
            }
        }
    }

    @Test
    public void lightDarkAndSystemThemeRecreationPreserveBarHeightAndPadding() {
        int previousNightMode = AppCompatDelegate.getDefaultNightMode();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            final int[] initialHeight = {0};
            scenario.onActivity(activity -> {
                initialHeight[0] = bar(activity).getHeight();
                assertTrue("Bottom bar should have been laid out", initialHeight[0] > 0);
                assertEquals(0, bar(activity).getPaddingBottom());
            });

            int[] modes = {
                    AppCompatDelegate.MODE_NIGHT_YES,
                    AppCompatDelegate.MODE_NIGHT_NO,
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    AppCompatDelegate.MODE_NIGHT_YES,
                    AppCompatDelegate.MODE_NIGHT_NO
            };
            for (int nightMode : modes) {
                AppCompatDelegate.setDefaultNightMode(nightMode);
                scenario.recreate();
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                scenario.onActivity(activity -> {
                    BottomNavigationView view = bar(activity);
                    assertEquals("No extra bottom padding after theme switch",
                            0, view.getPaddingBottom());
                    assertEquals("Bar height changed across themes",
                            initialHeight[0], view.getHeight());
                    assertEquals(4, view.getMenu().size());
                });
            }
        } finally {
            AppCompatDelegate.setDefaultNightMode(previousNightMode);
        }
    }
}
