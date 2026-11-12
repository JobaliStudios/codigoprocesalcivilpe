package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Limita una invitación opcional a valorar la app, sin puntajes ni telemetría.
 * Usa un contador local separado del resumen educativo voluntario.
 */
public final class QuizReviewPromptPolicy {
    private static final String PREFS = "quiz_review_prompt";
    private static final String COMPLETED = "completed_sessions";
    private static final String SHOWN = "times_shown";
    private static final String LAST_SHOWN = "last_shown_ms";
    private static final String NEVER_AGAIN = "never_again";
    private static final int MIN_COMPLETED = 3;
    private static final int MAX_SHOWN = 2;
    static final long COOLDOWN_MS = 90L * 24 * 60 * 60 * 1000;

    private final SharedPreferences preferences;

    public QuizReviewPromptPolicy(@NonNull Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Se llama solamente al terminar una ronda, nunca por aciertos o por satisfacción. */
    public synchronized void onSessionCompleted() {
        int count = preferences.getInt(COMPLETED, 0);
        if (count < Integer.MAX_VALUE) {
            preferences.edit().putInt(COMPLETED, count + 1).commit();
        }
    }

    /**
     * Consume una de las dos oportunidades como máximo; no vuelve a mostrarse
     * durante los siguientes 90 días, aunque la persona elija «Ahora no».
     */
    public synchronized boolean claimIfEligible(long nowMillis) {
        int completed = preferences.getInt(COMPLETED, 0);
        int shown = preferences.getInt(SHOWN, 0);
        long lastShown = preferences.getLong(LAST_SHOWN, 0L);
        if (nowMillis <= 0L || completed < MIN_COMPLETED || shown >= MAX_SHOWN
                || preferences.getBoolean(NEVER_AGAIN, false)
                || (lastShown > 0L && (nowMillis < lastShown
                        || nowMillis - lastShown < COOLDOWN_MS))) {
            return false;
        }
        return preferences.edit()
                .putInt(SHOWN, shown + 1)
                .putLong(LAST_SHOWN, nowMillis)
                .commit();
    }

    public synchronized void neverAskAgain() {
        preferences.edit().putBoolean(NEVER_AGAIN, true).commit();
    }

    public synchronized void clear() {
        preferences.edit().clear().commit();
    }
}
