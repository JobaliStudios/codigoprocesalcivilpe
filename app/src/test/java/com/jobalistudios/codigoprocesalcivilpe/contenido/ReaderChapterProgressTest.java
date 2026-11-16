package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Progreso del bloque real, nunca del total del Código. */
@RunWith(RobolectricTestRunner.class)
public class ReaderChapterProgressTest {
    @Test public void realBlockPositionsAlwaysRunFromOneToTotal() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleRepository.Location location = ArticleRepository.findArticle(context, "564").get(0);
        ArticleBlock block = location.block;
        assertTrue(block.articles.size() > 1);
        ReaderChapterProgress first = ReaderChapterProgress.of(block,
                block.articles.get(0).number);
        ReaderChapterProgress last = ReaderChapterProgress.of(block,
                block.articles.get(block.articles.size() - 1).number);
        assertEquals(1, first.position);
        assertEquals(block.articles.size(), first.total);
        assertEquals(block.articles.size(), last.position);
        assertEquals(100, last.percent());
        assertEquals(1000, last.progressOnScale(1000));
    }

    @Test public void missingArticleOrMissingBlockNeverDividesByZero() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleBlock block = ArticleRepository.findArticle(context, "564").get(0).block;
        ReaderChapterProgress missing = ReaderChapterProgress.of(block, "999999");
        assertEquals(0, missing.position);
        assertEquals(0, missing.percent());
        assertEquals(0, ReaderChapterProgress.of(null, "564").progressOnScale(1000));
    }

    @Test public void progressTracksOrdinalNotTextLength() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleBlock block = ArticleRepository.findArticle(context, "564").get(0).block;
        ReaderChapterProgress second = ReaderChapterProgress.of(block,
                block.articles.get(1).number);
        assertEquals(2, second.position);
        assertEquals(Math.round(200f / block.articles.size()), second.percent());
    }
}
