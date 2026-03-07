package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

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
}
