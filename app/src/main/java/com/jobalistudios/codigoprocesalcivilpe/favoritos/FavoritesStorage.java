package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesStorage {

    private static final String PREFS_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorites_items";

    private final SharedPreferences preferences;

    public FavoritesStorage(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFavorite(String activityClassName) {
        for (FavoriteItem item : getFavorites()) {
            if (item.getActivityClassName().equals(activityClassName)) {
                return true;
            }
        }
        return false;
    }

    public void toggle(FavoriteItem favoriteItem) {
        List<FavoriteItem> items = getFavorites();
        boolean removed = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getActivityClassName().equals(favoriteItem.getActivityClassName())) {
                items.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            items.add(favoriteItem);
        }

        saveFavorites(items);
    }

    public List<FavoriteItem> getFavorites() {
        Set<String> rawItems = preferences.getStringSet(KEY_FAVORITES, new HashSet<>());
        List<FavoriteItem> favoriteItems = new ArrayList<>();

        for (String rawItem : rawItems) {
            FavoriteItem item = FavoriteItem.fromJson(rawItem);
            if (item != null && !item.getActivityClassName().isEmpty()) {
                favoriteItems.add(item);
            }
        }

        favoriteItems.sort((item1, item2) -> item1.getTitle().compareToIgnoreCase(item2.getTitle()));
        return favoriteItems;
    }

    private void saveFavorites(List<FavoriteItem> favorites) {
        Set<String> serializedItems = new HashSet<>();
        for (FavoriteItem item : favorites) {
            serializedItems.add(item.toJson());
        }
        preferences.edit().putStringSet(KEY_FAVORITES, serializedItems).apply();
    }
}
