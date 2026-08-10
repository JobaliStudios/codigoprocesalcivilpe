package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

public class FavoritesManager {
    private static final String PREFS_NAME = "codigoprocesalcivil_favorites";
    private static final String KEY_ITEMS = "items";

    private final Context appContext;
    private final SharedPreferences sharedPreferences;
    private final LongSupplier clock;

    public FavoritesManager(Context context) {
        this(context, System::currentTimeMillis);
    }

    FavoritesManager(Context context, LongSupplier clock) {
        appContext = context.getApplicationContext();
        sharedPreferences = appContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.clock = clock;
    }

    public List<FavoriteItem> getAll() {
        List<FavoriteItem> result = new ArrayList<>();
        JSONArray array = getArray();
        for (int i = 0; i < array.length(); i++) {
            result.add(FavoriteItem.fromJson(array.optJSONObject(i)));
        }
        return result;
    }

    public boolean isFavorite(String id) {
        JSONArray array = getArray();
        for (int i = 0; i < array.length(); i++) {
            if (id.equals(array.optJSONObject(i).optString("id"))) {
                return true;
            }
        }
        return false;
    }

    public void toggle(FavoriteItem item) {
        if (isFavorite(item.getId())) {
            remove(item.getId());
        } else {
            add(item);
        }
    }

    public void add(FavoriteItem item) {
        JSONArray array = getArray();
        if (isFavorite(item.getId())) {
            return;
        }
        try {
            FavoriteItem storedItem = item.getDestinationId()
                    .startsWith(FavoriteDestinationMapper.ARTICLE_PREFIX)
                    ? item.withAddedAt(clock.getAsLong())
                    : item;
            array.put(storedItem.toJson());
            saveArray(array);
        } catch (JSONException ignored) {
        }
    }

    public boolean removeInvalidFavorites() {
        JSONArray array = getArray();
        JSONArray updated = new JSONArray();
        boolean removedAny = false;

        for (int i = 0; i < array.length(); i++) {
            FavoriteItem item = FavoriteItem.fromJson(array.optJSONObject(i));
            Intent intent = FavoriteDestinationMapper.toIntent(appContext, item.getDestinationId());
            if (intent != null) {
                updated.put(array.optJSONObject(i));
            } else {
                removedAny = true;
            }
        }

        if (removedAny) {
            saveArray(updated);
        }
        return removedAny;
    }

    public void remove(String id) {
        JSONArray array = getArray();
        JSONArray updated = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            if (!id.equals(array.optJSONObject(i).optString("id"))) {
                updated.put(array.optJSONObject(i));
            }
        }
        saveArray(updated);
    }

    /** Restaura el registro eliminado sin convertirlo en un favorito recién creado. */
    public void restore(FavoriteItem item) {
        JSONArray array = getArray();
        if (isFavorite(item.getId())) {
            return;
        }
        try {
            array.put(item.toJson());
            saveArray(array);
        } catch (JSONException ignored) {
        }
    }

    /** Elimina todos los favoritos guardados por el usuario. */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    private JSONArray getArray() {
        String raw = sharedPreferences.getString(KEY_ITEMS, "[]");
        try {
            return new JSONArray(raw);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    private void saveArray(JSONArray array) {
        sharedPreferences.edit().putString(KEY_ITEMS, array.toString()).apply();
    }
}
