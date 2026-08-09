package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/** Construye el texto jurídico plano utilizado por Copiar y Compartir. */
public final class ArticleShareFormatter {
    private final String footer;

    public ArticleShareFormatter(@NonNull String footer) {
        this.footer = footer.trim();
    }

    @NonNull
    public String format(@NonNull Article article) {
        String header = buildHeader(article);
        String body = removeDuplicatedInitialHeader(article);

        StringBuilder result = new StringBuilder(header);
        if (!body.isEmpty()) {
            result.append("\n\n").append(body);
        }
        if (!footer.isEmpty()) {
            result.append("\n\n").append(footer);
        }
        return result.toString();
    }

    @NonNull
    private String buildHeader(Article article) {
        String number = article.number.trim();
        String title = article.title.trim();
        String header = "Artículo " + number;
        return title.isEmpty() ? header : header + " — " + title;
    }

    @NonNull
    private String removeDuplicatedInitialHeader(Article article) {
        String text = article.text;
        int firstLineEnd = text.indexOf('\n');
        String firstLine = (firstLineEnd >= 0 ? text.substring(0, firstLineEnd) : text).trim();
        Pattern duplicatedHeader = Pattern.compile(
                "^Artículo\\s+" + Pattern.quote(article.number.trim()) + "\\s*\\.\\s*-.*$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        String body = duplicatedHeader.matcher(firstLine).matches()
                ? text.substring(firstLineEnd >= 0 ? firstLineEnd + 1 : text.length())
                : text;
        return stripOuterLineBreaks(body);
    }

    @NonNull
    private String stripOuterLineBreaks(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && (text.charAt(start) == '\n' || text.charAt(start) == '\r')) {
            start++;
        }
        while (end > start && (text.charAt(end - 1) == '\n' || text.charAt(end - 1) == '\r')) {
            end--;
        }
        return text.substring(start, end);
    }
}
