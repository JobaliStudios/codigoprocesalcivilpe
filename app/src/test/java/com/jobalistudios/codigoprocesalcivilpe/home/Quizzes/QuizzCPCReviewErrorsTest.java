package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuizzCPCReviewErrorsTest {

    @Test
    public void review_rendersAnswerDetailsAndOpensResolvedArticleWithoutFinishing() {
        QuizAnswerResult error = answer("Artículo 506-A");
        QuizzCPCReviewErrors activity = createActivity(error);

        LinearLayout container = activity.findViewById(R.id.reviewErrorsContainer);
        assertEquals(1, container.getChildCount());
        assertEquals("¿Pregunta de prueba?", text(activity, R.id.tvReviewQuestion));
        assertEquals("Opción elegida", text(activity, R.id.tvReviewSelectedAnswer));
        assertEquals("Opción correcta", text(activity, R.id.tvReviewCorrectAnswer));
        assertEquals("Artículo 506-A", text(activity, R.id.tvReviewRelatedArticle));

        MaterialButton openArticle = activity.findViewById(R.id.btnReviewOpenArticle);
        assertEquals(View.VISIBLE, openArticle.getVisibility());
        openArticle.performClick();

        Intent articleIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(articleIntent);
        assertEquals(
                SectionContentActivity.class.getName(),
                articleIntent.getComponent().getClassName()
        );
        assertNotNull(articleIntent.getExtras());
        assertFalse(activity.isFinishing());
    }

    @Test
    public void invalidArticle_keepsRelatedTextAndHidesNavigationWithoutCrash() {
        QuizzCPCReviewErrors activity = createActivity(answer("Artículo 9999 inexistente"));

        assertEquals("Artículo 9999 inexistente", text(activity, R.id.tvReviewRelatedArticle));
        assertEquals(
                View.GONE,
                activity.findViewById(R.id.btnReviewOpenArticle).getVisibility()
        );
    }

    @Test
    public void missingRelatedArticle_hidesArticleSectionAndButton() {
        QuizzCPCReviewErrors activity = createActivity(answer(null));

        assertEquals(View.GONE, activity.findViewById(R.id.relatedArticleGroup).getVisibility());
        assertEquals(
                View.GONE,
                activity.findViewById(R.id.btnReviewOpenArticle).getVisibility()
        );
    }

    private QuizzCPCReviewErrors createActivity(QuizAnswerResult answer) {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = QuizSessionContract.createReviewIntent(
                context,
                4,
                5,
                2_000L,
                List.of(answer)
        );
        return Robolectric.buildActivity(QuizzCPCReviewErrors.class, intent).setup().get();
    }

    private QuizAnswerResult answer(String relatedArticle) {
        return new QuizAnswerResult(
                "¿Pregunta de prueba?",
                0,
                "Opción elegida",
                1,
                "Opción correcta",
                relatedArticle,
                1,
                false
        );
    }

    private String text(QuizzCPCReviewErrors activity, int viewId) {
        return ((TextView) activity.findViewById(viewId)).getText().toString();
    }
}
