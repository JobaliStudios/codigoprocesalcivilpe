package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Checks the rendered border, not just the hex value stored in colors.xml. */
@RunWith(AndroidJUnit4.class)
public class BottomNavigationDividerInstrumentedTest {

    @Test
    public void oneDpDividerRendersExactPrototypeColorAndKeepsThemeInsetsStable() {
        int previous = AppCompatDelegate.getDefaultNightMode();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            for (int mode : new int[] {
                    AppCompatDelegate.MODE_NIGHT_NO,
                    AppCompatDelegate.MODE_NIGHT_YES,
                    AppCompatDelegate.MODE_NIGHT_NO
            }) {
                // Recreate as in the original reported theme-switching issue.
                scenario.onActivity(activity ->
                        AppCompatDelegate.setDefaultNightMode(mode));
                scenario.recreate();
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();

                scenario.onActivity(activity -> {
                    View bar = activity.findViewById(R.id.nav_view);
                    assertNotNull(bar);
                    assertEquals("No duplicate navigation inset", 0, bar.getPaddingBottom());

                    int thickness = activity.getResources().getDimensionPixelSize(
                            R.dimen.bottom_nav_divider_height);
                    assertTrue("A 1dp divider must occupy at least one physical pixel", thickness >= 1);

                    Drawable background = bar.getBackground();
                    assertNotNull(background);
                    Drawable.ConstantState state = background.getConstantState();
                    assertNotNull(state);
                    Drawable sample = state.newDrawable(activity.getResources()).mutate();
                    int width = 100;
                    int height = Math.max(thickness + 8, 100);
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    sample.setBounds(0, 0, width, height);
                    sample.draw(new Canvas(bitmap));

                    int dividerColor = activity.getColor(R.color.bottom_nav_top_divider);
                    int surfaceColor = activity.getColor(R.color.bottom_nav_surface);
                    if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
                        assertEquals("Light divider must be #E1CED2",
                                Color.rgb(225, 206, 210), dividerColor);
                    }
                    for (int y = 0; y < thickness; y++) {
                        assertEquals("Wrong divider color at row " + y,
                                dividerColor, bitmap.getPixel(width / 2, y));
                    }
                    for (int y = thickness; y < thickness + 4; y++) {
                        assertEquals("Divider extends below 1dp at row " + y,
                                surfaceColor, bitmap.getPixel(width / 2, y));
                    }
                    bitmap.recycle();
                });
            }
        } finally {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                    AppCompatDelegate.setDefaultNightMode(previous));
        }
    }
}
