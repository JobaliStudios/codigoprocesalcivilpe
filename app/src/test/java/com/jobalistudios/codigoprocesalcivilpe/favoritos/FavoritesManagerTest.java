package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FavoritesManagerTest {

    private static final String PREFS_NAME = "codigoprocesalcivil_favorites";

    private FavoritesManager favoritesManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
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

    private FavoriteItem createFavorite(String id) {
        return new FavoriteItem(id, "Título", "Subtítulo", "articulo", "seccion_primera_titulo_1");
    }
}
