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

import com.google.android.material.button.MaterialButton;
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
                activity.getString(R.string.quiz_cpc_review_practice_title),
                ((TextView) activity.findViewById(R.id.tvQuickReviewTitle)).getText().toString()
        );
        assertEquals(View.VISIBLE, activity.findViewById(R.id.ivQuickReviewChevron).getVisibility());

        MaterialButton detailsButton = activity.findViewById(R.id.btnReviewDetails);
        assertEquals(View.VISIBLE, detailsButton.getVisibility());
        detailsButton.performClick();
        Intent detailsIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(detailsIntent);
        assertEquals(QuizzCPCReviewErrors.class.getName(),
                detailsIntent.getComponent().getClassName());
        assertEquals(1, QuizSessionContract.getIncorrectAnswers(detailsIntent).size());
        assertEquals(error.getQuestionText(),
                QuizSessionContract.getIncorrectAnswers(detailsIntent).get(0).getQuestionText());

        reviewCard.performClick();

        Intent reviewIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(reviewIntent);
        assertEquals(
                QuizzCPCQuestions.class.getName(),
                reviewIntent.getComponent().getClassName()
        );
        assertEquals(QuizPracticeMode.PREVIOUS_ERRORS,
                QuizSessionContract.getConfig(reviewIntent).getMode());

        MaterialCardView focusCard = activity.findViewById(R.id.cardFocusSection);
        assertEquals(View.VISIBLE, focusCard.getVisibility());
        TextView focusSummary = activity.findViewById(R.id.tvFocusSectionSummary);
        String sectionId = QuizSectionCatalog.resolveSectionId(context, "Artículo 6");
        assertNotNull(sectionId);
        MaterialButton practiceSection = activity.findViewById(R.id.btnPracticeFocusSection);
        practiceSection.performClick();

        Intent focusedIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(focusedIntent);
        assertEquals(QuizzCPCQuestions.class.getName(),
                focusedIntent.getComponent().getClassName());
        assertEquals(QuizPracticeMode.SECTION,
                QuizSessionContract.getConfig(focusedIntent).getMode());
        assertEquals(sectionId, QuizSessionContract.getConfig(focusedIntent).getSectionId());
        assertFalse(focusSummary.getText().toString().isEmpty());
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
        assertEquals(View.GONE, activity.findViewById(R.id.btnReviewDetails).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.cardFocusSection).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.ivQuickReviewChevron).getVisibility());
        reviewCard.performClick();
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void unknownArticleErrorHidesOnlyFocusSectionNotDetailedReview() {
        Context context = ApplicationProvider.getApplicationContext();
        QuizAnswerResult unknown = new QuizAnswerResult(
                "Pregunta con referencia desconocida", 0, "A", 1, "B",
                "Artículo 9999", 0, false);
        Intent intent = QuizSessionContract.createResultIntent(
                context, 0, 1, 900L, List.of(unknown));
        QuizzCPCResult activity = Robolectric.buildActivity(QuizzCPCResult.class, intent)
                .setup().get();

        assertEquals(View.GONE, activity.findViewById(R.id.cardFocusSection).getVisibility());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.btnReviewDetails).getVisibility());
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
