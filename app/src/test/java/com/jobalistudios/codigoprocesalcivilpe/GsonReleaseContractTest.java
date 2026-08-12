package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.annotations.SerializedName;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.List;

/** Protege los contratos JSON que deben sobrevivir a la minimización de R8. */
@RunWith(RobolectricTestRunner.class)
public class GsonReleaseContractTest {

    @Test
    public void articleRepositoryDtosDeclareStableJsonNames() throws Exception {
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonFile",
                "version", "version"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonFile",
                "blocks", "blocks"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonBlock",
                "key", "key"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonBlock",
                "preamble", "preamble"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonBlock",
                "articles", "articles"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonArticle",
                "number", "number"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonArticle",
                "title", "title"
        );
        assertJsonName(
                "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository$JsonArticle",
                "text", "text"
        );
    }

    @Test
    public void questionModelDeclaresStableJsonNames() throws Exception {
        assertJsonName(QuestionModel.class, "questionText", "questionText");
        assertJsonName(QuestionModel.class, "options", "options");
        assertJsonName(QuestionModel.class, "correctAnswerIndex", "correctAnswerIndex");
        assertJsonName(QuestionModel.class, "relatedArticle", "relatedArticle");
    }

    @Test
    public void questionBankLoadsCompleteQuestionsForTheQuiz() {
        Context context = ApplicationProvider.getApplicationContext();
        List<QuestionModel> questions = QuestionBank.getRandomQuestions(context, 100);

        assertFalse(questions.isEmpty());
        for (QuestionModel question : questions) {
            assertNotNull(question.getQuestionText());
            assertFalse(question.getQuestionText().trim().isEmpty());
            assertNotNull(question.getOptions());
            assertTrue(question.getOptions().size() >= 2);
            assertTrue(question.getCorrectAnswerIndex() >= 0);
            assertTrue(question.getCorrectAnswerIndex() < question.getOptions().size());
            assertNotNull(question.getRelatedArticle());
        }
    }

    private static void assertJsonName(
            String className,
            String fieldName,
            String expectedJsonName
    ) throws Exception {
        assertJsonName(Class.forName(className), fieldName, expectedJsonName);
    }

    private static void assertJsonName(
            Class<?> modelClass,
            String fieldName,
            String expectedJsonName
    ) throws Exception {
        Field field = modelClass.getDeclaredField(fieldName);
        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        assertNotNull(modelClass.getSimpleName() + "." + fieldName
                + " debe declarar @SerializedName", serializedName);
        assertEquals(expectedJsonName, serializedName.value());
    }
}
