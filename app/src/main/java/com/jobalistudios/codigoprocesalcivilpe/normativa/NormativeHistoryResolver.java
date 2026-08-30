package com.jobalistudios.codigoprocesalcivilpe.normativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleLegalStatusResolver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Deriva el historial por rangos de artículo reutilizando NormativeAnnotationParser. */
public final class NormativeHistoryResolver {

    private static final DateTimeFormatter SPANISH_LONG_DATE =
            new java.time.format.DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d 'de' MMMM 'de' uuuu")
                    .toFormatter(new Locale("es", "PE"))
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern INSTRUMENT_NUMBER = Pattern.compile(
            "^(.+?)(?:\\s+N(?:[.°º]|\\.º)?\\s*)?(\\d+[A-Z-]*)$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Map<String, List<NormativeAnnotation>> ANNOTATIONS_BY_BLOCK =
            new LinkedHashMap<>();

    private final NormativeAnnotationParser annotationParser;
    private final NormativeSourceRegistry sourceRegistry;

    public NormativeHistoryResolver() {
        this(new NormativeAnnotationParser(), new NormativeSourceRegistry());
    }

    NormativeHistoryResolver(
            @NonNull NormativeAnnotationParser annotationParser,
            @NonNull NormativeSourceRegistry sourceRegistry
    ) {
        this.annotationParser = annotationParser;
        this.sourceRegistry = sourceRegistry;
    }

    /**
     * Resuelve solo las anotaciones comprendidas entre el inicio del artículo y el
     * inicio del siguiente artículo (o el final del bloque).
     */
    @NonNull
    public List<NormativeHistoryEntry> resolve(
            @NonNull Article article,
            @NonNull ArticleBlock block,
            @NonNull String blockContent
    ) {
        int articleEnd = resolveArticleEnd(article, block, blockContent.length());
        List<NormativeHistoryEntry> entries = new ArrayList<>();
        ArticleLegalStatusResolver.Status currentStatus =
                ArticleLegalStatusResolver.resolve(article);

        for (NormativeAnnotation annotation : annotationsFor(block.key, blockContent)) {
            if (annotation.start < article.offsetInBlock
                    || annotation.start >= articleEnd
                    || annotation.end > articleEnd) {
                continue;
            }
            entries.add(new NormativeHistoryEntry(
                    article.number,
                    currentStatus,
                    annotation,
                    formatInstrument(annotation.legalInstrument),
                    parseDate(annotation.publicationDate),
                    mapChangeType(annotation.type),
                    sourceRegistry.findOfficialSource(annotation.legalInstrument)
            ));
        }

        // TimSort es estable: las entradas sin fecha conservan el orden del texto.
        entries.sort(Comparator.comparing(
                (NormativeHistoryEntry entry) -> entry.publicationDate,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return Collections.unmodifiableList(entries);
    }

    /** Resuelve todas las entradas de un bloque sin volver a ejecutar el parser por artículo. */
    @NonNull
    public List<NormativeHistoryEntry> resolveBlock(
            @NonNull ArticleBlock block,
            @NonNull String blockContent
    ) {
        List<NormativeHistoryEntry> result = new ArrayList<>();
        for (Article article : block.articles) {
            result.addAll(resolve(article, block, blockContent));
        }
        return Collections.unmodifiableList(result);
    }

    private int resolveArticleEnd(Article article, ArticleBlock block, int contentLength) {
        for (int index = 0; index < block.articles.size(); index++) {
            Article candidate = block.articles.get(index);
            if (candidate == article
                    || (candidate.offsetInBlock == article.offsetInBlock
                    && candidate.number.equals(article.number))) {
                return index + 1 < block.articles.size()
                        ? block.articles.get(index + 1).offsetInBlock
                        : contentLength;
            }
        }
        return Math.min(contentLength, article.offsetInBlock + article.text.length());
    }

    @NonNull
    private List<NormativeAnnotation> annotationsFor(String blockKey, String blockContent) {
        String cacheKey = blockKey + ':' + blockContent.length() + ':' + blockContent.hashCode();
        synchronized (ANNOTATIONS_BY_BLOCK) {
            List<NormativeAnnotation> cached = ANNOTATIONS_BY_BLOCK.get(cacheKey);
            if (cached == null) {
                cached = annotationParser.parse(blockContent);
                ANNOTATIONS_BY_BLOCK.put(cacheKey, cached);
            }
            return cached;
        }
    }

    @Nullable
    static LocalDate parseDate(@Nullable String rawDate) {
        if (rawDate == null) {
            return null;
        }
        try {
            return LocalDate.parse(rawDate.trim(), SPANISH_LONG_DATE);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    @Nullable
    static String formatInstrument(@Nullable String rawInstrument) {
        if (rawInstrument == null) {
            return null;
        }
        String normalized = rawInstrument.trim().replaceAll("\\s+", " ");
        Matcher matcher = INSTRUMENT_NUMBER.matcher(normalized);
        if (!matcher.matches()) {
            return normalized;
        }
        return matcher.group(1).trim() + " N.º " + matcher.group(2);
    }

    @NonNull
    private static NormativeHistoryEntry.ChangeType mapChangeType(
            @NonNull NormativeAnnotation.Type type
    ) {
        switch (type) {
            case MODIFIED:
                return NormativeHistoryEntry.ChangeType.MODIFICATION;
            case REPEALED:
                return NormativeHistoryEntry.ChangeType.DEROGATION;
            case INCORPORATED:
                return NormativeHistoryEntry.ChangeType.INCORPORATION;
            case SUBSTITUTED:
                return NormativeHistoryEntry.ChangeType.REPLACEMENT;
            case OTHER:
                return NormativeHistoryEntry.ChangeType.OTHER;
            default:
                return NormativeHistoryEntry.ChangeType.UNKNOWN;
        }
    }
}
