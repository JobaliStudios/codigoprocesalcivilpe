package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Test;

import java.util.Arrays;

public class QuizQuestionIdentityTest {

    @Test
    public void sameQuestionProducesStableOpaqueIdentity() {
        QuestionModel question = new QuestionModel(
                "¿Cuál es el plazo?",
                Arrays.asList("Uno", "Dos"),
                1,
                "Artículo 42"
        );

        String first = QuizQuestionIdentity.forQuestion(question);
        String second = QuizQuestionIdentity.forQuestion(question);

        assertEquals(first, second);
        assertEquals(64, first.length());
    }

    @Test
    public void distinctQuestionMetadataProducesDifferentIdentity() {
        QuestionModel first = new QuestionModel(
                "¿Cuál es el plazo?", Arrays.asList("Uno", "Dos"), 1, "Artículo 42"
        );
        QuestionModel second = new QuestionModel(
                "¿Cuál es el plazo?", Arrays.asList("Uno", "Dos"), 0, "Artículo 42"
        );

        assertNotEquals(QuizQuestionIdentity.forQuestion(first), QuizQuestionIdentity.forQuestion(second));
    }
}
