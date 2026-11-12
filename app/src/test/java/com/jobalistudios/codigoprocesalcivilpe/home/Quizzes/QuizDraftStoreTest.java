package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizDraftStoreTest {
    private Application context;
    private QuizDraftStore store;
    private List<QuestionModel> questions;

    @Before public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        store = new QuizDraftStore(context);
        store.clear();
        questions = QuestionBank.getQuestions(context).subList(0, 3);
    }

    @After public void tearDown() {
        store.clear();
    }

    @Test public void savedAnswersKeepTheirExactOrderAndRecoverScoreWithoutNewReviewWrites() {
        int correct = questions.get(0).getCorrectAnswerIndex();
        int wrong = (questions.get(1).getCorrectAnswerIndex() + 1)
                % questions.get(1).getOptions().size();
        QuizSessionConfig config = QuizSessionConfig.section("seccion_primeratit")
                .withQuestionCount(5);
        assertTrue(store.save(config, questions, Arrays.asList(correct, wrong), 1, 12_400L));

        QuizDraftStore.Draft draft = store.load();
        assertNotNull(draft);
        assertEquals(3, draft.getQuestions().size());
        assertEquals(2, draft.getAnsweredCount());
        assertEquals(1, draft.getCurrentIndex());
        assertEquals(12_400L, draft.getElapsedMillis());
        assertEquals(config.getMode(), draft.getConfig().getMode());
        assertEquals(config.getSectionId(), draft.getConfig().getSectionId());
        assertEquals(5, draft.getConfig().getRequestedQuestionCount());
        assertEquals(Arrays.asList(correct, wrong), draft.getSelectedAnswers());

        QuizErrorStore errors = new QuizErrorStore(context);
        errors.clear();
        errors.recordIncorrect(questions.get(0));
        QuizzCPCViewModel viewModel = new QuizzCPCViewModel(context, errors);
        viewModel.restoreDraft(draft);
        assertEquals(1, viewModel.getStats().getCorrect());
        assertEquals(1, viewModel.getStats().getIncorrect());
        assertEquals(110, viewModel.getStats().getScore());
        assertEquals(0, viewModel.getCurrentAnswerPoints());
        assertTrue(errors.contains(questions.get(0)));
        assertEquals(2, viewModel.getAnswerCount());
        assertFalse(viewModel.getCurrentAnswer().isCorrect());
        errors.clear();
    }

    @Test public void restoringNextUnansweredQuestionPreservesPositionAndStreak() {
        int answer = questions.get(0).getCorrectAnswerIndex();
        assertTrue(store.save(QuizSessionConfig.all().withQuestionCount(5),
                questions, Collections.singletonList(answer), 1, 800L));
        QuizzCPCViewModel vm = new QuizzCPCViewModel(context);
        vm.restoreDraft(store.load());

        assertEquals(1, vm.getCurrentQuestionIndex());
        assertNull(vm.getCurrentAnswer());
        assertEquals(110, vm.getStats().getScore());
        assertEquals(1, vm.getStats().getCurrentStreak());
        assertEquals(0, vm.getCurrentAnswerPoints());
        assertEquals(1, vm.getSelectedAnswers().size());
    }

    @Test public void corruptOrStaleBankDraftIsDiscardedSafely() throws Exception {
        context.getSharedPreferences("quiz_session_draft", Context.MODE_PRIVATE)
                .edit().putString("draft_v1", "{malformed").commit();
        assertNull(store.load());

        assertTrue(store.save(QuizSessionConfig.all(), questions,
                Collections.emptyList(), 0, 0L));
        String raw = context.getSharedPreferences("quiz_session_draft", 0)
                .getString("draft_v1", null);
        JSONObject payload = new JSONObject(raw);
        JSONArray ids = payload.getJSONArray("questionIds");
        ids.put(0, "0000000000000000000000000000000000000000000000000000000000000000");
        context.getSharedPreferences("quiz_session_draft", 0)
                .edit().putString("draft_v1", payload.toString()).commit();
        assertNull(store.load());
        assertFalse(context.getSharedPreferences("quiz_session_draft", 0)
                .contains("draft_v1"));
    }

    @Test public void preventsInvalidAnswerIndexAndDuplicateQuestionEntries() {
        List<QuestionModel> repeated = Arrays.asList(questions.get(0), questions.get(0));
        assertFalse(store.save(QuizSessionConfig.all(), repeated,
                Collections.emptyList(), 0, 0));
        assertFalse(store.save(QuizSessionConfig.all(), questions,
                Collections.singletonList(99), 0, 0));
        assertNull(store.load());
    }
}
