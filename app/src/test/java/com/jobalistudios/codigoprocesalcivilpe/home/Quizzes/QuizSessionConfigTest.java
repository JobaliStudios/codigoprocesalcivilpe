package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class QuizSessionConfigTest {

    @Test
    public void anotherRoundKeepsPracticeModeAndClearsSessionErrors() {
        QuizSessionConfig config = new QuizSessionConfig(
                QuizPracticeMode.FAVORITES,
                null,
                10,
                true,
                Arrays.asList("question-one", "question-two")
        );

        QuizSessionConfig nextRound = config.forAnotherRound();

        assertEquals(QuizPracticeMode.FAVORITES, nextRound.getMode());
        assertEquals(10, nextRound.getRequestedQuestionCount());
        assertTrue(nextRound.getSessionErrorIds().isEmpty());
    }

    @Test
    public void parcelRoundTripKeepsSectionConfig() {
        QuizSessionConfig source = new QuizSessionConfig(
                QuizPracticeMode.SECTION,
                "seccion_primeratit",
                10,
                true,
                Arrays.asList("error")
        );
        Parcel parcel = Parcel.obtain();
        source.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        QuizSessionConfig restored = QuizSessionConfig.CREATOR.createFromParcel(parcel);

        assertEquals(source.getMode(), restored.getMode());
        assertEquals(source.getSectionId(), restored.getSectionId());
        assertEquals(source.getSessionErrorIds(), restored.getSessionErrorIds());
        parcel.recycle();
    }
}
