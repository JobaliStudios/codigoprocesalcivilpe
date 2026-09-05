package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuizScoreCalculatorTest {

    @Test
    public void correctAnswerAddsBaseAndStreakBonusCappedAtFifty() {
        assertEquals(110, QuizScoreCalculator.pointsForCorrectAnswer(1));
        assertEquals(130, QuizScoreCalculator.pointsForCorrectAnswer(3));
        assertEquals(150, QuizScoreCalculator.pointsForCorrectAnswer(8));
    }
}
