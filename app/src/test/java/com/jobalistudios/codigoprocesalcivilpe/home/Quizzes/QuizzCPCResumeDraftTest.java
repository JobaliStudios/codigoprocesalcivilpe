package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCResumeDraftTest {
    private Application application;
    private QuizDraftStore store;
    private List<QuestionModel> selected;

    @Before public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        store = new QuizDraftStore(application);
        store.clear();
        selected = QuestionBank.getQuestions(application).subList(0, 3);
    }

    @After public void tearDown() {
        store.clear();
    }

    @Test public void homeOffersResumeAndLaunchesExplicitResumeIntent() {
        int first = selected.get(0).getCorrectAnswerIndex();
        assertTrue(store.save(QuizSessionConfig.all().withQuestionCount(5), selected,
                Arrays.asList(first), 1, 12_000L));

        QuizzCPCStartScreen home = Robolectric.buildActivity(
                QuizzCPCStartScreen.class).setup().get();
        assertEquals(View.VISIBLE, home.findViewById(R.id.cardQuizDraft).getVisibility());
        String progress = ((TextView) home.findViewById(R.id.tvQuizDraftProgress))
                .getText().toString();
        assertTrue(progress.contains("1 de 3"));
        home.findViewById(R.id.btnQuizResumeDraft).performClick();

        Intent intent = Shadows.shadowOf(home).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(QuizzCPCQuestions.class.getName(),
                intent.getComponent().getClassName());
        assertTrue(intent.getBooleanExtra(QuizSessionContract.EXTRA_RESUME_DRAFT, false));
    }

    @Test public void resumedQuestionRestoresAnswersAndCorrectScore() {
        int first = selected.get(0).getCorrectAnswerIndex();
        assertTrue(store.save(QuizSessionConfig.all().withQuestionCount(5), selected,
                Arrays.asList(first), 1, 16_000L));

        QuizzCPCQuestions quiz = Robolectric.buildActivity(
                QuizzCPCQuestions.class,
                QuizSessionContract.createResumeQuestionIntent(application)).setup().get();
        QuizzCPCViewModel model = new ViewModelProvider(quiz)
                .get(QuizzCPCViewModel.class);
        assertEquals(1, model.getCurrentQuestionIndex());
        assertEquals(1, model.getAnswerCount());
        assertEquals(110, model.getStats().getScore());
        assertEquals(3, model.getTotalQuestions());
        assertEquals(5, model.getConfig().getRequestedQuestionCount());
        assertNotNull(store.load());
    }

    @Test public void completingTheRoundDeletesItsDraftAndStartsResult() {
        Intent intent = new Intent(application, QuizzCPCQuestions.class)
                .putExtra("QUESTION_COUNT", 1);
        QuizzCPCQuestions quiz = Robolectric.buildActivity(
                QuizzCPCQuestions.class, intent).setup().get();
        assertNotNull(store.load());

        LinearLayout options = quiz.findViewById(R.id.optionsContainer);
        options.getChildAt(0).performClick();
        assertNotNull(store.load());
        assertEquals(1, store.load().getAnsweredCount());
        quiz.findViewById(R.id.btnNext).performClick();

        assertNull(store.load());
        Intent result = Shadows.shadowOf(quiz).getNextStartedActivity();
        assertNotNull(result);
        assertEquals(QuizzCPCResult.class.getName(),
                result.getComponent().getClassName());
    }

    @Test public void invalidDraftDoesNotLaunchAnEmptyQuiz() {
        application.getSharedPreferences("quiz_session_draft", 0).edit()
                .putString("draft_v1", "{bad").commit();
        QuizzCPCQuestions quiz = Robolectric.buildActivity(
                QuizzCPCQuestions.class,
                QuizSessionContract.createResumeQuestionIntent(application)).setup().get();
        assertTrue(quiz.isFinishing());
    }
}
