package com.jobalistudios.codigoprocesalcivilpe.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Estado inmutable y pequeño que Home necesita para renderizar el dashboard. */
public final class HomeDashboardState {
    @Nullable public final ArticleEntry continueReading;
    @NonNull public final List<ArticleEntry> recentlyViewed;
    @NonNull public final List<ArticleEntry> recentFavorites;
    @Nullable public final ArticleOfTheDaySelector.Selection articleOfTheDay;
    public final boolean articleOfTheDayIsFavorite;
    public final int continuePosition;
    public final int continueTotal;

    HomeDashboardState(
            @Nullable ArticleEntry continueReading,
            @NonNull List<ArticleEntry> recentlyViewed,
            @NonNull List<ArticleEntry> recentFavorites,
            @Nullable ArticleOfTheDaySelector.Selection articleOfTheDay,
            boolean articleOfTheDayIsFavorite, int continuePosition, int continueTotal
    ) {
        this.continueReading = continueReading;
        this.articleOfTheDay = articleOfTheDay;
        this.articleOfTheDayIsFavorite = articleOfTheDayIsFavorite;
        this.continuePosition = continuePosition;
        this.continueTotal = continueTotal;
        this.recentlyViewed = immutableCopy(recentlyViewed);
        this.recentFavorites = immutableCopy(recentFavorites);
    }

    private static List<ArticleEntry> immutableCopy(List<ArticleEntry> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    public static final class ArticleEntry {
        @NonNull public final String number;
        @NonNull public final String title;

        ArticleEntry(@NonNull String number, @NonNull String title) {
            this.number = number;
            this.title = title;
        }
    }
}
