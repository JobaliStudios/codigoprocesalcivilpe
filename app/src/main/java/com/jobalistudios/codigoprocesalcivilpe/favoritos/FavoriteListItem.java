package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Modelo de presentación ya resuelto para que el Adapter no aplique reglas de negocio. */
public final class FavoriteListItem {
    @NonNull private final FavoriteItem favorite;
    @Nullable private final String articleNumber;
    private final boolean hasNote;
    private final boolean hasHighlight;
    private final int legalOrder;
    private final int originalPosition;
    @NonNull private final String normalizedSearchText;

    public FavoriteListItem(
            @NonNull FavoriteItem favorite,
            @Nullable String articleNumber,
            boolean hasNote,
            boolean hasHighlight,
            int legalOrder,
            int originalPosition,
            @NonNull String normalizedSearchText
    ) {
        this.favorite = favorite;
        this.articleNumber = articleNumber;
        this.hasNote = hasNote;
        this.hasHighlight = hasHighlight;
        this.legalOrder = legalOrder;
        this.originalPosition = originalPosition;
        this.normalizedSearchText = normalizedSearchText;
    }

    @NonNull
    public FavoriteItem getFavorite() {
        return favorite;
    }

    @Nullable
    public String getArticleNumber() {
        return articleNumber;
    }

    public boolean isArticle() {
        return articleNumber != null;
    }

    public boolean hasNote() {
        return hasNote;
    }

    public boolean hasHighlight() {
        return hasHighlight;
    }

    public int getLegalOrder() {
        return legalOrder;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public boolean matchesQuery(@NonNull String normalizedQuery) {
        return normalizedQuery.isEmpty() || normalizedSearchText.contains(normalizedQuery);
    }

    public boolean hasSameContent(@NonNull FavoriteListItem other) {
        FavoriteItem otherFavorite = other.favorite;
        return favorite.getId().equals(otherFavorite.getId())
                && favorite.getTitle().equals(otherFavorite.getTitle())
                && favorite.getSubtitle().equals(otherFavorite.getSubtitle())
                && favorite.getType().equals(otherFavorite.getType())
                && favorite.getDestinationId().equals(otherFavorite.getDestinationId())
                && favorite.getAddedAt() == otherFavorite.getAddedAt()
                && equalsNullable(articleNumber, other.articleNumber)
                && hasNote == other.hasNote
                && hasHighlight == other.hasHighlight
                && legalOrder == other.legalOrder
                && normalizedSearchText.equals(other.normalizedSearchText);
    }

    private boolean equalsNullable(@Nullable String first, @Nullable String second) {
        return first == null ? second == null : first.equals(second);
    }
}
