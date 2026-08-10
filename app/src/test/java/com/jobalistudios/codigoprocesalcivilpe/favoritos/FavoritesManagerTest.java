package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.atomic.AtomicLong;

@RunWith(RobolectricTestRunner.class)
public class FavoritesManagerTest {

    private static final String PREFS_NAME = "codigoprocesalcivil_favorites";

    private Context context;
    private FavoritesManager favoritesManager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit();
        favoritesManager = new FavoritesManager(context);
    }

    @Test
    public void add_persistsFavoriteAndPreventsDuplicates() {
        FavoriteItem item = createFavorite("item-1");

        favoritesManager.add(item);
        favoritesManager.add(item);

        assertTrue(favoritesManager.isFavorite("item-1"));
        assertEquals(1, favoritesManager.getAll().size());
    }

    @Test
    public void remove_deletesFavoriteById() {
        favoritesManager.add(createFavorite("item-1"));
        favoritesManager.add(createFavorite("item-2"));

        favoritesManager.remove("item-1");

        assertFalse(favoritesManager.isFavorite("item-1"));
        assertTrue(favoritesManager.isFavorite("item-2"));
        assertEquals(1, favoritesManager.getAll().size());
    }

    @Test
    public void toggle_addsThenRemovesFavorite() {
        FavoriteItem item = createFavorite("toggle-id");

        favoritesManager.toggle(item);
        assertTrue(favoritesManager.isFavorite("toggle-id"));

        favoritesManager.toggle(item);
        assertFalse(favoritesManager.isFavorite("toggle-id"));
        assertEquals(0, favoritesManager.getAll().size());
    }

    @Test
    public void newArticleFavorite_storesAddedAt() {
        FavoritesManager manager = new FavoritesManager(context, () -> 1_234L);

        manager.add(createArticleFavorite("564"));

        assertEquals(1_234L, manager.getAll().get(0).getAddedAt());
    }

    @Test
    public void jsonRoundTrip_preservesAddedAt() throws JSONException {
        FavoriteItem original = new FavoriteItem(
                "article:564", "Artículo 564", "Acceso de oficio", "Artículo",
                "article:564", 8_765L);

        FavoriteItem restored = FavoriteItem.fromJson(original.toJson());

        assertEquals(8_765L, restored.getAddedAt());
    }

    @Test
    public void oldJsonWithoutAddedAt_remainsValid() throws JSONException {
        JSONObject legacyJson = new JSONObject()
                .put("id", "article:564")
                .put("title", "Artículo 564")
                .put("subtitle", "Acceso de oficio")
                .put("type", "Artículo")
                .put("destination_id", "article:564");

        FavoriteItem restored = FavoriteItem.fromJson(legacyJson);

        assertEquals("article:564", restored.getDestinationId());
        assertEquals(0L, restored.getAddedAt());
    }

    @Test
    public void legacyActivityJsonWithoutAddedAt_doesNotThrow() throws JSONException {
        JSONObject legacyJson = new JSONObject()
                .put("id", "legacy")
                .put("activity", "com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit1");

        FavoriteItem restored = FavoriteItem.fromJson(legacyJson);

        assertEquals(FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_1,
                restored.getDestinationId());
        assertEquals(0L, restored.getAddedAt());
    }

    @Test
    public void removingAndAddingArticleAgain_assignsNewAddedAt() {
        AtomicLong clock = new AtomicLong(100L);
        FavoritesManager manager = new FavoritesManager(context, () -> clock.getAndAdd(100L));
        FavoriteItem article = createArticleFavorite("564");

        manager.add(article);
        assertEquals(100L, manager.getAll().get(0).getAddedAt());
        manager.remove(article.getId());
        manager.add(article);

        assertEquals(200L, manager.getAll().get(0).getAddedAt());
    }

    private FavoriteItem createFavorite(String id) {
        return new FavoriteItem(id, "Título", "Subtítulo", "articulo", "seccion_primera_titulo_1");
    }

    private FavoriteItem createArticleFavorite(String number) {
        String destination = FavoriteDestinationMapper.destinationForArticle(number);
        return new FavoriteItem(destination, "Artículo " + number, "", "Artículo", destination);
    }
}
