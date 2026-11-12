package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Coordina la búsqueda en el hilo principal, pero carga el índice y consulta los
 * artículos exclusivamente en un ejecutor de fondo. No altera el ranking ni los filtros.
 */
public class BusquedaViewModel extends AndroidViewModel {
    private static final String TAG = "BusquedaViewModel";
    static final String PREFS_NAME = "busqueda_prefs";
    static final String PREF_RECENT = "recent_queries";
    static final int MAX_RECENT = 8;
    static final long SEARCH_DEBOUNCE_MS = 275L;

    private final SharedPreferences sharedPreferences;
    private final FavoritesManager favoritesManager;
    private final Set<String> selectedSections = new HashSet<>();
    private final MutableLiveData<BusquedaUiState> uiState = new MutableLiveData<>();
    private final Executor backgroundExecutor;
    @Nullable private final ExecutorService ownedExecutor;
    private final Handler mainHandler;

    // El motor se inicializa una sola vez, en el ejecutor serial de búsqueda.
    @Nullable private LegalSearchEngine searchEngine;
    @Nullable private Runnable pendingSearch;
    private volatile int requestGeneration;
    private volatile boolean disposed;

    private String currentQuery = "";
    private SearchFilter currentFilter = SearchFilter.ALL;

    public BusquedaViewModel(@NonNull Application application) {
        this(application, Executors.newSingleThreadExecutor(),
                new Handler(Looper.getMainLooper()), true);
    }

    /** Permite probar debounce y solicitudes obsoletas sin depender de hilos reales. */
    BusquedaViewModel(@NonNull Application application, @NonNull Executor executor,
                      @NonNull Handler handler) {
        this(application, executor, handler, false);
    }

    private BusquedaViewModel(@NonNull Application application, @NonNull Executor executor,
                             @NonNull Handler handler, boolean ownsExecutor) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        favoritesManager = new FavoritesManager(application);
        backgroundExecutor = executor;
        ownedExecutor = ownsExecutor ? (ExecutorService) executor : null;
        mainHandler = handler;

