package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.content.SharedPreferences;

/** Persiste únicamente si el onboarding inicial ya fue completado. */
public final class OnboardingPreferences {

    static final String PREFS_NAME = "onboarding_preferences";
    static final String KEY_COMPLETED = "onboarding_completed";

    private OnboardingPreferences() {
        // Utility class
    }

    public static boolean shouldShow(Context context) {
        return !getPreferences(context).getBoolean(KEY_COMPLETED, false);
    }

    public static void markCompleted(Context context) {
        getPreferences(context)
                .edit()
                .putBoolean(KEY_COMPLETED, true)
                .apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
