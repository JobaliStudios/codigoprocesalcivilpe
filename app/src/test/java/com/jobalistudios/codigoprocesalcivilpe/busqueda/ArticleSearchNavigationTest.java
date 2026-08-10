package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class ArticleSearchNavigationTest {
    private static Context context;
    private static LegalSearchEngine engine;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        engine = LegalSearchEngine.create(context);
    }

    @Test
    public void numberResult_opensExactArticleWithoutInternalTextSearch() {
        ArticleSearchResult result = first("564", SearchFilter.NUMBER);

        Intent intent = ArticleSearchNavigation.createIntent(context, result, "564");

        assertNotNull(intent);
        assertEquals(SectionContentActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(result.item.article.offsetInBlock, intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
        assertFalse(intent.hasExtra(SectionContentActivity.EXTRA_INITIAL_QUERY));
    }

    @Test
    public void textResult_opensArticleAndExactMatchOffsetWithInitialQuery() {
        ArticleSearchResult result = first("embargo", SearchFilter.TEXT);

        Intent intent = ArticleSearchNavigation.createIntent(context, result, "embargo");

        assertNotNull(intent);
        int expected = result.item.article.offsetInBlock + result.matchOffsetInArticle;
        assertEquals(expected, intent.getIntExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
        assertEquals(expected, intent.getIntExtra(
                SectionContentActivity.EXTRA_INITIAL_QUERY_OFFSET, -1));
        assertEquals("embargo", intent.getStringExtra(SectionContentActivity.EXTRA_INITIAL_QUERY));
    }

    @Test
    public void alphanumericResult_keepsCanonicalStringAndOffset() {
        ArticleSearchResult result = first("506-A", SearchFilter.NUMBER);

        Intent intent = ArticleSearchNavigation.createIntent(context, result, "506-A");

        assertNotNull(intent);
        assertEquals("506-A", result.item.article.number);
        assertEquals(result.item.article.offsetInBlock, intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
    }

    private ArticleSearchResult first(String query, SearchFilter filter) {
        return engine.search(query, filter, Collections.emptySet(), Collections.emptySet())
                .results.get(0);
    }
}