        // El estado inicial es visible al instante, incluso mientras se prepara el índice.
        uiState.setValue(new BusquedaUiState(currentQuery, Collections.emptyList(),
                getRecentQueries(), currentFilter, selectedSections, EmptyState.PROMPT, false));
        backgroundExecutor.execute(() -> {
            if (!disposed) {
                try {
                    getOrCreateEngine();
                } catch (RuntimeException error) {
                    // Se informa al solicitar una búsqueda; el inicio nunca se bloquea.
                    Log.e(TAG, "No se pudo precalcular el índice jurídico", error);
                }
            }
        });
    }

    public LiveData<BusquedaUiState> getUiState() {
        return uiState;
    }

    public void updateQuery(String query) {
        // Conserva los espacios mientras se escriben consultas de varias palabras.
        String nextQuery = query == null ? "" : query;
        if (currentQuery.equals(nextQuery)) {
            return;
        }
        currentQuery = nextQuery;
        scheduleSearch(!currentQuery.trim().isEmpty());
    }

    public void setFilter(@NonNull SearchFilter filter) {
        if (currentFilter == filter) {
            return;
        }
        currentFilter = filter;
        scheduleSearch(false);
    }

    public void toggleSection(String sectionName) {
        if (selectedSections.contains(sectionName)) {
            selectedSections.remove(sectionName);
        } else {
            selectedSections.add(sectionName);
        }
        scheduleSearch(false);
    }

    public void clearFilters() {
        currentFilter = SearchFilter.ALL;
        selectedSections.clear();
        scheduleSearch(false);
    }

    public void submitCurrentQuery() {
        persistRecentQuery(currentQuery);
        // Enter ejecuta inmediatamente, incluso si había un debounce pendiente.
        scheduleSearch(false);
    }

    public void recordResultOpened() {
        persistRecentQuery(currentQuery);
        refreshRecentQueries();
    }

    public void useRecentQuery(String query) {
        currentQuery = query == null ? "" : query.trim();
        scheduleSearch(false);
    }

    public void removeRecentQuery(String query) {
        List<String> recents = getRecentQueries();
        String normalized = SearchTextNormalizer.normalizePlain(query);
        recents.removeIf(item -> SearchTextNormalizer.normalizePlain(item).equals(normalized));
        saveRecentQueries(recents);
        refreshRecentQueries();
    }

    public void clearAllRecentQueries() {
        clearRecentQueries(getApplication());
        refreshRecentQueries();
    }

    /** Refresca el set de favoritos después de volver desde la pantalla de lectura. */
    public void refreshFavorites() {
        scheduleSearch(false);
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

    private void refreshRecentQueries() {
        BusquedaUiState previous = uiState.getValue();
        if (previous != null) {
            uiState.setValue(new BusquedaUiState(previous.query, previous.results,
                    getRecentQueries(), previous.filter, previous.selectedSections,
                    previous.emptyState, previous.isLoading));
        }
    }

    private void scheduleSearch(boolean debounce) {
        if (disposed) {
            return;
        }
        if (pendingSearch != null) {
            mainHandler.removeCallbacks(pendingSearch);
            pendingSearch = null;
        }

        int generation = ++requestGeneration;
        String query = currentQuery;
        SearchFilter filter = currentFilter;
        Set<String> sections = new HashSet<>(selectedSections);

        // Con la consulta vacía mantenemos el estado inicial sin esperar al índice.
        if (query.trim().isEmpty() && filter != SearchFilter.FAVORITES) {
            uiState.setValue(new BusquedaUiState(query, Collections.emptyList(),
                    getRecentQueries(), filter, sections, EmptyState.PROMPT, false));
            return;
        }

        // Limpia los resultados anteriores; nunca muestra resultados de otro filtro
        // ni un estado vacío falso mientras se está calculando.
        uiState.setValue(new BusquedaUiState(query, Collections.emptyList(),
                getRecentQueries(), filter, sections, EmptyState.NONE, true));

        Runnable dispatch = () -> backgroundExecutor.execute(() -> {
            if (disposed || generation != requestGeneration) {
                return;
            }
            try {
                LegalSearchEngine engine = getOrCreateEngine();
                if (disposed || generation != requestGeneration) {
                    return;
                }
                Set<String> favoriteNumbers = favoriteArticleNumbers();
                LegalSearchEngine.SearchResponse response = engine.search(
                        query, filter, sections, favoriteNumbers);
                EmptyState emptyState = resolveEmptyState(
                        query, filter, sections, response, favoriteNumbers);

                mainHandler.post(() -> {
                    if (disposed || generation != requestGeneration) {
                        return;
                    }
                    uiState.setValue(new BusquedaUiState(query, response.results,
                            getRecentQueries(), filter, sections, emptyState, false));
                });
            } catch (RuntimeException error) {
                Log.e(TAG, "No se pudo completar la búsqueda jurídica", error);
                mainHandler.post(() -> {
                    if (disposed || generation != requestGeneration) {
                        return;
                    }
                    uiState.setValue(new BusquedaUiState(query, Collections.emptyList(),
                            getRecentQueries(), filter, sections, EmptyState.SEARCH_ERROR, false));
                });
            }
        });
        pendingSearch = dispatch;
        if (debounce) {
            mainHandler.postDelayed(dispatch, SEARCH_DEBOUNCE_MS);
        } else {
            dispatch.run();
        }
    }

    private LegalSearchEngine getOrCreateEngine() {
        if (searchEngine == null) {
            searchEngine = LegalSearchEngine.create(getApplication());
        }
        return searchEngine;
    }

    private static EmptyState resolveEmptyState(
            String query,
            SearchFilter filter,
            Set<String> sections,
            LegalSearchEngine.SearchResponse response,
            Set<String> favoriteNumbers
    ) {
        if (!response.results.isEmpty()) {
            return EmptyState.NONE;
        }
        if (response.invalidNumberQuery) {
            return EmptyState.INVALID_NUMBER;
        }
        if (filter == SearchFilter.FAVORITES && query.trim().isEmpty()) {
            if (favoriteNumbers.isEmpty()) {
                return EmptyState.NO_FAVORITES;
            }
            return EmptyState.FILTERED_NO_RESULTS;
        }
        if (query.trim().isEmpty()) {
            return EmptyState.PROMPT;
        }
        if (!sections.isEmpty() || filter != SearchFilter.ALL) {
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
        return new ArrayList<>(Arrays.asList(value.split("\n")));
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

    @Override
    protected void onCleared() {
        disposed = true;
        ++requestGeneration;
        if (pendingSearch != null) {
            mainHandler.removeCallbacks(pendingSearch);
            pendingSearch = null;
        }
        if (ownedExecutor != null) {
            ownedExecutor.shutdownNow();
        }
        super.onCleared();
    }

    public enum EmptyState {
        NONE,
        PROMPT,
        NO_RESULTS,
        INVALID_NUMBER,
        NO_FAVORITES,
        FILTERED_NO_RESULTS,
        SEARCH_ERROR
    }

    public static final class BusquedaUiState {
        @NonNull public final String query;
        @NonNull public final List<ArticleSearchResult> results;
        @NonNull public final List<String> recentQueries;
        @NonNull public final SearchFilter filter;
        @NonNull public final Set<String> selectedSections;
        @NonNull public final EmptyState emptyState;
        public final boolean isLoading;

        BusquedaUiState(
                @NonNull String query,
                @NonNull List<ArticleSearchResult> results,
                @NonNull List<String> recentQueries,
                @NonNull SearchFilter filter,
                @NonNull Set<String> selectedSections,
                @NonNull EmptyState emptyState,
                boolean isLoading
        ) {
            this.query = query;
            this.results = Collections.unmodifiableList(new ArrayList<>(results));
            this.recentQueries = Collections.unmodifiableList(new ArrayList<>(recentQueries));
            this.filter = filter;
            this.selectedSections = Collections.unmodifiableSet(new HashSet<>(selectedSections));
            this.emptyState = emptyState;
            this.isLoading = isLoading;
        }

        public boolean hasActiveFilters() {
            return filter != SearchFilter.ALL || !selectedSections.isEmpty();
        }
    }
}
