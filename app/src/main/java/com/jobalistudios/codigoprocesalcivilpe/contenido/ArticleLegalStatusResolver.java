package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

/** Resuelve el estado solo desde el título estructurado del artículo, nunca desde su cuerpo. */
public final class ArticleLegalStatusResolver {

    public enum Status {
        ACTIVE,
        REPEALED
    }

    private static final Pattern REPEALED_TITLE = Pattern.compile(
            "^\\[\\s*Derogad[oa]\\s*\\]$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private ArticleLegalStatusResolver() {
    }

    @NonNull
    public static Status resolve(@Nullable Article article) {
        return article != null && REPEALED_TITLE.matcher(article.title.trim()).matches()
                ? Status.REPEALED
                : Status.ACTIVE;
    }

    public static boolean isRepealed(@Nullable Article article) {
        return resolve(article) == Status.REPEALED;
    }
}
