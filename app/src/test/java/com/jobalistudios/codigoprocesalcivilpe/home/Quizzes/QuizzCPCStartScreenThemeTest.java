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

@RunWith(RobolectricTestRunner.class)
@Config(qualifiers = "night")
public class QuizzCPCStartScreenThemeTest {

    @Test
    public void changingQuestionCount_keepsNightPaletteForSelectedAndDefaultCards() {
        QuizzCPCStartScreen activity = Robolectric
                .buildActivity(QuizzCPCStartScreen.class)
                .setup()
                .get();
        MaterialCardView option5 = activity.findViewById(R.id.option5);
        MaterialCardView option10 = activity.findViewById(R.id.option10);

        option5.performClick();

        assertEquals(
                ContextCompat.getColor(activity, R.color.quiz_option_selected_bg),
                option5.getCardBackgroundColor().getDefaultColor()
        );
        assertEquals(
                ContextCompat.getColor(activity, R.color.quiz_option_default_bg),
                option10.getCardBackgroundColor().getDefaultColor()
        );
    }
}
