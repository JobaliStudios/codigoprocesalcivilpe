package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ArticleFavoritesTest {
    private static final String PREFS_NAME = "codigoprocesalcivil_favorites";

    private Context context;
    private FavoritesManager manager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        manager = new FavoritesManager(context);
    }

    @Test
    public void articleFavorite_hasStableFieldsAndTogglesWithoutDuplicates() {
        Article article = article("564");
        FavoriteItem item = ArticleFavorites.itemForArticle(article);

        assertEquals("article:564", item.getId());
        assertEquals("Artículo 564", item.getTitle());
        assertEquals(article.title, item.getSubtitle());
        assertEquals("Artículo", item.getType());
        assertEquals("article:564", item.getDestinationId());

        manager.toggle(item);
        manager.add(item);
        assertTrue(manager.isFavorite("article:564"));
        assertEquals(1, manager.getAll().size());

        manager.toggle(item);
        assertFalse(manager.isFavorite("article:564"));
    }

    @Test
    public void articleDestination_opensExactArticleOffset() {
        Article article = article("564");

        Intent intent = FavoriteDestinationMapper.toIntent(context, "article:564");

        assertNotNull(intent);
        assertEquals(SectionContentActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(article.offsetInBlock, intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
    }

    @Test
    public void alphanumericArticleDestination_isSupported() {
        Article article = article("506-A");

        Intent intent = FavoriteDestinationMapper.toIntent(context, "article:506-A");

        assertNotNull(intent);
        assertEquals(article.offsetInBlock, intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
    }

    @Test
    public void nodeAndLegacyDestinations_remainSupported() {
        assertNotNull(FavoriteDestinationMapper.toIntent(context, "node:sec_1_tit_1"));
        assertNotNull(FavoriteDestinationMapper.toIntent(
                context,
                "com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit1"
        ));
    }

    @Test
    public void articleFavorite_doesNotModifyNodeFavorite() {
        FavoriteItem node = new FavoriteItem(
                "node:sec_1_tit_1", "Título I", "Jurisdicción y acción",
                "Título", "node:sec_1_tit_1");
        manager.add(node);

        FavoriteItem article = ArticleFavorites.itemForArticle(article("564"));
        manager.toggle(article);
        manager.toggle(article);

        assertTrue(manager.isFavorite(node.getId()));
        assertFalse(manager.isFavorite(article.getId()));
        assertEquals(1, manager.getAll().size());
    }

    private Article article(String number) {
        return ArticleRepository.findArticle(context, number).get(0).article;
    }
}
