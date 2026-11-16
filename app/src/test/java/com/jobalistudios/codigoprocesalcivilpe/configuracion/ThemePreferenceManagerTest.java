package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ThemePreferenceManagerTest {
    private Context context;
    private SharedPreferences prefs;

    @Before public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test public void freshInstallDefaultsToSystem() {
        assertEquals(AppThemeMode.SYSTEM, ThemePreferenceManager.getAppThemeMode(context));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                ThemePreferenceManager.getAppNightMode(context));
    }

    @Test public void oldTrueMigratesToDarkWithoutTouchingOtherPreferences() {
        prefs.edit().putBoolean("dark_mode_enabled", true).putString("other_key", "retained").commit();
        assertEquals(AppThemeMode.DARK, ThemePreferenceManager.getAppThemeMode(context));
        assertFalse(prefs.contains("dark_mode_enabled"));
        assertEquals("DARK", prefs.getString("app_theme_mode", ""));
        assertEquals("retained", prefs.getString("other_key", ""));
        assertTrue(ThemePreferenceManager.isDarkModeEnabled(context));
    }

    @Test public void oldFalseMigratesToExplicitLight() {
        prefs.edit().putBoolean("dark_mode_enabled", false).commit();
        assertEquals(AppThemeMode.LIGHT, ThemePreferenceManager.getAppThemeMode(context));
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO,
                ThemePreferenceManager.getAppNightMode(context));
    }

    @Test public void newModeWinsOverLegacyBoolean() {
        prefs.edit().putString("app_theme_mode", "SYSTEM")
                .putBoolean("dark_mode_enabled", true).commit();
        assertEquals(AppThemeMode.SYSTEM, ThemePreferenceManager.getAppThemeMode(context));
    }

    @Test public void explicitModePersistsAndApplies() {
        ThemePreferenceManager.setAppThemeMode(context, AppThemeMode.DARK);
        ThemePreferenceManager.applySavedTheme(context);
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES,
                AppCompatDelegate.getDefaultNightMode());
        ThemePreferenceManager.setAppThemeMode(context, AppThemeMode.LIGHT);
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO,
                ThemePreferenceManager.getAppNightMode(context));
        ThemePreferenceManager.setAppThemeMode(context, AppThemeMode.SYSTEM);
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                ThemePreferenceManager.getAppNightMode(context));
    }

    @Test public void legacySetterRemainsExplicitAndDoesNotForceSystem() {
        ThemePreferenceManager.setDarkModeEnabled(context, false);
        assertEquals(AppThemeMode.LIGHT, ThemePreferenceManager.getAppThemeMode(context));
        ThemePreferenceManager.setDarkModeEnabled(context, true);
        assertEquals(AppThemeMode.DARK, ThemePreferenceManager.getAppThemeMode(context));
    }

    @Test public void privacyResetRestoresSystemForNextLaunch() {
        ThemePreferenceManager.setAppThemeMode(context, AppThemeMode.DARK);
        new com.jobalistudios.codigoprocesalcivilpe.privacidad.LocalDataManager(context)
                .clearAllLocalData();
        ThemePreferenceManager.applySavedTheme(context);
        assertEquals(AppThemeMode.SYSTEM, ThemePreferenceManager.getAppThemeMode(context));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.getDefaultNightMode());
    }
}
