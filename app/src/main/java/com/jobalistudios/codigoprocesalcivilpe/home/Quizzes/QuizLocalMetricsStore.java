package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Resumen voluntario y exclusivamente local de sesiones completadas.
 * No registra artículos, identificadores de preguntas, fechas, perfiles ni eventos de red.
 */
public final class QuizLocalMetricsStore {
    private static final String PREFS = "quiz_local_metrics";
    private static final String ENABLED = "enabled";
    private static final String COMPLETED = "completed";
    private static final String ANSWERED = "answered";
    private static final String CORRECT = "correct";

    private final SharedPreferences preferences;

    public QuizLocalMetricsStore(@NonNull Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public synchronized boolean isEnabled() {
        return preferences.getBoolean(ENABLED, false);
    }

    /** Apagar el registro borra también sus contadores anteriores. */
    public synchronized void setEnabled(boolean enabled) {
        if (enabled) {
            preferences.edit().putBoolean(ENABLED, true).commit();
        } else {
            clear();
        }
    }

    /** Solo se suman sesiones terminadas, y únicamente tras aceptar el registro local. */
    public synchronized boolean recordCompleted(int correct, int total) {
        if (!isEnabled() || total < 1 || total > 120 || correct < 0 || correct > total) {
            return false;
        }
        long completed = preferences.getLong(COMPLETED, 0L);
        long answered = preferences.getLong(ANSWERED, 0L);
        long correctCount = preferences.getLong(CORRECT, 0L);
        return preferences.edit()
                .putLong(COMPLETED, safeAdd(completed, 1L))
                .putLong(ANSWERED, safeAdd(answered, total))
                .putLong(CORRECT, safeAdd(correctCount, correct))
                .commit();
    }

    @NonNull
    public synchronized Snapshot snapshot() {
        if (!isEnabled()) {
            return new Snapshot(0L, 0L, 0L);
        }
        long completed = Math.max(0L, preferences.getLong(COMPLETED, 0L));
        long answered = Math.max(0L, preferences.getLong(ANSWERED, 0L));
        long correct = Math.min(answered, Math.max(0L, preferences.getLong(CORRECT, 0L)));
        return new Snapshot(completed, answered, correct);
    }

    public synchronized void clear() {
        preferences.edit().clear().commit();
    }

    private static long safeAdd(long oldValue, long increment) {
        return oldValue > Long.MAX_VALUE - increment
                ? Long.MAX_VALUE : Math.max(0L, oldValue) + increment;
    }

    public static final class Snapshot {
        private final long sessions;
        private final long answered;
        private final long correct;

        private Snapshot(long sessions, long answered, long correct) {
            this.sessions = sessions;
            this.answered = answered;
            this.correct = correct;
        }

        public long getSessions() { return sessions; }
        public long getAnswered() { return answered; }
        public long getCorrect() { return correct; }

        /** Porcentaje agregado de respuestas, no indicador de dominio jurídico. */
        public int getAccuracyPercent() {
            return answered == 0L ? 0
                    : (int) Math.round(100.0d * correct / answered);
        }
    }
}
