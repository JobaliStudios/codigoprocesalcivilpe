package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class QuizErrorStoreTest {
    private Context context;
    private QuestionModel firstQuestion;
    private QuestionModel secondQuestion;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        new QuizErrorStore(context).clear();
        firstQuestion = question("Pregunta uno", "Artículo 42");
        secondQuestion = question("Pregunta dos", "Artículo 43");
    }

    @Test
    public void incorrectQuestionSurvivesANewStoreInstance() {
        QuizErrorStore firstStore = new QuizErrorStore(context);

        firstStore.recordIncorrect(firstQuestion);

        assertTrue(new QuizErrorStore(context).contains(firstQuestion));
    }

    @Test
    public void resolvedQuestionIsRemovedWithoutChangingOtherErrors() {
        QuizErrorStore store = new QuizErrorStore(context);
        store.recordIncorrect(firstQuestion);
        store.recordIncorrect(secondQuestion);

        store.resolve(firstQuestion);

        assertFalse(store.contains(firstQuestion));
        assertTrue(store.contains(secondQuestion));
    }

    private QuestionModel question(String text, String article) {
        return new QuestionModel(text, Arrays.asList("A", "B"), 0, article);
    }
}
