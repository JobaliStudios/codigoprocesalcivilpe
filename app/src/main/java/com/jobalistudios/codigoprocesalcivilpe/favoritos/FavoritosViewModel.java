package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Fuente única para búsqueda, filtro, orden y presentación de Favoritos. */
public class FavoritosViewModel extends AndroidViewModel {

    public enum FilterMode {
        ALL,
        WITH_NOTES,
        WITH_HIGHLIGHTS
    }

    public enum SortMode {
        RECENT,
        ARTICLE_NUMBER
    }

    private final ArticleUserContentResolver userContentResolver;
    private final MutableLiveData<FavoritosUiState> uiState = new MutableLiveData<>();
    private List<FavoriteItem> sourceFavorites = new ArrayList<>();
    private List<FavoriteListItem> preparedFavorites = new ArrayList<>();
    private String query = "";
    private FilterMode filterMode = FilterMode.ALL;
    private SortMode sortMode = SortMode.RECENT;

    public FavoritosViewModel(@NonNull Application application) {
        this(application, new ArticleUserContentResolver(application));
    }

    FavoritosViewModel(
            @NonNull Application application,
            @NonNull ArticleUserContentResolver userContentResolver
    ) {
        super(application);
        this.userContentResolver = userContentResolver;
        refreshState();
    }

    @NonNull
    public LiveData<FavoritosUiState> getUiState() {
        return uiState;
    }

    /** Refresca favoritos y metadatos personales; conserva query, filtro y orden actuales. */
    public void setFavorites(@NonNull List<FavoriteItem> favorites) {
        sourceFavorites = new ArrayList<>(favorites);
        preparedFavorites = prepareFavorites(sourceFavorites);
        refreshState();
    }

    public void setQuery(String value) {
        query = value == null ? "" : value;
        refreshState();
    }

    public void setFilterMode(FilterMode mode) {
        filterMode = mode == null ? FilterMode.ALL : mode;
        refreshState();
    }

    public void setSortMode(SortMode mode) {
        sortMode = mode == null ? SortMode.RECENT : mode;
        refreshState();
    }

    @NonNull
    private List<FavoriteListItem> prepareFavorites(List<FavoriteItem> favorites) {
        Map<String, ArticleReference> articleIndex = buildArticleIndex();
        ArticleUserContentResolver.Session contentSession = userContentResolver.newSession();
        List<FavoriteListItem> result = new ArrayList<>();
        for (int position = 0; position < favorites.size(); position++) {
            FavoriteItem favorite = favorites.get(position);
            ArticleReference reference = resolveArticle(favorite, articleIndex);
            ArticleUserContentState contentState = reference == null
                    ? ArticleUserContentState.EMPTY
                    : contentSession.resolve(reference.block, reference.article);
            String searchable = favorite.getTitle() + " " + favorite.getSubtitle();
            if (reference != null) {
                searchable += " " + reference.article.number + " " + reference.article.title;
            }
            result.add(new FavoriteListItem(
                    favorite,
                    reference == null ? null : reference.article.number,
                    contentState.hasNote(),
                    contentState.hasHighlight(),
                    reference == null ? Integer.MAX_VALUE : reference.legalOrder,
                    position,
                    SearchTextNormalizer.normalizePlain(searchable)
            ));
        }
        return result;
    }

    @NonNull
    private Map<String, ArticleReference> buildArticleIndex() {
        Map<String, ArticleReference> result = new HashMap<>();
        Set<String> ambiguousNumbers = new HashSet<>();
        int legalOrder = 0;
        for (ArticleBlock block : ArticleRepository.getBlocks(getApplication()).values()) {
            for (Article article : block.articles) {
                String number = article.number.toUpperCase(Locale.ROOT);
                if (result.containsKey(number)) {
                    result.remove(number);
                    ambiguousNumbers.add(number);
                } else if (!ambiguousNumbers.contains(number)) {
                    result.put(number, new ArticleReference(block, article, legalOrder));
                }
                legalOrder++;
            }
        }
        return result;
    }

