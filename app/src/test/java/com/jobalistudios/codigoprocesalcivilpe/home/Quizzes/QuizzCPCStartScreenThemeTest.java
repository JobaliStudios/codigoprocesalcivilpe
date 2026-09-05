package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(qualifiers = "night")
public class QuizzCPCStartScreenThemeTest {

    @Test
    public void practiceCardsKeepNightPaletteAndQuickReviewStartsTenQuestionMode() {
        QuizzCPCStartScreen activity = Robolectric
                .buildActivity(QuizzCPCStartScreen.class)
                .setup()
                .get();
        MaterialCardView all = activity.findViewById(R.id.cardQuizAll);
        MaterialCardView favorites = activity.findViewById(R.id.cardQuizFavorites);

        activity.findViewById(R.id.btnQuickReview).performClick();

        assertEquals(
                ContextCompat.getColor(activity, R.color.quiz_card_surface),
                all.getCardBackgroundColor().getDefaultColor()
        );
        assertEquals(
                ContextCompat.getColor(activity, R.color.quiz_card_surface),
                favorites.getCardBackgroundColor().getDefaultColor()
        );
        assertEquals(QuizPracticeMode.QUICK_REVIEW,
                QuizSessionContract.getConfig(shadowOf(activity).getNextStartedActivity()).getMode());
    }
}
