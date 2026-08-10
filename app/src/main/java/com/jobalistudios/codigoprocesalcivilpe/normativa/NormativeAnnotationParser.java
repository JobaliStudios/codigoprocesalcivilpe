package com.jobalistudios.codigoprocesalcivilpe.normativa;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detecta de forma conservadora las anotaciones normativas ya presentes en el CPC. */
public final class NormativeAnnotationParser {

    private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    private static final Pattern ANNOTATION_START = Pattern.compile(
            "\\*[\\t ]*(?:"
                    + "Sumilla\\s+y\\s+artículo|"
                    + "Artículos?|Sumilla|Numeral(?:es)?|Denominación|Subcapítulo|Capítulo|"
                    + "Título|Sección|Párrafos?|Incisos?|Literales?"
                    + ")[^\\n]*(?:modificad[oa]s?|incorporad[oa]s?|sustituid[oa]s?|"
                    + "derogad[oa]s?)(?!\\p{L})[^\\n]*",
            REGEX_FLAGS
    );
    private static final Pattern PARAGRAPH_END = Pattern.compile("\\r?\\n[\\t ]*\\r?\\n");
    private static final Pattern LEGAL_INSTRUMENT = Pattern.compile(
            "(?:Decreto\\s+Legislativo|Decreto\\s+Ley|D\\s*-\\s*L|DL|Ley)"
                    + "\\s*(?:N(?:[.°º])?\\s*)?\\d+[A-Z-]*",
            REGEX_FLAGS
    );
    private static final Pattern PUBLICATION_DATE = Pattern.compile(
            "\\d{1,2}\\s+de\\s+(?:enero|febrero|marzo|abril|mayo|junio|julio|"
                    + "agosto|setiembre|septiembre|octubre|noviembre|diciembre)"
                    + "\\s+de\\s+\\d{4}",
            REGEX_FLAGS
    );

    @NonNull
    public List<NormativeAnnotation> parse(@NonNull String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> starts = new ArrayList<>();
        Matcher annotationMatcher = ANNOTATION_START.matcher(text);
        while (annotationMatcher.find()) {
            starts.add(annotationMatcher.start());
        }
        if (starts.isEmpty()) {
            return Collections.emptyList();
        }

        List<NormativeAnnotation> annotations = new ArrayList<>(starts.size());
        for (int index = 0; index < starts.size(); index++) {
            int start = starts.get(index);
            int nextAnnotationStart = index + 1 < starts.size()
                    ? starts.get(index + 1)
                    : text.length();
            Matcher paragraphEndMatcher = PARAGRAPH_END.matcher(text);
            int end = paragraphEndMatcher.find(start)
                    ? Math.min(paragraphEndMatcher.start(), nextAnnotationStart)
                    : nextAnnotationStart;
            String rawText = text.substring(start, end);
            annotations.add(new NormativeAnnotation(
                    start,
                    end,
                    rawText,
                    resolveType(rawText),
                    lastMatch(LEGAL_INSTRUMENT, rawText),
                    lastMatch(PUBLICATION_DATE, rawText)
            ));
        }
        return Collections.unmodifiableList(annotations);
    }

    @NonNull
    private NormativeAnnotation.Type resolveType(@NonNull String rawText) {
        String normalized = rawText.toLowerCase(Locale.ROOT);
        if (normalized.matches("(?s).*derogad[oa]s?.*")) {
            return NormativeAnnotation.Type.REPEALED;
        }
        if (normalized.matches("(?s).*sustituid[oa]s?.*")) {
            return NormativeAnnotation.Type.SUBSTITUTED;
        }
        if (normalized.matches("(?s).*incorporad[oa]s?.*")) {
            return NormativeAnnotation.Type.INCORPORATED;
        }
        if (normalized.matches("(?s).*modificad[oa]s?.*")) {
            return NormativeAnnotation.Type.MODIFIED;
        }
        return NormativeAnnotation.Type.OTHER;
    }

    private String lastMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        String match = null;
        while (matcher.find()) {
            match = matcher.group();
        }
        return match;
    }
}
