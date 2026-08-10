package com.jobalistudios.codigoprocesalcivilpe.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.RecentArticle;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Lee las fuentes locales existentes y prepara los datos visibles del dashboard. */
public final class HomeDashboardStateBuilder {
    static final int MAX_RECENTLY_VIEWED = 3;
    static final int MAX_RECENT_FAVORITES = 3;

    private final Context appContext;
    private final ReadingHistoryManager readingHistoryManager;
    private final FavoritesManager favoritesManager;

    public HomeDashboardStateBuilder(@NonNull Context context) {
        appContext = context.getApplicationContext();
        readingHistoryManager = new ReadingHistoryManager(appContext);
        favoritesManager = new FavoritesManager(appContext);
    }

    @NonNull
    public HomeDashboardState build() {
        List<HomeDashboardState.ArticleEntry> validHistory = resolveHistory(
                readingHistoryManager.getRecentArticles());
        HomeDashboardState.ArticleEntry continueReading = validHistory.isEmpty()
                ? null : validHistory.get(0);
        List<HomeDashboardState.ArticleEntry> recentlyViewed = new ArrayList<>();
        for (int index = 1;
             index < validHistory.size() && recentlyViewed.size() < MAX_RECENTLY_VIEWED;
             index++) {
            recentlyViewed.add(validHistory.get(index));
        }

        return new HomeDashboardState(
                continueReading,
                recentlyViewed,
                resolveRecentFavorites(favoritesManager.getAll())
        );
    }

    private List<HomeDashboardState.ArticleEntry> resolveHistory(
            List<RecentArticle> storedArticles
    ) {
        List<HomeDashboardState.ArticleEntry> result = new ArrayList<>();
        for (RecentArticle recent : storedArticles) {
            ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                    appContext, recent.getNumber());
            if (target == null) {
                continue;
            }
            String title = target.getTitle().isEmpty() ? recent.getTitle() : target.getTitle();
            result.add(new HomeDashboardState.ArticleEntry(target.getNumber(), title));
        }
        return result;
    }

    List<HomeDashboardState.ArticleEntry> resolveRecentFavorites(
            List<FavoriteItem> storedFavorites
    ) {
        List<FavoriteCandidate> candidates = new ArrayList<>();
        for (int index = 0; index < storedFavorites.size(); index++) {
            FavoriteItem item = storedFavorites.get(index);
            String destination = item.getDestinationId();
            if (!destination.startsWith(FavoriteDestinationMapper.ARTICLE_PREFIX)) {
                continue;
            }
            String number = destination.substring(FavoriteDestinationMapper.ARTICLE_PREFIX.length());
            ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                    appContext, number);
            if (target == null) {
                continue;
            }
            candidates.add(new FavoriteCandidate(
                    new HomeDashboardState.ArticleEntry(target.getNumber(), target.getTitle()),
                    item.getAddedAt(),
                    index
            ));
        }

        candidates.sort(Comparator
                .comparingLong((FavoriteCandidate candidate) -> candidate.addedAt).reversed()
                .thenComparingInt(candidate -> candidate.persistedIndex));
        List<HomeDashboardState.ArticleEntry> result = new ArrayList<>();
        for (FavoriteCandidate candidate : candidates) {
            if (result.size() == MAX_RECENT_FAVORITES) {
                break;
            }
            result.add(candidate.article);
        }
        return result;
    }

    private static final class FavoriteCandidate {
        final HomeDashboardState.ArticleEntry article;
        final long addedAt;
        final int persistedIndex;

        FavoriteCandidate(
                HomeDashboardState.ArticleEntry article,
                long addedAt,
                int persistedIndex
        ) {
            this.article = article;
            this.addedAt = addedAt;
            this.persistedIndex = persistedIndex;
        }
    }
}
