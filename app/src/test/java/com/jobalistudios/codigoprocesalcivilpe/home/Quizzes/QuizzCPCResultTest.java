package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCResultTest {

    @Test
    public void errors_showInteractiveReviewAndForwardExactSession() {
        Context context = ApplicationProvider.getApplicationContext();
        QuizAnswerResult error = errorAnswer();
        Intent source = QuizSessionContract.createResultIntent(
                context,
                4,
                5,
                15_000L,
                List.of(error)
        );
        QuizzCPCResult activity = Robolectric.buildActivity(QuizzCPCResult.class, source)
                .setup()
                .get();

        MaterialCardView reviewCard = activity.findViewById(R.id.cardQuickReview);
        assertEquals(
                activity.getString(R.string.quiz_cpc_review_errors),
                ((TextView) activity.findViewById(R.id.tvQuickReviewTitle)).getText().toString()
        );
        assertEquals(View.VISIBLE, activity.findViewById(R.id.ivQuickReviewChevron).getVisibility());

        reviewCard.performClick();

        Intent reviewIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(reviewIntent);
        assertEquals(
                QuizzCPCReviewErrors.class.getName(),
                reviewIntent.getComponent().getClassName()
        );
        assertEquals(4, reviewIntent.getIntExtra(QuizSessionContract.EXTRA_SCORE, -1));
        assertEquals(15_000L, reviewIntent.getLongExtra(
                QuizSessionContract.EXTRA_ELAPSED_MILLIS,
                -1L
        ));
        assertEquals(
                error.getQuestionText(),
                QuizSessionContract.getIncorrectAnswers(reviewIntent).get(0).getQuestionText()
        );
    }

    @Test
    public void perfectScore_showsPositiveStateWithoutReviewAction() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent source = QuizSessionContract.createResultIntent(
                context,
                5,
                5,
                10_000L,
                Collections.emptyList()
        );
        QuizzCPCResult activity = Robolectric.buildActivity(QuizzCPCResult.class, source)
                .setup()
                .get();

        MaterialCardView reviewCard = activity.findViewById(R.id.cardQuickReview);
        assertEquals(
                activity.getString(R.string.quiz_cpc_perfect_title),
                ((TextView) activity.findViewById(R.id.tvQuickReviewTitle)).getText().toString()
        );
        assertEquals(
                activity.getString(R.string.quiz_cpc_perfect_text),
                ((TextView) activity.findViewById(R.id.tvQuickReview)).getText().toString()
        );
        assertFalse(reviewCard.isClickable());
        assertEquals(View.GONE, activity.findViewById(R.id.ivQuickReviewChevron).getVisibility());
        reviewCard.performClick();
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    private QuizAnswerResult errorAnswer() {
        return new QuizAnswerResult(
                "¿Pregunta de prueba?",
                0,
                "Artículo 5",
                1,
                "Artículo 6",
                "Artículo 6 modificado por D-L 25940",
                2,
                false
        );
    }
}
