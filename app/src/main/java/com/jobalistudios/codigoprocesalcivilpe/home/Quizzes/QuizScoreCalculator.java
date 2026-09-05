package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

/** Deterministic scoring for one correct answer. Time never affects the score. */
public final class QuizScoreCalculator {
    private static final int BASE_POINTS = 100;
    private static final int MAX_STREAK_BONUS = 50;
    private static final int BONUS_PER_STREAK = 10;

    private QuizScoreCalculator() {
    }

    public static int pointsForCorrectAnswer(int resultingStreak) {
        int safeStreak = Math.max(1, resultingStreak);
        return BASE_POINTS + Math.min(safeStreak * BONUS_PER_STREAK, MAX_STREAK_BONUS);
    }
}