    private ArticleReference resolveArticle(
            FavoriteItem favorite,
            Map<String, ArticleReference> articleIndex
    ) {
        String destination = favorite.getDestinationId();
        if (!destination.startsWith(FavoriteDestinationMapper.ARTICLE_PREFIX)) {
            return null;
        }
        String number = destination.substring(FavoriteDestinationMapper.ARTICLE_PREFIX.length())
                .trim().toUpperCase(Locale.ROOT);
        return articleIndex.get(number);
    }

    private void refreshState() {
        String normalizedQuery = SearchTextNormalizer.normalizePlain(query);
        List<FavoriteListItem> visible = new ArrayList<>();
        for (FavoriteListItem item : preparedFavorites) {
            if (matchesFilter(item) && item.matchesQuery(normalizedQuery)) {
                visible.add(item);
            }
        }
        visible.sort(sortMode == SortMode.ARTICLE_NUMBER
                ? ARTICLE_NUMBER_COMPARATOR
                : RECENT_COMPARATOR);
        uiState.setValue(new FavoritosUiState(
                visible,
                query,
                filterMode,
                sortMode,
                resolveEmptyState(visible),
                sourceFavorites.size()
        ));
    }

    private boolean matchesFilter(FavoriteListItem item) {
        if (filterMode == FilterMode.WITH_NOTES) {
            return item.isArticle() && item.hasNote();
        }
        if (filterMode == FilterMode.WITH_HIGHLIGHTS) {
            return item.isArticle() && item.hasHighlight();
        }
        return true;
    }

    @NonNull
    private FavoritosUiState.EmptyState resolveEmptyState(List<FavoriteListItem> visible) {
        if (!visible.isEmpty()) {
            return FavoritosUiState.EmptyState.NONE;
        }
        if (sourceFavorites.isEmpty()) {
            return FavoritosUiState.EmptyState.NO_FAVORITES;
        }
        if (!query.trim().isEmpty()) {
            return FavoritosUiState.EmptyState.NO_QUERY_RESULTS;
        }
        if (filterMode == FilterMode.WITH_NOTES) {
            return FavoritosUiState.EmptyState.NO_NOTES;
        }
        if (filterMode == FilterMode.WITH_HIGHLIGHTS) {
            return FavoritosUiState.EmptyState.NO_HIGHLIGHTS;
        }
        return FavoritosUiState.EmptyState.NO_QUERY_RESULTS;
    }

    private static final Comparator<FavoriteListItem> RECENT_COMPARATOR = (first, second) -> {
        long firstTime = first.getFavorite().getAddedAt();
        long secondTime = second.getFavorite().getAddedAt();
        boolean firstDated = firstTime > 0L;
        boolean secondDated = secondTime > 0L;
        if (firstDated != secondDated) {
            return firstDated ? -1 : 1;
        }
        if (firstDated && firstTime != secondTime) {
            return Long.compare(secondTime, firstTime);
        }
        return Integer.compare(first.getOriginalPosition(), second.getOriginalPosition());
    };

    private static final Comparator<FavoriteListItem> ARTICLE_NUMBER_COMPARATOR =
            (first, second) -> {
                if (first.isArticle() != second.isArticle()) {
                    return first.isArticle() ? -1 : 1;
                }
                if (first.isArticle()) {
                    int order = Integer.compare(first.getLegalOrder(), second.getLegalOrder());
                    if (order != 0) {
                        return order;
                    }
                }
                int title = SearchTextNormalizer.normalizePlain(first.getFavorite().getTitle())
                        .compareTo(SearchTextNormalizer.normalizePlain(
                                second.getFavorite().getTitle()));
                return title != 0
                        ? title
                        : Integer.compare(first.getOriginalPosition(), second.getOriginalPosition());
            };

    private static final class ArticleReference {
        final ArticleBlock block;
        final Article article;
        final int legalOrder;

        ArticleReference(ArticleBlock block, Article article, int legalOrder) {
            this.block = block;
            this.article = article;
            this.legalOrder = legalOrder;
        }
    }
}
