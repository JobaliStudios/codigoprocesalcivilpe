package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizSessionContractTest {

    @Test
    public void resultIntent_containsExactCompletedSessionData() {
        Context context = ApplicationProvider.getApplicationContext();
        QuizAnswerResult first = answer("Pregunta uno", "Elegida 1", "Correcta 1", 0);
        QuizAnswerResult second = answer("Pregunta dos", "Elegida 2", "Correcta 2", 3);

        Intent intent = QuizSessionContract.createResultIntent(
                context,
                3,
                5,
                42_000L,
                List.of(first, second)
        );

        assertEquals(QuizzCPCResult.class.getName(), intent.getComponent().getClassName());
        assertEquals(3, intent.getIntExtra(QuizSessionContract.EXTRA_SCORE, -1));
        assertEquals(5, intent.getIntExtra(QuizSessionContract.EXTRA_TOTAL, -1));
        assertEquals(42_000L, intent.getLongExtra(
                QuizSessionContract.EXTRA_ELAPSED_MILLIS,
                -1L
        ));
        ArrayList<QuizAnswerResult> restored =
                QuizSessionContract.getIncorrectAnswers(intent);
        assertEquals(2, restored.size());
        assertEquals("Pregunta uno", restored.get(0).getQuestionText());
        assertEquals(0, restored.get(0).getOriginalPosition());
        assertEquals("Pregunta dos", restored.get(1).getQuestionText());
        assertEquals(3, restored.get(1).getOriginalPosition());
    }

    @Test
    public void missingAnswerExtra_returnsSafeEmptyList() {
        assertTrue(QuizSessionContract.getIncorrectAnswers(new Intent()).isEmpty());
    }

    private QuizAnswerResult answer(
            String question,
            String selected,
            String correct,
            int position
    ) {
        return new QuizAnswerResult(
                question,
                0,
                selected,
                1,
                correct,
                "Artículo 6 modificado por D-L 25940",
                position,
                false
        );
    }
}
