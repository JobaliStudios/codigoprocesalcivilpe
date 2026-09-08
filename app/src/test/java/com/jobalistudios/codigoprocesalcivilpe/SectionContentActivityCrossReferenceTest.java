package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Intent;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityCrossReferenceTest {
    private ActivityController<SectionContentActivity> controller;

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void realArticle495_relatedArticleReplacesContentWithoutStartingAnotherReader() {
        Application application = ApplicationProvider.getApplicationContext();
        ArticleNavigationResolver.Target source =
                ArticleNavigationResolver.resolve(application, "495");
        assertNotNull(source);
        controller = Robolectric.buildActivity(
                SectionContentActivity.class,
                source.createIntent(application)
        ).create().start().resume().visible();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        SectionContentActivity activity = controller.get();

        TextView content = activity.findViewById(R.id.textView2);
        Spanned text = (Spanned) content.getText();
        ArticleCrossReferenceSpan[] spans = text.getSpans(
                0,
                text.length(),
                ArticleCrossReferenceSpan.class
        );
        assertTrue(spans.length >= 2);

        View related = activity.findViewById(R.id.relatedArticlesContainer);
        ChipGroup chips = activity.findViewById(R.id.relatedArticlesChipGroup);
        assertEquals(View.VISIBLE, related.getVisibility());
        assertEquals(2, chips.getChildCount());
        assertEquals("Art. 424", ((Chip) chips.getChildAt(0)).getText().toString());

        chips.getChildAt(0).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertEquals(null, started);
        assertEquals("Artículo 424",
                ((TextView) activity.findViewById(R.id.currentArticleNumber)).getText().toString());
        assertFalse(activity.isFinishing());
    }

    @Test
    public void onNewIntent_reusesReaderAndUpdatesDestination() {
        Application application = ApplicationProvider.getApplicationContext();
        ArticleNavigationResolver.Target source =
                ArticleNavigationResolver.resolve(application, "330");
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(application, "424");
        assertNotNull(source);
        assertNotNull(target);
        controller = Robolectric.buildActivity(
                SectionContentActivity.class,
                source.createIntent(application)
        ).create().start().resume().visible();

        SectionContentActivity sameActivity = controller.get();
        controller.newIntent(target.createIntent(application));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertEquals(sameActivity, controller.get());
        assertEquals("Artículo 424", ((TextView) sameActivity.findViewById(
                R.id.currentArticleNumber)).getText().toString());
    }

    @Test
    public void crossReference_keepsLogicalHistoryInsideSameReader() {
        Application application = ApplicationProvider.getApplicationContext();
        ArticleNavigationResolver.Target source =
                ArticleNavigationResolver.resolve(application, "330");
        assertNotNull(source);
        controller = Robolectric.buildActivity(
                SectionContentActivity.class,
                source.createIntent(application)
        ).create().start().resume().visible();
        SectionContentActivity activity = controller.get();

        activity.openArticleReference("424");
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertEquals("Artículo 424", ((TextView) activity.findViewById(
                R.id.currentArticleNumber)).getText().toString());

        activity.getOnBackPressedDispatcher().onBackPressed();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertEquals("Artículo 330", ((TextView) activity.findViewById(
                R.id.currentArticleNumber)).getText().toString());
        assertFalse(activity.isFinishing());
    }
}
