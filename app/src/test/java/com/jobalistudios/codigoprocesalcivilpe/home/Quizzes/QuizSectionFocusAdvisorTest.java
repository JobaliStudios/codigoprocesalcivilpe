package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class QuizSectionFocusAdvisorTest {
    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void choosesMostMissedSectionWithoutUsingOtherSessions() {
        QuizSectionFocusAdvisor.Focus focus = QuizSectionFocusAdvisor.recommend(
                context, Arrays.asList(
                        mistake("Artículo 6", 1),
                        mistake("Artículo 54", 2),
                        mistake("Artículo 64", 3)
                ));
        assertNotNull(focus);
        assertEquals(2, focus.getIncorrectCount());
        assertEquals(QuizSectionCatalog.resolveSectionId(context, "Artículo 54"),
                focus.getSection().getId());
    }

    @Test
    public void equalCountsPreferFirstLegalSectionInCatalog() {
        QuizSectionFocusAdvisor.Focus focus = QuizSectionFocusAdvisor.recommend(
                context, Arrays.asList(
                        mistake("Artículo 54", 1), mistake("Artículo 6", 2)
                ));
        assertNotNull(focus);
        assertEquals(1, focus.getIncorrectCount());
        assertEquals(QuizSectionCatalog.resolveSectionId(context, "Artículo 6"),
                focus.getSection().getId());
    }

    @Test
    public void noErrorsOrUnresolvableReferencesHaveNoRecommendation() {
        assertNull(QuizSectionFocusAdvisor.recommend(context, Collections.emptyList()));
        assertNull(QuizSectionFocusAdvisor.recommend(context,
                Arrays.asList(mistake(null, 0), mistake("Artículo 9999", 1))));
    }

    @Test
    public void correctAnswerDoesNotCountAsAnError() {
        QuizAnswerResult answeredCorrectly = new QuizAnswerResult(
                "Correcta", 1, "Sí", 1, "Sí", "Artículo 54", 0, true);
        QuizSectionFocusAdvisor.Focus focus = QuizSectionFocusAdvisor.recommend(
                context, Arrays.asList(answeredCorrectly, mistake("Artículo 6", 1)));
        assertNotNull(focus);
        assertEquals(1, focus.getIncorrectCount());
        assertEquals(QuizSectionCatalog.resolveSectionId(context, "Artículo 6"),
                focus.getSection().getId());
    }

    private QuizAnswerResult mistake(String reference, int index) {
        return new QuizAnswerResult("Pregunta " + index, 0, "No", 1, "Sí",
                reference, index, false);
    }
}
