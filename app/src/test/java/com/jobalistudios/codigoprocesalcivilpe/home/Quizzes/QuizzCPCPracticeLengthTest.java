package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCPracticeLengthTest {
    @Before public void clearOldDraft() {
        new QuizDraftStore(androidx.test.core.app.ApplicationProvider.getApplicationContext()).clear();
    }

    @After public void clearDraftAfterTest() {
        new QuizDraftStore(androidx.test.core.app.ApplicationProvider.getApplicationContext()).clear();
    }

    @Test
    public void defaultIsTenAndTwentyQuestionsReachTheFullBankSelector() {
        QuizzCPCStartScreen activity = Robolectric
                .buildActivity(QuizzCPCStartScreen.class).setup().get();
        MaterialButtonToggleGroup group = activity.findViewById(R.id.quizQuestionCountGroup);
        assertEquals(R.id.btnQuizCount10, group.getCheckedButtonId());

        group.check(R.id.btnQuizCount20);
        activity.findViewById(R.id.cardQuizAll).performClick();

        Intent launched = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(launched);
        QuizSessionConfig selected = QuizSessionContract.getConfig(launched);
        assertEquals(QuizPracticeMode.ALL, selected.getMode());
        assertEquals(20, selected.getRequestedQuestionCount());
        assertEquals(20, QuizSelectorFactory.create(activity).select(selected).getQuestions().size());
    }

    @Test
    public void selectedFiveAlsoAppliesToQuickReview() {
        QuizzCPCStartScreen activity = Robolectric
                .buildActivity(QuizzCPCStartScreen.class).setup().get();
        MaterialButtonToggleGroup group = activity.findViewById(R.id.quizQuestionCountGroup);
        group.check(R.id.btnQuizCount5);
        activity.findViewById(R.id.btnQuickReview).performClick();

        Intent launched = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(launched);
        QuizSessionConfig selected = QuizSessionContract.getConfig(launched);
        assertEquals(QuizPracticeMode.QUICK_REVIEW, selected.getMode());
        assertEquals(5, selected.getRequestedQuestionCount());
        assertEquals(5, QuizSelectorFactory.create(activity).select(selected).getQuestions().size());
    }

    @Test
    public void selectedTwentySurvivesActivityRecreation() {
        try (ActivityScenario<QuizzCPCStartScreen> scenario =
                     ActivityScenario.launch(QuizzCPCStartScreen.class)) {
            scenario.onActivity(activity -> {
                MaterialButtonToggleGroup group =
                        activity.findViewById(R.id.quizQuestionCountGroup);
                group.check(R.id.btnQuizCount20);
                assertEquals(R.id.btnQuizCount20, group.getCheckedButtonId());
            });
            scenario.recreate();
            scenario.onActivity(activity -> {
                MaterialButtonToggleGroup group =
                        activity.findViewById(R.id.quizQuestionCountGroup);
                assertEquals(R.id.btnQuizCount20, group.getCheckedButtonId());
                assertTrue(activity.findViewById(R.id.cardQuizAll).isClickable());
            });
        }
    }
}
