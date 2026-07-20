package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Context;
import android.content.SharedPreferences;

/** Preferencias de lectura: mantener la pantalla encendida y tamaño de letra del contenido. */
public final class ReadingPreferenceManager {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on_enabled";
    private static final String KEY_FONT_SCALE = "content_font_scale";

    public static final float MIN_FONT_SCALE = 0.85f;
    public static final float MAX_FONT_SCALE = 1.60f;
    public static final float DEFAULT_FONT_SCALE = 1f;

    private ReadingPreferenceManager() {
    }

    public static boolean isKeepScreenOnEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_KEEP_SCREEN_ON, false);
    }

    public static void setKeepScreenOnEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply();
    }

    public static float getContentFontScale(Context context) {
        float scale = getPreferences(context).getFloat(KEY_FONT_SCALE, DEFAULT_FONT_SCALE);
        return Math.max(MIN_FONT_SCALE, Math.min(MAX_FONT_SCALE, scale));
    }

    public static void setContentFontScale(Context context, float scale) {
        getPreferences(context).edit().putFloat(KEY_FONT_SCALE, scale).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
