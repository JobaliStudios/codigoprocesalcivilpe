package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalContentCatalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Motor local, determinista y precalculado de búsqueda jurídica a nivel de artículo. */
public final class LegalSearchEngine {
    public static final int MAX_RESULTS = 75;

    private static final int SCORE_NUMBER_EXACT = 10_000;
    private static final int SCORE_NUMBER_PREFIX = 9_000;
    private static final int SCORE_NUMBER_CONTAINS = 8_000;
    private static final int SCORE_TITLE_EXACT = 7_000;
    private static final int SCORE_TITLE_PREFIX = 6_000;
    private static final int SCORE_TITLE_CONTAINS = 5_000;
    private static final int SCORE_BODY_PHRASE = 4_000;
    private static final int SCORE_ALL_TERMS = 3_000;

    private final List<ArticleSearchItem> index;
    private final SearchSnippetBuilder snippetBuilder = new SearchSnippetBuilder();

    private LegalSearchEngine(List<ArticleSearchItem> index) {
        this.index = Collections.unmodifiableList(index);
    }

    @NonNull
    public static LegalSearchEngine create(@NonNull Context context) {
        Context appContext = context.getApplicationContext();
        Map<String, LegalContentCatalog.Entry> entriesByBlock = new HashMap<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            entriesByBlock.put(
                    appContext.getResources().getResourceEntryName(entry.textRes),
                    entry
            );
        }

