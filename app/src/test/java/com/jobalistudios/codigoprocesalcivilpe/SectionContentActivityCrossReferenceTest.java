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
    public void realArticle495_showsLinksAndRelatedChipsWithSafeBackStackIntent() {
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
        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(
                ArticleNavigationResolver.resolve(application, "424").getOffsetInBlock(),
                started.getIntExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1)
        );
        int destructiveFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK;
        assertEquals(0, started.getFlags() & destructiveFlags);
        assertFalse(activity.isFinishing());
    }
}
