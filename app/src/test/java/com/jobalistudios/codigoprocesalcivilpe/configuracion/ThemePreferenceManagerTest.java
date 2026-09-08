package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.appcompat.app.AppCompatDelegate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ThemePreferenceManagerTest {

    private static final String PREFS_NAME = "app_settings";

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Test
    public void isDarkModeEnabled_defaultsToFalse() {
        assertFalse(ThemePreferenceManager.isDarkModeEnabled(context));
    }

    @Test
    public void setDarkModeEnabled_persistsAcrossReads() {
        ThemePreferenceManager.setDarkModeEnabled(context, true);
        assertTrue(ThemePreferenceManager.isDarkModeEnabled(context));

        ThemePreferenceManager.setDarkModeEnabled(context, false);
        assertFalse(ThemePreferenceManager.isDarkModeEnabled(context));
    }

    @Test
    public void appNightMode_neverFollowsSystemTheme() {
        ThemePreferenceManager.setDarkModeEnabled(context, false);
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO,
                ThemePreferenceManager.getAppNightMode(context));

        ThemePreferenceManager.setDarkModeEnabled(context, true);
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES,
                ThemePreferenceManager.getAppNightMode(context));
    }
}
