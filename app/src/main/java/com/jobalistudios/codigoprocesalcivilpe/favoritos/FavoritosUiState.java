package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/** Estado completo y no persistente de la pantalla Favoritos. */
public final class FavoritosUiState {

    public enum EmptyState {
        NONE,
        NO_FAVORITES,
        NO_NOTES,
        NO_HIGHLIGHTS,
        NO_QUERY_RESULTS
    }

    @NonNull public final List<FavoriteListItem> visibleItems;
    @NonNull public final String query;
    @NonNull public final FavoritosViewModel.FilterMode filterMode;
    @NonNull public final FavoritosViewModel.SortMode sortMode;
    @NonNull public final EmptyState emptyState;
    public final int totalFavorites;

    public FavoritosUiState(
            @NonNull List<FavoriteListItem> visibleItems,
            @NonNull String query,
            @NonNull FavoritosViewModel.FilterMode filterMode,
            @NonNull FavoritosViewModel.SortMode sortMode,
            @NonNull EmptyState emptyState,
            int totalFavorites
    ) {
        this.visibleItems = Collections.unmodifiableList(visibleItems);
        this.query = query;
        this.filterMode = filterMode;
        this.sortMode = sortMode;
        this.emptyState = emptyState;
        this.totalFavorites = totalFavorites;
    }
}
