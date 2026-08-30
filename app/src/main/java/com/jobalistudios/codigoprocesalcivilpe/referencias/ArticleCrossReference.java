package com.jobalistudios.codigoprocesalcivilpe.referencias;

import androidx.annotation.NonNull;

/** Referencia potencial detectada sin modificar el texto jurídico original. */
public final class ArticleCrossReference {
    public final int start;
    public final int end;
    @NonNull public final String articleNumber;
    @NonNull public final String rawText;
    public final boolean externalContext;

    ArticleCrossReference(
            int start,
            int end,
            @NonNull String articleNumber,
            @NonNull String rawText,
            boolean externalContext
    ) {
        this.start = start;
        this.end = end;
        this.articleNumber = articleNumber;
        this.rawText = rawText;
        this.externalContext = externalContext;
    }
}
