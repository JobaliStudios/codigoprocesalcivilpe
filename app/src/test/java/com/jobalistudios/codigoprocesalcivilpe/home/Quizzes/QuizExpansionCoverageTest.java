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
 * Primera ampliación: la cobertura legal se extiende a las seis secciones,
 * sin alterar las IDs de las preguntas de repaso ni necesitar una conexión.
 */
@RunWith(RobolectricTestRunner.class)
public class QuizExpansionCoverageTest {
    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void expansionKeepsOriginalQuestionsAndAddsThirtyTwoDistinctArticles() {
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        assertTrue("El banco debe contener al menos 63 preguntas", questions.size() >= 63);

        Set<String> numbers = new HashSet<>();
        for (QuestionModel q : questions) {
            String number = RelatedArticleNumberExtractor.extract(q.getRelatedArticle());
            assertNotNull(number);
            numbers.add(number);
        }
        assertTrue("El banco ampliado debe incluir al menos 62 artículos",
                numbers.size() >= 62);
        for (String newNumber : new String[]{
                "49", "50", "52", "57", "83", "119", "120", "121", "188", "189",
                "192", "193", "196", "197", "364", "410", "424", "425", "426",
                "427", "475", "486", "546", "585", "608", "610", "688", "749",
                "751", "753", "759", "834"}) {
            assertTrue("Sin pregunta nueva para artículo " + newNumber,
                    numbers.contains(newNumber));
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
    public void newQuestionCanBeReopenedAndResolvedInReviewErrors() {
        context.getSharedPreferences("quiz_error_review", Context.MODE_PRIVATE)
                .edit().clear().commit();
        List<QuestionModel> questions = QuestionBank.getQuestions(context);
        QuestionModel newQuestion = questions.get(31);
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
