package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class VisibleArticleResolverTest {

    @Test
    public void offsetInsideArticle560_resolvesArticle560() {
        assertOffsetResolves("560");
    }

    @Test
    public void offsetInsideArticle561_resolvesArticle561() {
        assertOffsetResolves("561");
    }

    @Test
    public void offsetInsideAlphanumericArticle_resolvesExactStringNumber() {
        assertOffsetResolves("506-A");
    }

    @Test
    public void offsetInsidePreamble_isNotTreatedAsAnArticle() {
        Article article = new Article("1", "Título", "Artículo 1.- Título", 12);
        ArticleBlock block = new ArticleBlock(
                "bloque", "Preámbulo.\n", Collections.singletonList(article));

        assertNull(VisibleArticleResolver.findAtOffset(block, 0));
        assertEquals(article, VisibleArticleResolver.findAtOffset(block, 12));
    }

    private void assertOffsetResolves(String number) {
        Context context = ApplicationProvider.getApplicationContext();
        List<ArticleRepository.Location> locations = ArticleRepository.findArticle(context, number);
        assertFalse(locations.isEmpty());
        ArticleRepository.Location location = locations.get(0);
        int offsetInsideArticle = location.article.offsetInBlock
                + Math.max(0, location.article.text.length() / 2);

        Article resolved = VisibleArticleResolver.findAtOffset(
                location.block,
                offsetInsideArticle
        );

        assertEquals(number, resolved.number);
    }
}
