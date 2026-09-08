package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemePreferenceManager {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE_ENABLED = "dark_mode_enabled";

    private ThemePreferenceManager() {
        // Utility class
    }

    public static boolean isDarkModeEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_DARK_MODE_ENABLED, false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled) {
        getPreferences(context)
                .edit()
                .putBoolean(KEY_DARK_MODE_ENABLED, enabled)
                .apply();
    }

    public static int getAppNightMode(Context context) {
        return isDarkModeEnabled(context)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
