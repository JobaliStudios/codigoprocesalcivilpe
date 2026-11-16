package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.home.ArticleOfTheDaySelector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ArticleOfTheDaySelectorTest {
    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final Instant SAMPLE = Instant.parse("2026-11-16T15:00:00Z");

    private ArticleOfTheDaySelector selector(Instant now, ZoneId zone) {
        return new ArticleOfTheDaySelector(Clock.fixed(now, zone));
    }

    @Test public void sameLocalDateAndCorpusOrderAlwaysPickSameArticle() {
        Context context = ApplicationProvider.getApplicationContext();
        List<ArticleBlock> blocks = new java.util.ArrayList<>(
                ArticleRepository.getBlocks(context).values());
        assertFalse(blocks.isEmpty());
        ArticleOfTheDaySelector.Selection first = selector(SAMPLE,LIMA).select(blocks);
        ArticleOfTheDaySelector.Selection again = selector(SAMPLE.plusSeconds(60),LIMA)
                .select(blocks);
        assertNotNull(first);
        assertNotNull(again);
        assertEquals("2026-11-16",first.localDate);
        assertEquals(first.article.number,again.article.number);
        assertEquals(first.article.title,again.article.title);
        assertTrue(first.excerpt.length() <= 181);
    }

    @Test public void updatesAtLocalMidnightAndObservesTimezone() {
        Instant before = Instant.parse("2026-11-16T04:59:59Z");
        Instant after = before.plusSeconds(1);
        List<ArticleBlock> blocks = Collections.singletonList(
                block(new Article("1", "Primero", "Artículo 1.- Primero\nTexto", 0),
                        new Article("2", "Segundo", "Artículo 2.- Segundo\nTexto", 23)));
        ArticleOfTheDaySelector.Selection limaBefore = selector(before,LIMA).select(blocks);
        ArticleOfTheDaySelector.Selection limaAfter = selector(after,LIMA).select(blocks);
        ArticleOfTheDaySelector.Selection utcBefore =
                selector(before,ZoneId.of("UTC")).select(blocks);
        assertEquals("2026-11-15",limaBefore.localDate);
        assertEquals("2026-11-16",limaAfter.localDate);
        assertEquals("2026-11-16",utcBefore.localDate);
    }

    @Test public void titlelessAndRepealedArticlesNeverEligible() {
        Article empty = new Article("1","","Artículo 1.- Sin sumilla\nTexto",0);
        Article repealed = new Article("2","[Derogado]","Artículo 2.- [Derogado]\nTexto",27);
        Article active = new Article("3","Admisibilidad","Artículo 3.- Admisibilidad\nTexto",57);
        ArticleBlock block = block(empty,repealed,active);
        ArticleOfTheDaySelector.Selection pick = selector(SAMPLE,LIMA)
                .select(Collections.singletonList(block));
        assertNotNull(pick);
        assertEquals("3",pick.article.number);
        assertEquals("Admisibilidad",pick.article.title);
        assertFalse(ArticleLegalStatusResolver.isRepealed(pick.article));
    }

    @Test public void allExcludedOrEmptyCorpusHasNoSelection() {
        ArticleOfTheDaySelector selector = selector(SAMPLE,LIMA);
        assertNull(selector.select(Collections.emptyList()));
        assertNull(selector.select(Collections.singletonList(
                block(new Article("8","[Derogado]","Artículo 8.- [Derogado]",0),
                        new Article("9","","Artículo 9.-",24)))));
    }

    @Test public void excerptUsesBodyOnlyAndCutsAtWordBoundary() {
        String text = "Artículo 55.- Encabezado\n" + String.join(" ",
                Collections.nCopies(50,"palabra"));
        String preview = ArticleOfTheDaySelector.Selection.class == null ? "" :
                selector(SAMPLE,LIMA).select(Collections.singletonList(block(
                        new Article("55","Encabezado",text,0)))).excerpt;
        assertTrue(preview.startsWith("palabra "));
        assertFalse(preview.contains("Artículo 55"));
        assertTrue(preview.endsWith("…"));
        assertTrue(preview.length() <= 181);
        assertFalse(preview.endsWith("palab…"));
    }

    private ArticleBlock block(Article... articles) {
        return new ArticleBlock("synthetic", "", Arrays.asList(articles));
    }
}
