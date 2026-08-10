package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/** Resultado ya preparado para presentar; el Adapter no vuelve a ejecutar la búsqueda. */
public final class ArticleSearchResult {
    @NonNull public final ArticleSearchItem item;
    @NonNull public final String snippet;
    @NonNull public final List<SearchTextNormalizer.Range> highlightRanges;
    /** Offset de coincidencia dentro del artículo; -1 para resultados puramente numéricos. */
    public final int matchOffsetInArticle;
    public final int relevance;
    public final boolean isFavorite;

    ArticleSearchResult(
            @NonNull ArticleSearchItem item,
            @NonNull SearchSnippetBuilder.Snippet snippet,
            int relevance,
            boolean isFavorite
    ) {
        this.item = item;
        this.snippet = snippet.text;
        this.highlightRanges = Collections.unmodifiableList(snippet.highlightRanges);
        this.matchOffsetInArticle = snippet.matchOffsetInSource;
        this.relevance = relevance;
        this.isFavorite = isFavorite;
    }
}
