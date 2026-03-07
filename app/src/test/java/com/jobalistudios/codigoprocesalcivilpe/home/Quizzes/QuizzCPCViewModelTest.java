package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCViewModelTest {

    @Test
    public void initQuestions_setsInitialStateAndLoadsFirstQuestion() {
        QuizzCPCViewModel viewModel = buildViewModel();

        viewModel.initQuestions(5);

        assertEquals(5, viewModel.getTotalQuestions());
        assertEquals(0, viewModel.getCurrentQuestionIndex());
        assertEquals(0, viewModel.getScore());
        assertNotNull(viewModel.getCurrentQuestionLiveData().getValue());
    }

    @Test
    public void submitAnswer_updatesScoreOnlyWhenAnswerIsCorrect() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(3);

        QuestionModel first = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(first);

        int correctIndex = first.getCorrectAnswerIndex();
        int wrongIndex = (correctIndex + 1) % first.getOptions().size();

        assertTrue(viewModel.submitAnswer(correctIndex));
        assertEquals(1, viewModel.getScore());

        viewModel.moveToNextQuestion();

        assertFalse(viewModel.submitAnswer(wrongIndex));
        assertEquals(1, viewModel.getScore());
    }

    @Test
    public void moveToNextQuestion_advancesUntilLastQuestion() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(2);

        assertTrue(viewModel.moveToNextQuestion());
        assertEquals(1, viewModel.getCurrentQuestionIndex());

        assertFalse(viewModel.moveToNextQuestion());
        assertEquals(1, viewModel.getCurrentQuestionIndex());
    }

    private QuizzCPCViewModel buildViewModel() {
        Application application = ApplicationProvider.getApplicationContext();
        return new QuizzCPCViewModel(application);
    }
}
