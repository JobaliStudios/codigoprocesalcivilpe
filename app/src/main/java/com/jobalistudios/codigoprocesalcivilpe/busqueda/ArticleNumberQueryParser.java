package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reconoce las formas habituales de escribir un número de artículo sin convertirlo a int. */
public final class ArticleNumberQueryParser {
    private static final Pattern ARTICLE_QUERY_PATTERN = Pattern.compile(
            "^\\s*(?:art(?:\\.|[íi]culo)?)?\\s*(?:n[°º.]?\\s*)?"
                    + "(\\d{1,4})\\s*(?:[-\\s]?\\s*([a-z]))?\\s*\\.?\\s*$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private ArticleNumberQueryParser() {
    }

    /** Devuelve el identificador canónico (p. ej. 506-A) o null si no es una consulta numérica. */
    @Nullable
    public static String extract(String query) {
        if (query == null) {
            return null;
        }
        Matcher matcher = ARTICLE_QUERY_PATTERN.matcher(query);
        if (!matcher.matches()) {
            return null;
        }
        String letter = matcher.group(2);
        return letter == null
                ? matcher.group(1)
                : matcher.group(1) + "-" + letter.toUpperCase(Locale.ROOT);
    }
}
