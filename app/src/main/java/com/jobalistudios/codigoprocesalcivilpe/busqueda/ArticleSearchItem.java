package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;

/** Entrada precalculada del índice local de búsqueda, una por artículo jurídico. */
public final class ArticleSearchItem {
    @NonNull public final Article article;
    @NonNull public final String blockKey;
    @NonNull public final String sectionName;
    @NonNull public final String articleRange;
    @NonNull public final String legalContext;
    @StringRes public final int textResId;
    @StringRes public final int titleResId;
    @StringRes public final int subtitleResId;
    public final int sequenceOrder;
    public final int bodyStartOffset;

    final String normalizedNumber;
    final String normalizedTitle;
    final String normalizedBody;

    ArticleSearchItem(
            @NonNull Article article,
            @NonNull String blockKey,
            @NonNull String sectionName,
            @NonNull String articleRange,
            @NonNull String legalContext,
            @StringRes int textResId,
            @StringRes int titleResId,
            @StringRes int subtitleResId,
            int sequenceOrder
    ) {
        this.article = article;
        this.blockKey = blockKey;
        this.sectionName = sectionName;
        this.articleRange = articleRange;
        this.legalContext = legalContext;
        this.textResId = textResId;
        this.titleResId = titleResId;
        this.subtitleResId = subtitleResId;
        this.sequenceOrder = sequenceOrder;
        int newline = article.text.indexOf('\n');
        this.bodyStartOffset = newline < 0 ? article.text.length() : newline + 1;
        this.normalizedNumber = article.number.toUpperCase(java.util.Locale.ROOT);
        this.normalizedTitle = SearchTextNormalizer.normalizePlain(article.title);
        this.normalizedBody = SearchTextNormalizer.normalizePlain(
                article.text.substring(bodyStartOffset));
    }

    @NonNull
    public String displayTitle() {
        return article.title.trim().isEmpty()
                ? "Artículo " + article.number
                : "Artículo " + article.number + " — " + article.title;
    }
}
