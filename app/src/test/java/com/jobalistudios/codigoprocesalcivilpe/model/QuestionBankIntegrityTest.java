package com.jobalistudios.codigoprocesalcivilpe.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizQuestionIdentity;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizSectionCatalog;
import com.jobalistudios.codigoprocesalcivilpe.navigation.RelatedArticleNumberExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Calidad estructural del banco de estudio y conservación de IDs de repaso.
 * Un artículo resoluble NO acredita vigencia ni exactitud de la respuesta.
 */
@RunWith(RobolectricTestRunner.class)
public class QuestionBankIntegrityTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void everyQuestionHasUsableAnswersAndAResolvableLegalArticle() {
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        assertTrue("No deben desaparecer las 31 preguntas originales", questions.size() >= 31);
        Set<String> identities = new HashSet<>();

        for (QuestionModel question : questions) {
            assertNotNull(question.getQuestionText());
            assertFalse(question.getQuestionText().trim().isEmpty());
            assertNotNull(question.getOptions());
            assertTrue(question.getOptions().size() >= 2);
            assertTrue(question.getOptions().size() <= 6);
            assertTrue(question.getCorrectAnswerIndex() >= 0);
            assertTrue(question.getCorrectAnswerIndex() < question.getOptions().size());
            Set<String> distinctOptions = new HashSet<>();
            for (String option : question.getOptions()) {
                assertNotNull(option);
                assertFalse(option.trim().isEmpty());
                assertTrue("Opciones repetidas en " + question.getQuestionText(),
                        distinctOptions.add(option.trim().toLowerCase(Locale.ROOT)));
            }

            String number = RelatedArticleNumberExtractor.extract(question.getRelatedArticle());
            assertNotNull("Sin referencia de artículo: " + question.getQuestionText(), number);
            assertFalse("Artículo no encontrado: " + number,
                    ArticleRepository.findArticle(context, number).isEmpty());
            assertNotNull("Sin sección: " + number,
                    QuizSectionCatalog.resolveSectionId(context, question));
            assertTrue("ID de error duplicado: " + question.getQuestionText(),
                    identities.add(QuizQuestionIdentity.forQuestion(question)));
        }
        assertEquals(questions.size(), identities.size());
    }

    @Test
    public void randomQuestionCopiesPreserveStableErrorReviewIdentity() {
        Set<String> originalIds = new HashSet<>();
        for (QuestionModel question : QuestionBank.getQuestions(context)) {
            originalIds.add(QuizQuestionIdentity.forQuestion(question));
        }
        for (QuestionModel question : QuestionBank.getRandomQuestions(context, 1000)) {
            assertTrue("La selección aleatoria debe reutilizar IDs de repaso",
                    originalIds.contains(QuizQuestionIdentity.forQuestion(question)));
        }
    }
}
