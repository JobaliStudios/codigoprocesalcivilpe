package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
public class LegalSearchEngineTest {
    private static Context context;
    private static LegalSearchEngine engine;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        engine = LegalSearchEngine.create(context);
    }

    @Test
    public void indexContainsEveryRepositoryArticle() {
        int expected = 0;
        for (ArticleBlock block : ArticleRepository.getBlocks(context).values()) {
            expected += block.articles.size();
        }

        assertEquals(expected, engine.indexSize());
    }

    @Test
    public void all_prioritizesExactArticleNumber() {
        List<ArticleSearchResult> results = search("564", SearchFilter.ALL);

        assertFalse(results.isEmpty());
        assertEquals("564", results.get(0).item.article.number);
        assertEquals(-1, results.get(0).matchOffsetInArticle);
    }

    @Test
    public void number_supportsAlphanumericAndPartialRanking() {
        assertEquals("506-A", search("artículo 506 a", SearchFilter.NUMBER)
                .get(0).item.article.number);
        assertEquals("566-A", search("566-A", SearchFilter.NUMBER)
                .get(0).item.article.number);
        assertEquals("647-A", search("647-A", SearchFilter.NUMBER)
                .get(0).item.article.number);

        List<ArticleSearchResult> partial = search("56", SearchFilter.NUMBER);
        assertEquals("56", partial.get(0).item.article.number);
        int firstContains = indexOf(partial, "156");
        int prefix = indexOf(partial, "560");
        assertTrue(prefix >= 0);
        assertTrue(firstContains < 0 || prefix < firstContains);
    }

    @Test
    public void text_returnsArticlesWithContextualHighlightedSnippet() {
        List<ArticleSearchResult> results = search("embargo", SearchFilter.TEXT);

        assertFalse(results.isEmpty());
        assertEquals("642", results.get(0).item.article.number);
        for (ArticleSearchResult result : results) {
            assertTrue(SearchTextNormalizer.normalizePlain(result.snippet).contains("embargo"));
            assertFalse(result.highlightRanges.isEmpty());
            assertTrue(result.matchOffsetInArticle >= 0);
        }
    }

    @Test
    public void text_prioritizesAndCentersExactMultiWordPhrase() {
        ArticleSearchResult result = search("medida cautelar", SearchFilter.TEXT).get(0);

        assertTrue(SearchTextNormalizer.normalizePlain(result.snippet)
                .contains("medida cautelar"));
        assertFalse(result.highlightRanges.isEmpty());
    }

    @Test
    public void text_doesNotTreatOwnArticleHeaderAsNumberSearch() {
        List<ArticleSearchResult> results = search("506-A", SearchFilter.TEXT);

        for (ArticleSearchResult result : results) {
            assertFalse("506-A".equals(result.item.article.number));
        }
    }

    @Test
    public void accentInsensitiveText_keepsOriginalAccentInSnippetRange() {
        ArticleSearchResult result = search("ejecucion", SearchFilter.TEXT).get(0);
        SearchTextNormalizer.Range range = result.highlightRanges.get(0);
        String highlighted = result.snippet.substring(range.start, range.end);

        assertEquals("ejecucion", SearchTextNormalizer.normalizePlain(highlighted));
        assertTrue(highlighted.contains("ó") || highlighted.contains("Ó"));
    }

    @Test
    public void exactTitleRanksBeforeBodyAndTiesRemainInLegalOrder() {
        List<ArticleSearchResult> embargo = search("embargo", SearchFilter.ALL);
        assertEquals("642", embargo.get(0).item.article.number);

        List<ArticleSearchResult> competencia = search("competencia", SearchFilter.TEXT);
        assertEquals("396", competencia.get(0).item.article.number);
        assertEquals("488", competencia.get(1).item.article.number);
        assertEquals(numbers(competencia), numbers(search("competencia", SearchFilter.TEXT)));
    }

    @Test
    public void sectionFilter_combinesWithTextFilter() {
        LegalSearchEngine.SearchResponse response = engine.search(
                "embargo",
                SearchFilter.TEXT,
                Collections.singleton("Sección Quinta"),
                Collections.emptySet()
        );

        assertFalse(response.results.isEmpty());
        for (ArticleSearchResult result : response.results) {
            assertEquals("Sección Quinta", result.item.sectionName);
        }
    }

    @Test
    public void favorites_withoutAndWithQuery_filtersOnlyFavoriteArticles() {
        Set<String> favorites = new HashSet<>(Arrays.asList("564", "506-A"));
        LegalSearchEngine.SearchResponse allFavorites = engine.search(
                "", SearchFilter.FAVORITES, Collections.emptySet(), favorites);
        assertEquals(2, allFavorites.results.size());
        assertEquals("506-A", allFavorites.results.get(0).item.article.number);
        assertEquals("564", allFavorites.results.get(1).item.article.number);

        LegalSearchEngine.SearchResponse matching = engine.search(
                "acceso", SearchFilter.FAVORITES, Collections.emptySet(), favorites);
        assertEquals(1, matching.results.size());
        assertEquals("564", matching.results.get(0).item.article.number);

        assertTrue(engine.search("inexistente", SearchFilter.FAVORITES,
                Collections.emptySet(), favorites).results.isEmpty());
        assertTrue(engine.search("", SearchFilter.FAVORITES,
                Collections.emptySet(), Collections.emptySet()).results.isEmpty());
    }

    @Test
    public void invalidNumberAndResultLimit_areExplicit() {
        LegalSearchEngine.SearchResponse invalid = engine.search(
                "embargo", SearchFilter.NUMBER,
                Collections.emptySet(), Collections.emptySet());
        assertTrue(invalid.invalidNumberQuery);
        assertTrue(invalid.results.isEmpty());

        assertTrue(search("artículo", SearchFilter.TEXT).size()
                <= LegalSearchEngine.MAX_RESULTS);
    }

    private List<ArticleSearchResult> search(String query, SearchFilter filter) {
        return engine.search(query, filter, Collections.emptySet(), Collections.emptySet()).results;
    }

    private int indexOf(List<ArticleSearchResult> results, String number) {
        for (int index = 0; index < results.size(); index++) {
            if (number.equals(results.get(index).item.article.number)) {
                return index;
            }
        }
        return -1;
    }

    private List<String> numbers(List<ArticleSearchResult> results) {
        java.util.ArrayList<String> numbers = new java.util.ArrayList<>();
        for (ArticleSearchResult result : results) {
            numbers.add(result.item.article.number);
        }
        return numbers;
    }
}
