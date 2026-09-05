package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizSectionCatalogTest {

    @Test
    public void availableSectionsAreDerivedFromResolvableQuestionArticles() {
        Context context = ApplicationProvider.getApplicationContext();
        List<QuestionModel> questions = QuestionBank.getQuestions(context);

        List<QuizSection> sections = QuizSectionCatalog.getAvailableSections(context, questions);

        assertFalse(sections.isEmpty());
        for (QuizSection section : sections) {
            assertFalse(section.getId().isEmpty());
            assertFalse(section.getLabel().isEmpty());
            assertTrue(section.getQuestionCount() > 0);
        }
    }

    @Test
    public void questionSectionIsResolvedFromItsArticleInsteadOfQuestionText() {
        Context context = ApplicationProvider.getApplicationContext();
        QuestionModel question = QuestionBank.getQuestions(context).get(0);

        String sectionId = QuizSectionCatalog.resolveSectionId(context, question);

        assertNotNull(sectionId);
    }
}
