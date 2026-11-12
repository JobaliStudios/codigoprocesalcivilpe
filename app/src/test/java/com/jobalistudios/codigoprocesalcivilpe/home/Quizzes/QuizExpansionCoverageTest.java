package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.navigation.RelatedArticleNumberExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tres ampliaciones: cobertura legal en las seis secciones,
 * sin alterar las IDs de las preguntas de repaso ni necesitar una conexión.
 */
@RunWith(RobolectricTestRunner.class)
public class QuizExpansionCoverageTest {
    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void secondExpansionPreservesEarlierBankAndAddsThirtyTwoMoreArticles() {
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        assertTrue("El banco debe contener al menos 95 preguntas", questions.size() >= 95);

        Set<String> numbers = new HashSet<>();
        for (QuestionModel q : questions) {
            String number = RelatedArticleNumberExtractor.extract(q.getRelatedArticle());
            assertNotNull(number);
            numbers.add(number);
        }
        assertTrue("El banco ampliado debe incluir al menos 94 artículos",
                numbers.size() >= 94);
        for (String newNumber : new String[]{
                "49", "50", "52", "57", "83", "119", "120", "121", "188", "189",
                "192", "193", "196", "197", "364", "410", "424", "425", "426",
                "427", "475", "486", "546", "585", "608", "610", "688", "749",
                "751", "753", "759", "834",
                "51", "53", "55", "59", "92", "93", "123", "125", "128",
                "191", "194", "199", "200", "355", "356", "357", "428",
                "430", "442", "443", "444", "445", "448", "551", "552",
                "554", "559", "560", "561", "562", "754", "761"}) {
            assertTrue("Sin pregunta nueva para artículo " + newNumber,
                    numbers.contains(newNumber));
        }
    }

    @Test
    public void thirdExpansionReaches120QuestionsAnd119DistinctArticles() {
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        assertEquals("La tercera ampliación debe alcanzar la meta mínima", 120, questions.size());

        Set<String> numbers = new HashSet<>();
        for (QuestionModel question : questions) {
            String number = RelatedArticleNumberExtractor.extract(question.getRelatedArticle());
            assertNotNull(number);
            numbers.add(number);
        }
        assertEquals("Se esperan 119 artículos distintos", 119, numbers.size());
        for (String expected : new String[]{
                "1", "2", "7", "54", "64", "70", "94", "129", "142",
                "155", "202", "358", "420", "431", "447", "458", "468",
                "481", "548", "590", "612", "689", "760", "762", "763"}) {
            assertTrue("Falta pregunta nueva para el artículo " + expected,
                    numbers.contains(expected));
        }
    }

    @Test
    public void everyLegalSectionHasSelectableStudyQuestionsAfterExpansion() {
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        List<QuizSection> sections = QuizSectionCatalog.getAvailableSections(context, questions);
        assertEquals("Hay seis secciones CPC que deben tener preguntas", 6, sections.size());

        QuizQuestionSelector selector = QuizSelectorFactory.create(context);
        for (QuizSection section : sections) {
            assertTrue(section.getQuestionCount() > 0);
            QuizSelection selection = selector.select(QuizSessionConfig.section(section.getId()));
            assertTrue("Sección sin preguntas seleccionables: " + section.getId(),
                    selection.isAvailable());
            assertFalse(selection.getQuestions().isEmpty());
            for (QuestionModel chosen : selection.getQuestions()) {
                assertEquals(section.getId(),
                        QuizSectionCatalog.resolveSectionId(context, chosen));
            }
        }
    }

    @Test
    public void thirdBatchQuestionKeepsItsReviewErrorIdentityAfterReopening() {
        context.getSharedPreferences("quiz_error_review", Context.MODE_PRIVATE)
                .edit().clear().commit();

        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        assertEquals(120, questions.size());
        QuestionModel thirdBatchQuestion = questions.get(95);
        assertEquals("96.ª pregunta del banco: artículo 1",
                "1", RelatedArticleNumberExtractor.extract(thirdBatchQuestion.getRelatedArticle()));

        QuizErrorStore initial = new QuizErrorStore(context);
        initial.recordIncorrect(thirdBatchQuestion);
        QuizErrorStore reopened = new QuizErrorStore(context);
        assertTrue(reopened.contains(thirdBatchQuestion));

        QuizSelection errors = QuizSelectorFactory.create(context)
                .select(QuizSessionConfig.previousErrors());
        assertTrue(errors.isAvailable());
        assertTrue(errors.getQuestions().stream().anyMatch(q ->
                QuizQuestionIdentity.forQuestion(q)
                        .equals(QuizQuestionIdentity.forQuestion(thirdBatchQuestion))));

        reopened.resolve(thirdBatchQuestion);
        assertFalse(new QuizErrorStore(context).contains(thirdBatchQuestion));
        context.getSharedPreferences("quiz_error_review", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @Test
    public void newQuestionCanBeReopenedAndResolvedInReviewErrors() {
        context.getSharedPreferences("quiz_error_review", Context.MODE_PRIVATE)
                .edit().clear().commit();
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        QuestionModel newQuestion = questions.get(63);
        QuizErrorStore before = new QuizErrorStore(context);
        before.recordIncorrect(newQuestion);

        QuizErrorStore reopened = new QuizErrorStore(context);
        assertTrue(reopened.contains(newQuestion));
        QuizSelection errors = QuizSelectorFactory.create(context)
                .select(QuizSessionConfig.previousErrors());
        assertTrue(errors.isAvailable());
        assertTrue(errors.getQuestions().stream()
                .anyMatch(q -> QuizQuestionIdentity.forQuestion(q)
                        .equals(QuizQuestionIdentity.forQuestion(newQuestion))));

        reopened.resolve(newQuestion);
        assertFalse(new QuizErrorStore(context).contains(newQuestion));
        context.getSharedPreferences("quiz_error_review", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }
}
