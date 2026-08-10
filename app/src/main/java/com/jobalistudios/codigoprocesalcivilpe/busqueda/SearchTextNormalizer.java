package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Normaliza mayúsculas y acentos conservando el mapeo hacia los offsets UTF-16 originales. */
public final class SearchTextNormalizer {
    private SearchTextNormalizer() {
    }

    @NonNull
    public static String normalizePlain(@NonNull String input) {
        return map(input).normalized.trim();
    }

    @NonNull
    public static MappedText map(@NonNull String original) {
        StringBuilder normalized = new StringBuilder();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        for (int offset = 0; offset < original.length();) {
            int codePoint = original.codePointAt(offset);
            int charCount = Character.charCount(codePoint);
            int originalEnd = offset + charCount;

            if (Character.isWhitespace(codePoint)) {
                if (normalized.length() == 0
                        || normalized.charAt(normalized.length() - 1) != ' ') {
                    normalized.append(' ');
                    starts.add(offset);
                    ends.add(originalEnd);
                } else {
                    ends.set(ends.size() - 1, originalEnd);
                }
                offset = originalEnd;
                continue;
            }

            String value = new String(Character.toChars(codePoint));
            String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD);
            for (int inner = 0; inner < decomposed.length();) {
                int decomposedCodePoint = decomposed.codePointAt(inner);
                int type = Character.getType(decomposedCodePoint);
                if (type != Character.NON_SPACING_MARK
                        && type != Character.COMBINING_SPACING_MARK
                        && type != Character.ENCLOSING_MARK) {
                    String lower = new String(Character.toChars(decomposedCodePoint))
                            .toLowerCase(Locale.ROOT);
                    normalized.append(lower);
                    for (int index = 0; index < lower.length(); index++) {
                        starts.add(offset);
                        ends.add(originalEnd);
                    }
                }
                inner += Character.charCount(decomposedCodePoint);
            }
            offset = originalEnd;
        }

        return new MappedText(original, normalized.toString(), toArray(starts), toArray(ends));
    }

    @Nullable
    public static Range findFirst(
            @NonNull String original,
            @NonNull String query,
            int minimumOriginalOffset
    ) {
        MappedText mapped = map(original);
        String normalizedQuery = normalizePlain(query);
        if (normalizedQuery.isEmpty()) {
            return null;
        }
        int normalizedFrom = mapped.normalizedOffsetForOriginal(minimumOriginalOffset);
        int match = mapped.normalized.indexOf(normalizedQuery, normalizedFrom);
        return match < 0 ? null : mapped.toOriginalRange(match, match + normalizedQuery.length());
    }

    @NonNull
    public static List<Range> findAll(@NonNull String original, @NonNull String query) {
        MappedText mapped = map(original);
        String normalizedQuery = normalizePlain(query);
        List<Range> ranges = new ArrayList<>();
        if (normalizedQuery.isEmpty()) {
            return ranges;
        }
        int from = 0;
        while (from <= mapped.normalized.length() - normalizedQuery.length()) {
            int match = mapped.normalized.indexOf(normalizedQuery, from);
            if (match < 0) {
                break;
            }
            ranges.add(mapped.toOriginalRange(match, match + normalizedQuery.length()));
            from = match + normalizedQuery.length();
        }
        return ranges;
    }

    private static int[] toArray(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    public static final class MappedText {
        @NonNull public final String original;
        @NonNull public final String normalized;
        private final int[] originalStarts;
        private final int[] originalEnds;

        private MappedText(
                @NonNull String original,
                @NonNull String normalized,
                int[] originalStarts,
                int[] originalEnds
        ) {
            this.original = original;
            this.normalized = normalized;
            this.originalStarts = originalStarts;
            this.originalEnds = originalEnds;
        }

        int normalizedOffsetForOriginal(int originalOffset) {
            int clamped = Math.max(0, originalOffset);
            for (int i = 0; i < originalStarts.length; i++) {
                if (originalStarts[i] >= clamped) {
                    return i;
                }
            }
            return normalized.length();
        }

        @NonNull
        Range toOriginalRange(int normalizedStart, int normalizedEnd) {
            if (normalizedStart < 0 || normalizedStart >= originalStarts.length
                    || normalizedEnd <= normalizedStart) {
                throw new IllegalArgumentException("Rango normalizado inválido");
            }
            int last = Math.min(normalizedEnd - 1, originalEnds.length - 1);
            return new Range(originalStarts[normalizedStart], originalEnds[last]);
        }
    }

    public static final class Range {
        public final int start;
        public final int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
