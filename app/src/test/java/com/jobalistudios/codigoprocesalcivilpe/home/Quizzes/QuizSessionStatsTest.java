package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuizSessionStatsTest {

    @Test
    public void correctAnswersIncreaseStreakAndScoreWithCappedBonus() {
        QuizSessionStats stats = new QuizSessionStats(6);

        assertEquals(110, stats.recordAnswer(true));
        assertEquals(120, stats.recordAnswer(true));
        assertEquals(130, stats.recordAnswer(true));
        assertEquals(3, stats.getCorrect());
        assertEquals(3, stats.getCurrentStreak());
        assertEquals(3, stats.getBestStreak());
        assertEquals(360, stats.getScore());

        assertEquals(140, stats.recordAnswer(true));
        assertEquals(150, stats.recordAnswer(true));
        assertEquals(650, stats.getScore());
    }

    @Test
    public void incorrectAnswerAwardsNothingAndResetsCurrentStreak() {
        QuizSessionStats stats = new QuizSessionStats(3);
        stats.recordAnswer(true);

        assertEquals(0, stats.recordAnswer(false));
        assertEquals(1, stats.getCorrect());
        assertEquals(1, stats.getIncorrect());
        assertEquals(0, stats.getCurrentStreak());
        assertEquals(1, stats.getBestStreak());
        assertEquals(110, stats.getScore());
    }
}
