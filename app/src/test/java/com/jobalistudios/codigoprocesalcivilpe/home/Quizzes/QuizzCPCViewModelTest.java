package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

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

        assertTrue(viewModel.submitAnswer(first.getCorrectAnswerIndex()));
        assertEquals(1, viewModel.getScore());

        viewModel.moveToNextQuestion();

        // El índice incorrecto debe calcularse sobre la pregunta ACTUAL: las preguntas
        // son aleatorias y un índice de otra pregunta puede coincidir con la correcta.
        QuestionModel second = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(second);
        int wrongIndex = (second.getCorrectAnswerIndex() + 1) % second.getOptions().size();

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

    @Test
    public void incorrectAnswer_recordsSelectedAndCorrectOptionsWithQuestionMetadata() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(1);
        QuestionModel question = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(question);
        int wrongIndex = wrongIndex(question);

        assertFalse(viewModel.submitAnswer(wrongIndex));

        QuizAnswerResult result = viewModel.getIncorrectAnswers().get(0);
        assertEquals(question.getQuestionText(), result.getQuestionText());
        assertEquals(wrongIndex, result.getSelectedAnswerIndex());
        assertEquals(question.getOptions().get(wrongIndex), result.getSelectedAnswerText());
        assertEquals(question.getCorrectAnswerIndex(), result.getCorrectAnswerIndex());
        assertEquals(
                question.getOptions().get(question.getCorrectAnswerIndex()),
                result.getCorrectAnswerText()
        );
        assertEquals(question.getRelatedArticle(), result.getRelatedArticle());
        assertEquals(0, result.getOriginalPosition());
        assertFalse(result.isCorrect());
    }

    @Test
    public void correctAnswer_isNotIncludedInIncorrectAnswers() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(1);
        QuestionModel question = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(question);

        assertTrue(viewModel.submitAnswer(question.getCorrectAnswerIndex()));

        assertTrue(viewModel.getIncorrectAnswers().isEmpty());
        assertEquals(1, viewModel.getAnswerCount());
    }

    @Test
    public void severalIncorrectAnswers_keepOriginalQuizOrder() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(3);

        for (int position = 0; position < 3; position++) {
            QuestionModel question = viewModel.getCurrentQuestionLiveData().getValue();
            assertNotNull(question);
            viewModel.submitAnswer(wrongIndex(question));
            if (position < 2) {
                assertTrue(viewModel.moveToNextQuestion());
            }
        }

        List<QuizAnswerResult> errors = viewModel.getIncorrectAnswers();
        assertEquals(3, errors.size());
        assertEquals(0, errors.get(0).getOriginalPosition());
        assertEquals(1, errors.get(1).getOriginalPosition());
        assertEquals(2, errors.get(2).getOriginalPosition());
    }

    @Test
    public void submittingSameQuestionTwice_keepsFirstAnswerAndScoreOnce() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(1);
        QuestionModel question = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(question);
        int correctIndex = question.getCorrectAnswerIndex();

        assertTrue(viewModel.submitAnswer(correctIndex));
        assertTrue(viewModel.submitAnswer(wrongIndex(question)));

        assertEquals(1, viewModel.getScore());
        assertEquals(1, viewModel.getAnswerCount());
        assertEquals(correctIndex, viewModel.getCurrentAnswer().getSelectedAnswerIndex());
    }

    @Test
    public void initializingSameViewModelAgain_preservesCurrentSession() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(1);
        QuestionModel question = viewModel.getCurrentQuestionLiveData().getValue();
        assertNotNull(question);
        viewModel.submitAnswer(wrongIndex(question));

        viewModel.initQuestions(20);

        assertEquals(1, viewModel.getTotalQuestions());
        assertEquals(1, viewModel.getAnswerCount());
        assertEquals(1, viewModel.getIncorrectAnswers().size());
    }

    @Test
    public void newViewModel_startsWithoutErrorsFromPreviousSession() {
        QuizzCPCViewModel previousSession = buildViewModel();
        previousSession.initQuestions(1);
        QuestionModel question = previousSession.getCurrentQuestionLiveData().getValue();
        assertNotNull(question);
        previousSession.submitAnswer(wrongIndex(question));
        assertEquals(1, previousSession.getIncorrectAnswers().size());

        QuizzCPCViewModel newSession = buildViewModel();
        newSession.initQuestions(1);

        assertEquals(0, newSession.getAnswerCount());
        assertTrue(newSession.getIncorrectAnswers().isEmpty());
    }

    @Test
    public void invalidAnswerIndex_isRejectedWithoutRecordingResult() {
        QuizzCPCViewModel viewModel = buildViewModel();
        viewModel.initQuestions(1);

        assertFalse(viewModel.submitAnswer(-1));
        assertFalse(viewModel.submitAnswer(Integer.MAX_VALUE));
        assertEquals(0, viewModel.getAnswerCount());
        assertEquals(0, viewModel.getScore());
    }

    private QuizzCPCViewModel buildViewModel() {
        Application application = ApplicationProvider.getApplicationContext();
        return new QuizzCPCViewModel(application);
    }

    private int wrongIndex(QuestionModel question) {
        return (question.getCorrectAnswerIndex() + 1) % question.getOptions().size();
    }
}
