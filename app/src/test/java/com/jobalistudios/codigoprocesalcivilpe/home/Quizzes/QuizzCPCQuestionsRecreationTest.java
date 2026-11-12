package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCQuestionsRecreationTest {
    @Before public void clearOldDraft() {
        new QuizDraftStore(androidx.test.core.app.ApplicationProvider.getApplicationContext()).clear();
    }

    @After public void clearDraftAfterTest() {
        new QuizDraftStore(androidx.test.core.app.ApplicationProvider.getApplicationContext()).clear();
    }


    @Test
    public void answeredQuestion_remainsAnsweredAfterActivityRecreation() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, QuizzCPCQuestions.class)
                .putExtra("QUESTION_COUNT", 5);

        try (ActivityScenario<QuizzCPCQuestions> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout options = activity.findViewById(R.id.optionsContainer);
                assertEquals(View.GONE, activity.findViewById(R.id.btnStudyArticle).getVisibility());
                options.getChildAt(0).performClick();
                assertEquals(View.VISIBLE, activity.findViewById(R.id.btnStudyArticle).getVisibility());
                assertTrue(activity.findViewById(R.id.btnNext).isEnabled());
                assertAllOptionsLocked(options);
            });

            scenario.recreate();

            scenario.onActivity(activity -> {
                LinearLayout options = activity.findViewById(R.id.optionsContainer);
                assertTrue(activity.findViewById(R.id.btnNext).isEnabled());
                assertTrue(activity.findViewById(R.id.cardFeedback).isShown());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.btnStudyArticle).getVisibility());
                assertAllOptionsLocked(options);
            });
        }
    }

    @Test
    public void articleStudyAction_opensTheExistingReaderAfterAnswer() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, QuizzCPCQuestions.class)
                .putExtra("QUESTION_COUNT", 5);
        try (ActivityScenario<QuizzCPCQuestions> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout options = activity.findViewById(R.id.optionsContainer);
                options.getChildAt(0).performClick();

                MaterialButton articleButton = activity.findViewById(R.id.btnStudyArticle);
                assertEquals(View.VISIBLE, articleButton.getVisibility());
                assertTrue(articleButton.performClick());

                Intent readerIntent = Shadows.shadowOf(activity).getNextStartedActivity();
                assertNotNull(readerIntent);
                assertNotNull(readerIntent.getComponent());
                assertEquals(SectionContentActivity.class.getName(),
                        readerIntent.getComponent().getClassName());
            });
        }
    }

    private void assertAllOptionsLocked(LinearLayout options) {
        for (int index = 0; index < options.getChildCount(); index++) {
            View option = options.getChildAt(index);
            assertFalse(option.isClickable());
        }
    }
}
