package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityChapterNavigationTest {
    private Application app;
    private ActivityController<SectionContentActivity> controller;

    @Before public void setUp() {
        app = ApplicationProvider.getApplicationContext();
        app.getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().clear().commit();
    }

    @After public void tearDown() {
        if (controller != null) controller.pause().stop().destroy();
    }

    @Test public void progressAndNextPillChangeWithVisibleArticle() {
        SectionContentActivity activity = launch("564");
        ArticleRepository.Location location = ArticleRepository.findArticle(app, "564").get(0);
        ArticleBlock block = location.block;
        int position = block.articles.indexOf(location.article);
        // Repository objects can be compared by identity inside the same cached block.
        int ordinal = position + 1;
        TextView chapter = activity.findViewById(R.id.readerArticlePosition);
        LinearProgressIndicator progress = activity.findViewById(R.id.readerChapterProgress);
        assertEquals(activity.getString(R.string.reader_chapter_position,
                "564", ordinal, block.articles.size()), chapter.getText().toString());
        assertEquals(activity.getString(R.string.reader_chapter_progress_accessibility,
                ordinal, block.articles.size()), progress.getContentDescription().toString());
        assertEquals(Math.round(1000f * ordinal / block.articles.size()), progress.getProgress());
        MaterialButton next = activity.findViewById(R.id.btnNextArticle);
        assertTrue(next.isEnabled());
        assertTrue(next.getText().toString().startsWith("Art."));
        next.performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(chapter.getText().toString().contains("565"));
    }

    @Test public void firstArticleDisablesPreviousButton() {
        Article first = null;
        for (ArticleBlock block : ArticleRepository.getBlocks(app).values()) {
            if (!block.articles.isEmpty()) {
                first = block.articles.get(0);
                break;
            }
        }
        assertNotNull(first);
        SectionContentActivity activity = launch(first.number);
        MaterialButton previous = activity.findViewById(R.id.btnPreviousArticle);
        assertFalse(previous.isEnabled());
    }

    @Test public void savedVisibleArticleSurvivesActivityRecreation() {
        SectionContentActivity activity = launch("564");
        Bundle state = new Bundle();
        controller.saveInstanceState(state);
        assertEquals("564", state.getString("currentArticleNumber"));
        controller.pause().stop().destroy();
        controller = Robolectric.buildActivity(SectionContentActivity.class,
                ArticleNavigationResolver.resolve(app, "564").createIntent(app))
                .create(state).start().restoreInstanceState(state).resume().visible();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(((TextView) controller.get().findViewById(R.id.readerArticlePosition))
                .getText().toString().contains("564"));
    }

    @Test public void chapterIndexOpensAndReusesNavigationActions() {
        SectionContentActivity activity = launch("564");
        activity.findViewById(R.id.btnReaderChapterIndex).performClick();
        activity.getSupportFragmentManager().executePendingTransactions();
        ChapterIndexBottomSheet sheet = (ChapterIndexBottomSheet)
                activity.getSupportFragmentManager().findFragmentByTag(ChapterIndexBottomSheet.TAG);
        assertNotNull(sheet);
        assertNotNull(sheet.requireView().findViewById(R.id.chapterIndexList));
        sheet.requireView().findViewById(R.id.chapterIndexGoToArticle).performClick();
        activity.getSupportFragmentManager().executePendingTransactions();
        assertNotNull(activity.getSupportFragmentManager().findFragmentByTag(
                com.jobalistudios.codigoprocesalcivilpe.busqueda.GoToArticleBottomSheet.TAG));
    }

    private SectionContentActivity launch(String number) {
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(app, number);
        assertNotNull(target);
        controller = Robolectric.buildActivity(SectionContentActivity.class,
                target.createIntent(app)).create().start().resume().visible();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        return controller.get();
    }
}