        List<ArticleSearchItem> items = new ArrayList<>();
        int order = 0;
        for (ArticleBlock block : ArticleRepository.getBlocks(appContext).values()) {
            LegalContentCatalog.Entry entry = entriesByBlock.get(block.key);
            if (entry == null) {
                continue;
            }
            String section = appContext.getString(entry.sectionNameRes);
            String range = appContext.getString(entry.articleRangeRes);
            String legalContext = appContext.getString(entry.titleRes)
                    + " · " + appContext.getString(entry.subtitleRes);
            for (Article article : block.articles) {
                items.add(new ArticleSearchItem(
                        article,
                        block.key,
                        section,
                        range,
                        legalContext,
                        entry.textRes,
                        entry.titleRes,
                        entry.subtitleRes,
                        order++
                ));
            }
        }
        return new LegalSearchEngine(items);
    }

    @NonNull
    public SearchResponse search(
            String rawQuery,
            @NonNull SearchFilter filter,
            @NonNull Set<String> selectedSections,
            @NonNull Set<String> favoriteArticleNumbers
    ) {
        String query = rawQuery == null ? "" : rawQuery.trim();
        String normalizedQuery = SearchTextNormalizer.normalizePlain(query);
        Set<String> favorites = canonicalNumbers(favoriteArticleNumbers);

        if (query.isEmpty() && filter != SearchFilter.FAVORITES) {
            return new SearchResponse(Collections.emptyList(), false);
        }

        String numberQuery = ArticleNumberQueryParser.extract(query);
        boolean invalidNumber = filter == SearchFilter.NUMBER
                && !query.isEmpty() && numberQuery == null;
        if (invalidNumber) {
            return new SearchResponse(Collections.emptyList(), true);
        }

        List<ArticleSearchResult> results = new ArrayList<>();
        for (ArticleSearchItem item : index) {
            if (!selectedSections.isEmpty() && !selectedSections.contains(item.sectionName)) {
                continue;
            }
            boolean favorite = favorites.contains(item.normalizedNumber);
            if (filter == SearchFilter.FAVORITES && !favorite) {
                continue;
            }

            Match match;
            if (query.isEmpty()) {
                match = Match.preview(1);
            } else if (filter == SearchFilter.NUMBER) {
                match = numberMatch(item, numberQuery);
            } else if (filter == SearchFilter.TEXT) {
                match = textMatch(item, query, normalizedQuery);
            } else {
                Match number = numberQuery == null ? Match.none() : numberMatch(item, numberQuery);
                Match text = textMatch(item, query, normalizedQuery);
                match = number.score >= text.score ? number : text;
            }

            if (match.score <= 0) {
                continue;
            }
            SearchSnippetBuilder.Snippet snippet = match.preview
                    ? snippetBuilder.preview(item.article.text)
                    : snippetBuilder.build(item.article.text, match.snippetQuery,
                    match.minimumSnippetOffset);
            results.add(new ArticleSearchResult(item, snippet, match.score, favorite));
        }

        results.sort(Comparator
                .comparingInt((ArticleSearchResult result) -> result.relevance).reversed()
                .thenComparingInt(result -> result.item.sequenceOrder));
        if (results.size() > MAX_RESULTS) {
            results = new ArrayList<>(results.subList(0, MAX_RESULTS));
        }
        return new SearchResponse(Collections.unmodifiableList(results), false);
    }

    int indexSize() {
        return index.size();
    }

    private Match numberMatch(ArticleSearchItem item, String numberQuery) {
        if (numberQuery == null) {
            return Match.none();
        }
        String wanted = numberQuery.toUpperCase(Locale.ROOT);
        if (item.normalizedNumber.equals(wanted)) {
            return Match.preview(SCORE_NUMBER_EXACT);
        }
        if (item.normalizedNumber.startsWith(wanted)) {
            return Match.preview(SCORE_NUMBER_PREFIX);
        }
        if (item.normalizedNumber.contains(wanted)) {
            return Match.preview(SCORE_NUMBER_CONTAINS);
        }
        return Match.none();
    }

    private Match textMatch(ArticleSearchItem item, String query, String normalizedQuery) {
        if (normalizedQuery.isEmpty()) {
            return Match.none();
        }
        if (item.normalizedTitle.equals(normalizedQuery)) {
            return Match.text(SCORE_TITLE_EXACT, query, 0);
        }
        if (item.normalizedTitle.startsWith(normalizedQuery)) {
            return Match.text(SCORE_TITLE_PREFIX, query, 0);
        }
        if (item.normalizedTitle.contains(normalizedQuery)) {
            return Match.text(SCORE_TITLE_CONTAINS, query, 0);
        }
        if (item.normalizedBody.contains(normalizedQuery)) {
            return Match.text(SCORE_BODY_PHRASE, query, item.bodyStartOffset);
        }

        String[] terms = normalizedQuery.split(" ");
        if (terms.length > 1 && containsAllTerms(item, terms)) {
            String firstMatchingTerm = firstTermPresent(item, terms);
            return Match.text(SCORE_ALL_TERMS, firstMatchingTerm, item.bodyStartOffset);
        }
        return Match.none();
    }

    private boolean containsAllTerms(ArticleSearchItem item, String[] terms) {
        String combined = item.normalizedTitle + " " + item.normalizedBody;
        for (String term : terms) {
            if (!term.isEmpty() && !combined.contains(term)) {
                return false;
            }
        }
        return true;
    }

    private String firstTermPresent(ArticleSearchItem item, String[] terms) {
        for (String term : terms) {
            if (item.normalizedBody.contains(term)) {
                return term;
            }
        }
        for (String term : terms) {
            if (item.normalizedTitle.contains(term)) {
                return term;
            }
        }
        return terms[0];
    }

    private Set<String> canonicalNumbers(Set<String> numbers) {
        Set<String> result = new HashSet<>();
        for (String number : numbers) {
            result.add(number.trim().toUpperCase(Locale.ROOT));
        }
        return result;
    }

    private static final class Match {
        final int score;
        final boolean preview;
        final String snippetQuery;
        final int minimumSnippetOffset;

        private Match(int score, boolean preview, String snippetQuery, int minimumSnippetOffset) {
            this.score = score;
            this.preview = preview;
            this.snippetQuery = snippetQuery;
            this.minimumSnippetOffset = minimumSnippetOffset;
        }

        static Match none() {
            return new Match(0, true, "", 0);
        }

        static Match preview(int score) {
            return new Match(score, true, "", 0);
        }

        static Match text(int score, String query, int minimumOffset) {
            return new Match(score, false, query, minimumOffset);
        }
    }

    public static final class SearchResponse {
        @NonNull public final List<ArticleSearchResult> results;
        public final boolean invalidNumberQuery;

        SearchResponse(
                @NonNull List<ArticleSearchResult> results,
                boolean invalidNumberQuery
        ) {
            this.results = results;
            this.invalidNumberQuery = invalidNumberQuery;
        }
    }
}
