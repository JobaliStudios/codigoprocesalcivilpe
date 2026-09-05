package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.ArticleQuickNotes;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class UserNotesAggregatorTest {
    private Context context;
    private List<FavoriteItem> favorites;
    private Map<String, List<Highlight>> highlights;
    private UserNotesAggregator aggregator;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        favorites = new ArrayList<>();
        highlights = new HashMap<>();
        aggregator = new UserNotesAggregator(new UserNotesAggregator.DataSource() {
            @Override
            public List<FavoriteItem> getFavorites() {
                return favorites;
            }

            @Override
            public Map<String, ArticleBlock> getArticleBlocks() {
                return ArticleRepository.getBlocks(context);
            }

            @Override
            public List<Highlight> getHighlights(String blockKey) {
                return highlights.getOrDefault(blockKey, Collections.emptyList());
            }
        });
    }

    @Test
    public void favoriteOnly_producesEntry() {
        favorites.add(favorite("564"));

        ArticleNotesEntry entry = onlyEntry();

        assertTrue(entry.isFavorite());
        assertEquals("564", entry.getArticleNumber());
        assertFalse(entry.getArticleTitle().isEmpty());
        assertTrue(entry.getHighlights().isEmpty());
        assertTrue(entry.getArticleNotes().isEmpty());
    }

    @Test
    public void validHighlightOnly_producesExactCurrentText() {
        Highlight highlight = validHighlight("564", "highlight-564", null, 12);
        addHighlight("564", highlight);

        ArticleNotesEntry entry = onlyEntry();

        assertFalse(entry.isFavorite());
        assertEquals(1, entry.getHighlights().size());
        assertEquals(highlight.getSnippet(), entry.getHighlights().get(0).getText());
    }

    @Test
    public void articleQuickNoteOnly_producesNoteWithoutFakeHighlight() {
        Highlight quickNote = validHighlight(
                "731",
                ArticleQuickNotes.idFor("731"),
                "Comparar con el artículo anterior.",
                firstLineLength(location("731").article.text)
        );
        addHighlight("731", quickNote);

        ArticleNotesEntry entry = onlyEntry();

        assertTrue(entry.getHighlights().isEmpty());
        assertEquals(1, entry.getArticleNotes().size());
        assertEquals("Comparar con el artículo anterior.",
                entry.getArticleNotes().get(0).getText());
    }

    @Test
    public void noPersonalContent_producesNoEntry() {
        assertTrue(aggregator.build().isEmpty());
    }

    @Test
    public void favoriteHighlightAndNotes_areGroupedOnce() {
        favorites.add(favorite("564"));
        addHighlight("564", validHighlight("564", "highlight", "Nota asociada", 10));
        addHighlight("564", validHighlight(
                "564",
                ArticleQuickNotes.idFor("564"),
                "Nota general",
                firstLineLength(location("564").article.text)
        ));

        ArticleNotesEntry entry = onlyEntry();

        assertTrue(entry.isFavorite());
        assertEquals(1, entry.getHighlights().size());
        assertEquals("Nota asociada", entry.getHighlights().get(0).getNote());
        assertEquals(1, entry.getArticleNotes().size());
    }

    @Test
    public void sameArticleAcrossSources_isNeverDuplicated() {
        favorites.add(favorite("564"));
        favorites.add(favorite("564"));
        addHighlight("564", validHighlight("564", "highlight", null, 8));

        assertEquals(1, aggregator.build().size());
    }

    @Test
    public void legalOrder_handlesAlphanumericArticle() {
        favorites.add(favorite("507"));
        favorites.add(favorite("506-A"));
        favorites.add(favorite("505"));
        favorites.add(favorite("506"));

        List<ArticleNotesEntry> result = aggregator.build();

        assertEquals("505", result.get(0).getArticleNumber());
        assertEquals("506", result.get(1).getArticleNumber());
        assertEquals("506-A", result.get(2).getArticleNumber());
        assertEquals("507", result.get(3).getArticleNumber());
    }

    @Test
    public void highlights_areOrderedByResolvedOffset() {
        addHighlight("564", validHighlightAt("564", "later", null, 30, 8));
        addHighlight("564", validHighlightAt("564", "earlier", null, 5, 8));

        ArticleNotesEntry entry = onlyEntry();

        assertTrue(entry.getHighlights().get(0).getStart()
                < entry.getHighlights().get(1).getStart());
    }

    @Test
    public void invalidHighlight_isOmittedWithItsAssociatedNote() {
        addHighlight("564", new Highlight(
                "stale",
                -1,
                -1,
                "yellow",
                "No debe exportarse sin contexto",
                "fragmento que ya no existe",
                1L
        ));

        assertTrue(aggregator.build().isEmpty());
    }

    @Test
    public void whitespaceQuickNote_doesNotCountAsContent() {
        addHighlight("564", validHighlight(
                "564",
                ArticleQuickNotes.idFor("564"),
                "   ",
                firstLineLength(location("564").article.text)
        ));

        assertTrue(aggregator.build().isEmpty());
    }

    @Test
    public void staleQuickNoteRange_stillUsesItsExplicitArticleIdentity() {
        addHighlight("564", new Highlight(
                ArticleQuickNotes.idFor("564"),
                -1,
                -1,
                "yellow",
                "Nota que debe conservarse",
                "encabezado legal anterior",
                1L
        ));

        ArticleNotesEntry entry = onlyEntry();

        assertTrue(entry.getHighlights().isEmpty());
        assertEquals("Nota que debe conservarse", entry.getArticleNotes().get(0).getText());
    }

    @Test
    public void unresolvedArticleFavorite_isSafeAndDoesNotInventTitle() {
        favorites.add(favorite("9999"));

        ArticleNotesEntry entry = onlyEntry();

        assertEquals("9999", entry.getArticleNumber());
        assertEquals("", entry.getArticleTitle());
    }

    private ArticleNotesEntry onlyEntry() {
        List<ArticleNotesEntry> result = aggregator.build();
        assertEquals(1, result.size());
        return result.get(0);
    }

    private FavoriteItem favorite(String number) {
        String destination = FavoriteDestinationMapper.destinationForArticle(number);
        return new FavoriteItem(destination, "Título guardado", "", "Artículo", destination);
    }

    private void addHighlight(String articleNumber, Highlight highlight) {
        String blockKey = location(articleNumber).block.key;
        highlights.computeIfAbsent(blockKey, ignored -> new ArrayList<>()).add(highlight);
    }

    private Highlight validHighlight(
            String articleNumber,
            String id,
            String note,
            int length
    ) {
        return validHighlightAt(articleNumber, id, note, 0, length);
    }

    private Highlight validHighlightAt(
            String articleNumber,
            String id,
            String note,
            int relativeStart,
            int length
    ) {
        ArticleRepository.Location location = location(articleNumber);
        Article article = location.article;
        int relativeEnd = Math.min(article.text.length(), relativeStart + length);
        int start = article.offsetInBlock + relativeStart;
        int end = article.offsetInBlock + relativeEnd;
        String snippet = location.block.fullText().substring(start, end);
        return new Highlight(id, start, end, "yellow", note, snippet, start);
    }

    private ArticleRepository.Location location(String number) {
        List<ArticleRepository.Location> locations = ArticleRepository.findArticle(context, number);
        assertEquals(1, locations.size());
        return locations.get(0);
    }

    private int firstLineLength(String value) {
        int newline = value.indexOf('\n');
        return newline < 0 ? value.length() : newline;
    }
}
