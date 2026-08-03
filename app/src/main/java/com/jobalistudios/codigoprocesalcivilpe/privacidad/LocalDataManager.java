package com.jobalistudios.codigoprocesalcivilpe.privacidad;

import android.content.Context;

import com.jobalistudios.codigoprocesalcivilpe.busqueda.BusquedaViewModel;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightsManager;

/** Control centralizado de los datos que la aplicación guarda localmente. */
public final class LocalDataManager {

    private static final String APP_SETTINGS = "app_settings";

    private final Context appContext;

    public LocalDataManager(Context context) {
        appContext = context.getApplicationContext();
    }

    public void clearRecentSearches() {
        BusquedaViewModel.clearRecentQueries(appContext);
    }

    public void clearFavorites() {
        new FavoritesManager(appContext).clearAll();
    }

    public void clearHighlightsAndNotes() {
        new HighlightsManager(appContext).clearAll();
    }

    /**
     * Elimina datos creados por el usuario y preferencias funcionales de esta app.
     * No altera directamente el almacenamiento interno de los SDK de Google.
     */
    public void clearAllLocalData() {
        clearRecentSearches();
        clearFavorites();
        clearHighlightsAndNotes();
        appContext.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
