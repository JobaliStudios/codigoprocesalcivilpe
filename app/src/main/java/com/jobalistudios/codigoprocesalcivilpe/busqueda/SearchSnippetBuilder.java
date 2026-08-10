package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/** Construye fragmentos contextuales alrededor de la coincidencia sin alterar el texto fuente. */
public final class SearchSnippetBuilder {
    static final int TARGET_LENGTH = 180;
    static final int MAX_LENGTH_WITHOUT_ELLIPSES = 210;
    private static final int CONTEXT_BEFORE = 75;
    private static final int BOUNDARY_WINDOW = 24;

    @NonNull
    public Snippet build(@NonNull String source, @NonNull String query) {
        return build(source, query, 0);
    }

    @NonNull
    public Snippet build(
            @NonNull String source,
            @NonNull String query,
            int minimumSourceOffset
    ) {
        SearchTextNormalizer.Range match = SearchTextNormalizer.findFirst(
                source, query, minimumSourceOffset);
        if (match == null) {
            return preview(source);
        }

        int start = Math.max(0, match.start - CONTEXT_BEFORE);
        int end = Math.min(source.length(), Math.max(match.end + CONTEXT_BEFORE,
                start + TARGET_LENGTH));
        if (end - start > MAX_LENGTH_WITHOUT_ELLIPSES) {
            end = Math.min(source.length(), start + MAX_LENGTH_WITHOUT_ELLIPSES);
        }
        if (end < match.end) {
            end = match.end;
            start = Math.max(0, end - MAX_LENGTH_WITHOUT_ELLIPSES);
        }

        start = adjustStartToWord(source, start, match.start);
        end = adjustEndToWord(source, end, match.end);
        return createSnippet(source, query, start, end, match.start);
    }

    @NonNull
    public Snippet preview(@NonNull String source) {
        if (source.isEmpty()) {
            return new Snippet("", Collections.emptyList(), -1);
        }
        int end = Math.min(source.length(), TARGET_LENGTH);
        end = adjustEndToWord(source, end, 0);
        return createSnippet(source, "", 0, end, -1);
    }

    private Snippet createSnippet(
            String source,
            String query,
            int start,
            int end,
            int matchOffset
    ) {
        String compact = compactWhitespace(source.substring(start, end));
        String text = (start > 0 ? "…" : "") + compact + (end < source.length() ? "…" : "");
        List<SearchTextNormalizer.Range> ranges = query.trim().isEmpty()
                ? Collections.emptyList()
                : SearchTextNormalizer.findAll(text, query);
        return new Snippet(text, ranges, matchOffset);
    }

    private int adjustStartToWord(String source, int proposed, int matchStart) {
        if (proposed <= 0) {
            return 0;
        }
        int limit = Math.min(matchStart, proposed + BOUNDARY_WINDOW);
        for (int index = proposed; index < limit; index++) {
            if (Character.isWhitespace(source.charAt(index))) {
                return index + 1;
            }
        }
        return proposed;
    }

    private int adjustEndToWord(String source, int proposed, int matchEnd) {
        if (proposed >= source.length()) {
            return source.length();
        }
        int limit = Math.max(matchEnd, proposed - BOUNDARY_WINDOW);
        for (int index = proposed; index > limit; index--) {
            char value = source.charAt(index - 1);
            if (Character.isWhitespace(value) || value == '.' || value == ';' || value == ',') {
                return index;
            }
        }
        return proposed;
    }

    private String compactWhitespace(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean previousWhitespace = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (Character.isWhitespace(current)) {
                if (!previousWhitespace && result.length() > 0) {
                    result.append(' ');
                }
                previousWhitespace = true;
            } else {
                result.append(current);
                previousWhitespace = false;
            }
        }
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == ' ') {
            result.deleteCharAt(length - 1);
        }
        return result.toString();
    }

    public static final class Snippet {
        @NonNull public final String text;
        @NonNull public final List<SearchTextNormalizer.Range> highlightRanges;
        /** Offset de la primera coincidencia dentro del texto fuente, o -1 para una vista previa. */
        public final int matchOffsetInSource;

        Snippet(
                @NonNull String text,
                @NonNull List<SearchTextNormalizer.Range> highlightRanges,
                int matchOffsetInSource
        ) {
            this.text = text;
            this.highlightRanges = Collections.unmodifiableList(highlightRanges);
            this.matchOffsetInSource = matchOffsetInSource;
        }
    }
}
