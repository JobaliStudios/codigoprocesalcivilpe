package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/** Preferencias locales exclusivas de lectura, independientes de favoritos y notas. */
public final class ReadingPreferenceManager {
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on_enabled";
    private static final String KEY_FONT_SCALE = "content_font_scale";
    private static final String KEY_READER_THEME = "reader_theme";
    private static final String KEY_READER_FONT = "reader_font";
    private static final String KEY_LINE_SPACING = "line_spacing";
    private static final String KEY_MARGINS = "margins";
    private static final String KEY_JUSTIFY = "justify";
    private static final String KEY_FOCUS_DEFAULT = "focus_default";
    private static final String KEY_SWIPE_NAVIGATION = "swipe_navigation";
    private static final String KEY_GLOSSARY_ENABLED = "glossary_enabled";

    public enum ReaderTheme { AUTO, LIGHT, SEPIA, DARK }
    public enum ReaderFont { SANS, SERIF, ACCESSIBLE }
    public enum LineSpacing {
        COMPACT(1.5f), NORMAL(1.7f), WIDE(1.9f);
        public final float multiplier;
        LineSpacing(float multiplier) { this.multiplier = multiplier; }
    }
    public enum Margins {
        NARROW(16), NORMAL(24), WIDE(40);
        public final int dp;
        Margins(int dp) { this.dp = dp; }
    }

    public static final float MIN_FONT_SCALE = 0.85f;
    public static final float MAX_FONT_SCALE = 1.60f;
    public static final float DEFAULT_FONT_SCALE = 1f;
    public static final float FONT_SCALE_STEP = 0.05f;

    private ReadingPreferenceManager() {
    }

    public static boolean isKeepScreenOnEnabled(Context context) {
        return prefs(context).getBoolean(KEY_KEEP_SCREEN_ON, false);
    }

    public static void setKeepScreenOnEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply();
    }

    public static float getContentFontScale(Context context) {
        return clamp(prefs(context).getFloat(KEY_FONT_SCALE, DEFAULT_FONT_SCALE));
    }

    public static void setContentFontScale(Context context, float scale) {
        float rounded = Math.round((clamp(scale) - MIN_FONT_SCALE) / FONT_SCALE_STEP)
                * FONT_SCALE_STEP + MIN_FONT_SCALE;
        prefs(context).edit().putFloat(KEY_FONT_SCALE, clamp(rounded)).apply();
    }

    public static ReaderTheme getReaderTheme(Context c) {
        return enumValue(prefs(c), KEY_READER_THEME, ReaderTheme.AUTO, ReaderTheme.class);
    }

    public static void setReaderTheme(Context c, ReaderTheme value) {
        setEnum(c, KEY_READER_THEME, value);
    }

    public static ReaderFont getReaderFont(Context c) {
        return enumValue(prefs(c), KEY_READER_FONT, ReaderFont.SANS, ReaderFont.class);
    }

    public static void setReaderFont(Context c, ReaderFont value) {
        setEnum(c, KEY_READER_FONT, value);
    }

    public static LineSpacing getLineSpacing(Context c) {
        return enumValue(prefs(c), KEY_LINE_SPACING, LineSpacing.NORMAL, LineSpacing.class);
    }

    public static void setLineSpacing(Context c, LineSpacing value) {
        setEnum(c, KEY_LINE_SPACING, value);
    }

    public static Margins getMargins(Context c) {
        return enumValue(prefs(c), KEY_MARGINS, Margins.NORMAL, Margins.class);
    }

    public static void setMargins(Context c, Margins value) {
        setEnum(c, KEY_MARGINS, value);
    }

    public static boolean isJustifyEnabled(Context c) {
        return prefs(c).getBoolean(KEY_JUSTIFY, false);
    }

    public static void setJustifyEnabled(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_JUSTIFY, value).apply();
    }

    public static boolean isFocusDefaultEnabled(Context c) {
        return prefs(c).getBoolean(KEY_FOCUS_DEFAULT, false);
    }

    public static void setFocusDefaultEnabled(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_FOCUS_DEFAULT, value).apply();
    }

    public static boolean isSwipeNavigationEnabled(Context c) {
        return prefs(c).getBoolean(KEY_SWIPE_NAVIGATION, false);
    }

    public static void setSwipeNavigationEnabled(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_SWIPE_NAVIGATION, value).apply();
    }

    public static boolean isGlossaryEnabled(Context c) {
        return prefs(c).getBoolean(KEY_GLOSSARY_ENABLED, true);
    }

    public static void setGlossaryEnabled(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_GLOSSARY_ENABLED, value).apply();
    }

    /** No toca ningún otro dato guardado bajo app_settings. */
    public static void resetReadingPreferences(Context context) {
        prefs(context).edit()
                .remove(KEY_KEEP_SCREEN_ON).remove(KEY_FONT_SCALE)
                .remove(KEY_READER_THEME).remove(KEY_READER_FONT)
                .remove(KEY_LINE_SPACING).remove(KEY_MARGINS)
                .remove(KEY_JUSTIFY).remove(KEY_FOCUS_DEFAULT)
                .remove(KEY_SWIPE_NAVIGATION).remove(KEY_GLOSSARY_ENABLED)
                .apply();
    }

    private static float clamp(float value) {
        if (Float.isNaN(value)) return DEFAULT_FONT_SCALE;
        return Math.max(MIN_FONT_SCALE, Math.min(MAX_FONT_SCALE, value));
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void setEnum(Context c, String key, @NonNull Enum<?> value) {
        prefs(c).edit().putString(key, value.name()).apply();
    }

    private static <E extends Enum<E>> E enumValue(
            SharedPreferences preferences, String key, E defaultValue, Class<E> type) {
        try {
            String stored = preferences.getString(key, defaultValue.name());
            return Enum.valueOf(type, stored);
        } catch (IllegalArgumentException | ClassCastException invalid) {
            return defaultValue;
        }
    }
}
