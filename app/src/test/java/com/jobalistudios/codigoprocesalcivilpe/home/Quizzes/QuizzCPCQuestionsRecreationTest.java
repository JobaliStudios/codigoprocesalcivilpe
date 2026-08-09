package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCQuestionsRecreationTest {

    @Test
    public void answeredQuestion_remainsAnsweredAfterActivityRecreation() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, QuizzCPCQuestions.class)
                .putExtra("QUESTION_COUNT", 5);

        try (ActivityScenario<QuizzCPCQuestions> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout options = activity.findViewById(R.id.optionsContainer);
                options.getChildAt(0).performClick();
                assertTrue(activity.findViewById(R.id.btnNext).isEnabled());
                assertAllOptionsLocked(options);
            });

            scenario.recreate();

            scenario.onActivity(activity -> {
                LinearLayout options = activity.findViewById(R.id.optionsContainer);
                assertTrue(activity.findViewById(R.id.btnNext).isEnabled());
                assertTrue(activity.findViewById(R.id.cardFeedback).isShown());
                assertAllOptionsLocked(options);
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
