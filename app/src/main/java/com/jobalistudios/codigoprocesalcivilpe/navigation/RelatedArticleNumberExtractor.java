package com.jobalistudios.codigoprocesalcivilpe.navigation;

import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extrae el identificador canónico de textos como "Artículo 506-A modificado...". */
public final class RelatedArticleNumberExtractor {
    private static final Pattern ARTICLE_PATTERN = Pattern.compile(
            "\\bart[ií]culo\\s+(\\d+)(?:\\s*-\\s*([a-z]))?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private RelatedArticleNumberExtractor() {
    }

    @Nullable
    public static String extract(@Nullable String relatedArticle) {
        if (relatedArticle == null) {
            return null;
        }
        Matcher matcher = ARTICLE_PATTERN.matcher(relatedArticle.trim());
        if (!matcher.find()) {
            return null;
        }

        String suffix = matcher.group(2);
        return suffix == null
                ? matcher.group(1)
                : matcher.group(1) + "-" + suffix.toUpperCase(Locale.ROOT);
    }
}
