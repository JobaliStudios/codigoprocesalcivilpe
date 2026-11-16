package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

/** Migra una sola vez el antiguo booleano de 1.07 a un tema explícito de 1.08. */
public final class ThemePreferenceManager {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE_ENABLED = "dark_mode_enabled";
    private static final String KEY_APP_THEME_MODE = "app_theme_mode";

    private ThemePreferenceManager() {
    }

    @NonNull
    public static AppThemeMode getAppThemeMode(Context context) {
        SharedPreferences prefs = getPreferences(context);
        if (prefs.contains(KEY_APP_THEME_MODE)) {
            try {
                return AppThemeMode.valueOf(prefs.getString(KEY_APP_THEME_MODE,
                        AppThemeMode.SYSTEM.name()));
            } catch (IllegalArgumentException | ClassCastException invalid) {
                return AppThemeMode.SYSTEM;
            }
        }

        if (prefs.contains(KEY_DARK_MODE_ENABLED)) {
            AppThemeMode migrated = prefs.getBoolean(KEY_DARK_MODE_ENABLED, false)
                    ? AppThemeMode.DARK : AppThemeMode.LIGHT;
            // La migración se confirma de forma síncrona: no se pierde ante un cierre inmediato.
            prefs.edit().putString(KEY_APP_THEME_MODE, migrated.name())
                    .remove(KEY_DARK_MODE_ENABLED).commit();
            return migrated;
        }
        return AppThemeMode.SYSTEM;
    }

    public static void setAppThemeMode(Context context, @NonNull AppThemeMode mode) {
        getPreferences(context).edit()
                .putString(KEY_APP_THEME_MODE, mode.name())
                .remove(KEY_DARK_MODE_ENABLED).apply();
    }

    /** API de compatibilidad para consumidores antiguos; el nuevo selector usa el enum. */
    public static boolean isDarkModeEnabled(Context context) {
        return getAppThemeMode(context) == AppThemeMode.DARK;
    }

    /** API de compatibilidad; false representa una selección explícita de Claro. */
    public static void setDarkModeEnabled(Context context, boolean enabled) {
        setAppThemeMode(context, enabled ? AppThemeMode.DARK : AppThemeMode.LIGHT);
    }

    public static int getAppNightMode(Context context) {
        switch (getAppThemeMode(context)) {
            case DARK: return AppCompatDelegate.MODE_NIGHT_YES;
            case LIGHT: return AppCompatDelegate.MODE_NIGHT_NO;
            case SYSTEM:
            default: return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    /** Aplica el tema incluso tras el borrado del Centro de privacidad. */
    public static void applySavedTheme(Context context) {
        AppCompatDelegate.setDefaultNightMode(getAppNightMode(context));
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
