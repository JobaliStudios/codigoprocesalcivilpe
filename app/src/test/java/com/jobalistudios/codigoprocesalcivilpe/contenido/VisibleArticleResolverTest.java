package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

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
