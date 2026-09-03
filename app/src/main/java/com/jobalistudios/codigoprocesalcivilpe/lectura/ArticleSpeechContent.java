package com.jobalistudios.codigoprocesalcivilpe.lectura;

import androidx.annotation.NonNull;

/** Contenido jurídico estructurado de un único artículo preparado para lectura. */
public final class ArticleSpeechContent {

    @NonNull public final String articleNumber;
    @NonNull public final String articleTitle;
    @NonNull public final String rawText;

    public ArticleSpeechContent(
            @NonNull String articleNumber,
            @NonNull String articleTitle,
            @NonNull String rawText
    ) {
        this.articleNumber = articleNumber;
        this.articleTitle = articleTitle;
        this.rawText = rawText;
    }
}
