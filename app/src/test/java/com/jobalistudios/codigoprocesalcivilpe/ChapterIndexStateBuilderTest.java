package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.ArticleQuickNotes;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ChapterIndexStateBuilderTest {
    private Context context;
    private ArticleBlock block;
    private String current;
    private ChapterIndexStateBuilder builder;

    @Before public void before() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("codigoprocesalcivil_favorites", Context.MODE_PRIVATE)
                .edit().clear().commit();
        context.getSharedPreferences("codigoprocesalcivil_highlights", Context.MODE_PRIVATE)
                .edit().clear().commit();
        block = ArticleRepository.findArticle(context, "564").get(0).block;
        current = block.articles.get(0).number;
        builder = new ChapterIndexStateBuilder(context);
    }

    @After public void after() {
        context.getSharedPreferences("codigoprocesalcivil_favorites", Context.MODE_PRIVATE)
                .edit().clear().commit();
        context.getSharedPreferences("codigoprocesalcivil_highlights", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @Test public void allIncludesEveryArticleInLegalOrderAndMarksCurrent() {
        List<ChapterIndexStateBuilder.Entry> rows = builder.build(
                block.key, current, "", ChapterIndexStateBuilder.Filter.ALL);
        assertEquals(block.articles.size(), rows.size());
        assertEquals(current, rows.get(0).number);
        assertEquals(1, rows.get(0).position);
        assertTrue(rows.get(0).current);
        assertFalse(rows.get(1).current);
    }

    @Test public void searchByNumberAndWithoutAccentsUsesExistingNormalizer() {
        List<ChapterIndexStateBuilder.Entry> result = builder.build(
                block.key, current, current, ChapterIndexStateBuilder.Filter.ALL);
        assertTrue(result.stream().anyMatch(row -> row.number.equals(current)));
        String title = block.articles.get(0).title;
        if (!title.isEmpty()) {
            String normalized = com.jobalistudios.codigoprocesalcivilpe.busqueda
                    .SearchTextNormalizer.normalizePlain(title);
            assertTrue(builder.build(block.key,current,normalized,
                    ChapterIndexStateBuilder.Filter.ALL).stream()
                    .anyMatch(row -> row.number.equals(current)));
        }
    }

    @Test public void favoritesFilterShowsOnlyPersistedArticles() {
        FavoritesManager favorites = new FavoritesManager(context);
        favorites.add(ArticleFavorites.itemForArticle(block.articles.get(0)));
        List<ChapterIndexStateBuilder.Entry> rows = builder.build(
                block.key, current, "", ChapterIndexStateBuilder.Filter.FAVORITES);
        assertEquals(1, rows.size());
        assertEquals(current, rows.get(0).number);
        assertTrue(rows.get(0).favorite);
    }

    @Test public void noteAndHighlightIndicatorsFollowStoredRanges() {
        // El formato antiguo con notas en claro se interpreta sin perder datos; la
        // migración cifrada es responsabilidad de HighlightsManager.
        String json = "[{\"id\":\"article-quick-note:" + current
                + "\",\"start\":0,\"end\":1,\"colorTag\":\"yellow\","
                + "\"snippet\":\"X\",\"note\":\"Apunte\",\"createdAt\":1}]";
        context.getSharedPreferences("codigoprocesalcivil_highlights", Context.MODE_PRIVATE)
                .edit().putString(block.key, json).commit();
        List<ChapterIndexStateBuilder.Entry> notes = builder.build(block.key, current,
                "", ChapterIndexStateBuilder.Filter.NOTES);
        assertEquals(1, notes.size());
        assertTrue(notes.get(0).note);
        assertFalse(notes.get(0).highlight);
        assertEquals(0, builder.build(block.key,current,"",
                ChapterIndexStateBuilder.Filter.HIGHLIGHTS).size());
    }

    @Test public void noMatchesOrInvalidBlockReturnEmptyList() {
        assertTrue(builder.build(block.key, current,
                "frase imposible de encontrar 434343434343",
                ChapterIndexStateBuilder.Filter.ALL).isEmpty());
        assertTrue(builder.build("clave_que_no_existe", current,
                "", ChapterIndexStateBuilder.Filter.ALL).isEmpty());
    }
}
