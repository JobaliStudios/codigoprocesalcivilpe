package com.jobalistudios.codigoprocesalcivilpe.normativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleLegalStatusResolver;

import java.time.LocalDate;

/**
 * Vista estructurada de una anotación normativa perteneciente a un artículo.
 * La anotación original sigue siendo la fuente de rango, texto, norma y tipo.
 */
public final class NormativeHistoryEntry {

    public enum ChangeType {
        MODIFICATION,
        DEROGATION,
        INCORPORATION,
        REPLACEMENT,
        OTHER,
        UNKNOWN
    }

    @NonNull public final String articleNumber;
    @NonNull public final ArticleLegalStatusResolver.Status currentStatus;
    @NonNull public final NormativeAnnotation annotation;
    @Nullable public final String instrumentDisplayName;
    @Nullable public final LocalDate publicationDate;
    @NonNull public final ChangeType changeType;
    @Nullable public final String sourceUrl;

    public NormativeHistoryEntry(
            @NonNull String articleNumber,
            @NonNull ArticleLegalStatusResolver.Status currentStatus,
            @NonNull NormativeAnnotation annotation,
            @Nullable String instrumentDisplayName,
            @Nullable LocalDate publicationDate,
            @NonNull ChangeType changeType,
            @Nullable String sourceUrl
    ) {
        this.articleNumber = articleNumber;
        this.currentStatus = currentStatus;
        this.annotation = annotation;
        this.instrumentDisplayName = instrumentDisplayName;
        this.publicationDate = publicationDate;
        this.changeType = changeType;
        this.sourceUrl = sourceUrl;
    }
}
