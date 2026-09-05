package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCGamificationViewModelTest {
    private Application application;
    private QuestionModel question;
    private QuizErrorStore errorStore;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        errorStore = new QuizErrorStore(application);
        errorStore.clear();
        question = new QuestionModel("Pregunta", Arrays.asList("A", "B"), 1, "Artículo 42");
    }

    @Test
    public void firstCorrectAnswerAwardsPointsOnlyOnceEvenAfterDoubleTap() {
        QuizzCPCViewModel viewModel = new QuizzCPCViewModel(application, errorStore);
        viewModel.initSession(QuizSessionConfig.all(), Collections.singletonList(question));

        assertTrue(viewModel.submitAnswer(1));
        assertTrue(viewModel.submitAnswer(1));

        assertEquals(1, viewModel.getStats().getCorrect());
        assertEquals(110, viewModel.getStats().getScore());
        assertEquals(110, viewModel.getCurrentAnswerPoints());
    }

    @Test
    public void correctPreviousErrorIsResolvedWhileWrongAnswerIsPersisted() {
        errorStore.recordIncorrect(question);
        QuizzCPCViewModel reviewModel = new QuizzCPCViewModel(application, errorStore);
        reviewModel.initSession(QuizSessionConfig.previousErrors(), Collections.singletonList(question));

        reviewModel.submitAnswer(1);

        assertFalse(errorStore.contains(question));

        QuizzCPCViewModel newModel = new QuizzCPCViewModel(application, errorStore);
        newModel.initSession(QuizSessionConfig.all(), Collections.singletonList(question));
        newModel.submitAnswer(0);
        assertTrue(errorStore.contains(question));
    }
}
