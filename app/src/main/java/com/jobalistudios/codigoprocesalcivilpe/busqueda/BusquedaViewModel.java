package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Coordina el estado del buscador; el índice y ranking viven en {@link LegalSearchEngine}. */
public class BusquedaViewModel extends AndroidViewModel {
    static final String PREFS_NAME = "busqueda_prefs";
    static final String PREF_RECENT = "recent_queries";
    static final int MAX_RECENT = 8;

    private final SharedPreferences sharedPreferences;
    private final LegalSearchEngine searchEngine;
    private final FavoritesManager favoritesManager;
    private final Set<String> selectedSections = new HashSet<>();
    private final MutableLiveData<BusquedaUiState> uiState = new MutableLiveData<>();

    private String currentQuery = "";
    private SearchFilter currentFilter = SearchFilter.ALL;

    public BusquedaViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        searchEngine = LegalSearchEngine.create(application);
        favoritesManager = new FavoritesManager(application);
        refreshState();
    }

    public LiveData<BusquedaUiState> getUiState() {
        return uiState;
    }

    public void updateQuery(String query) {
        // Conserva los espacios mientras el usuario escribe consultas de varias palabras.
        // El motor y la persistencia ya recortan la entrada en sus respectivos límites.
        currentQuery = query == null ? "" : query;
        refreshState();
    }

    public void setFilter(@NonNull SearchFilter filter) {
        currentFilter = filter;
        refreshState();
    }

    public void toggleSection(String sectionName) {
        if (selectedSections.contains(sectionName)) {
            selectedSections.remove(sectionName);
        } else {
            selectedSections.add(sectionName);
        }
        refreshState();
    }

    public void clearFilters() {
        currentFilter = SearchFilter.ALL;
        selectedSections.clear();
        refreshState();
    }

    public void submitCurrentQuery() {
        persistRecentQuery(currentQuery);
        refreshState();
    }

    public void recordResultOpened() {
        persistRecentQuery(currentQuery);
        refreshState();
    }

    public void useRecentQuery(String query) {
        currentQuery = query == null ? "" : query.trim();
        refreshState();
    }

    public void removeRecentQuery(String query) {
        List<String> recents = getRecentQueries();
        String normalized = SearchTextNormalizer.normalizePlain(query);
        recents.removeIf(item -> SearchTextNormalizer.normalizePlain(item).equals(normalized));
        saveRecentQueries(recents);
        refreshState();
    }

    public void clearAllRecentQueries() {
        clearRecentQueries(getApplication());
        refreshState();
    }

    /** Refresca el set de favoritos después de volver desde la pantalla de lectura. */
    public void refreshFavorites() {
        refreshState();
    }

    /** Elimina únicamente el historial de búsquedas recientes, también desde Privacidad. */
    public static void clearRecentQueries(Context context) {
        context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(PREF_RECENT)
                .apply();
    }

    @Nullable
    static String extractArticleNumber(String query) {
        return ArticleNumberQueryParser.extract(query);
    }

    private void refreshState() {
        Set<String> favoriteNumbers = favoriteArticleNumbers();
        LegalSearchEngine.SearchResponse response = searchEngine.search(
                currentQuery,
                currentFilter,
                selectedSections,
                favoriteNumbers
        );
        EmptyState emptyState = resolveEmptyState(response, favoriteNumbers);
        uiState.setValue(new BusquedaUiState(
                currentQuery,
                response.results,
                getRecentQueries(),
                currentFilter,
                selectedSections,
                emptyState
        ));
    }

    private EmptyState resolveEmptyState(
            LegalSearchEngine.SearchResponse response,
            Set<String> favoriteNumbers
    ) {
        if (!response.results.isEmpty()) {
            return EmptyState.NONE;
        }
        if (response.invalidNumberQuery) {
            return EmptyState.INVALID_NUMBER;
        }
        if (currentFilter == SearchFilter.FAVORITES && currentQuery.trim().isEmpty()) {
            if (favoriteNumbers.isEmpty()) {
                return EmptyState.NO_FAVORITES;
            }
            return EmptyState.FILTERED_NO_RESULTS;
        }
        if (currentQuery.trim().isEmpty()) {
            return EmptyState.PROMPT;
        }
        if (!selectedSections.isEmpty() || currentFilter != SearchFilter.ALL) {
            return EmptyState.FILTERED_NO_RESULTS;
        }
        return EmptyState.NO_RESULTS;
    }

    private Set<String> favoriteArticleNumbers() {
        Set<String> result = new HashSet<>();
        for (FavoriteItem favorite : favoritesManager.getAll()) {
            String destination = favorite.getDestinationId();
            if (destination.startsWith(FavoriteDestinationMapper.ARTICLE_PREFIX)) {
                result.add(destination.substring(FavoriteDestinationMapper.ARTICLE_PREFIX.length()));
            }
        }
        return result;
    }

    private List<String> getRecentQueries() {
        String value = sharedPreferences.getString(PREF_RECENT, "");
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split("\\n")));
    }

    private void persistRecentQuery(String query) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String normalized = SearchTextNormalizer.normalizePlain(trimmed);
        List<String> queries = getRecentQueries();
        queries.removeIf(item -> SearchTextNormalizer.normalizePlain(item).equals(normalized));
        queries.add(0, trimmed);
        if (queries.size() > MAX_RECENT) {
            queries = new ArrayList<>(queries.subList(0, MAX_RECENT));
        }
        saveRecentQueries(queries);
    }

    private void saveRecentQueries(List<String> queries) {
        if (queries.isEmpty()) {
            sharedPreferences.edit().remove(PREF_RECENT).apply();
        } else {
            sharedPreferences.edit()
                    .putString(PREF_RECENT, TextUtils.join("\n", queries))
                    .apply();
        }
    }

    public enum EmptyState {
        NONE,
        PROMPT,
        NO_RESULTS,
        INVALID_NUMBER,
        NO_FAVORITES,
        FILTERED_NO_RESULTS
    }

    public static final class BusquedaUiState {
        @NonNull public final String query;
        @NonNull public final List<ArticleSearchResult> results;
        @NonNull public final List<String> recentQueries;
        @NonNull public final SearchFilter filter;
        @NonNull public final Set<String> selectedSections;
        @NonNull public final EmptyState emptyState;

        BusquedaUiState(
                @NonNull String query,
                @NonNull List<ArticleSearchResult> results,
                @NonNull List<String> recentQueries,
                @NonNull SearchFilter filter,
                @NonNull Set<String> selectedSections,
                @NonNull EmptyState emptyState
        ) {
            this.query = query;
            this.results = results;
            this.recentQueries = Collections.unmodifiableList(new ArrayList<>(recentQueries));
            this.filter = filter;
            this.selectedSections = Collections.unmodifiableSet(new HashSet<>(selectedSections));
            this.emptyState = emptyState;
        }

        public boolean hasActiveFilters() {
            return filter != SearchFilter.ALL || !selectedSections.isEmpty();
        }
    }
}
