package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.ArticleQuickNotes;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightsManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Reúne favoritos, resaltados y notas existentes sin crear una persistencia adicional. */
public final class UserNotesAggregator {

    interface DataSource {
        @NonNull List<FavoriteItem> getFavorites();

        @NonNull Map<String, ArticleBlock> getArticleBlocks();

        @NonNull List<Highlight> getHighlights(@NonNull String blockKey);
    }

    private final DataSource dataSource;

    public UserNotesAggregator(@NonNull Context context) {
        Context appContext = context.getApplicationContext();
        FavoritesManager favoritesManager = new FavoritesManager(appContext);
        HighlightsManager highlightsManager = new HighlightsManager(appContext);
        dataSource = new DataSource() {
            @NonNull
            @Override
            public List<FavoriteItem> getFavorites() {
                return favoritesManager.getAll();
            }

            @NonNull
            @Override
            public Map<String, ArticleBlock> getArticleBlocks() {
                return ArticleRepository.getBlocks(appContext);
            }

            @NonNull
            @Override
            public List<Highlight> getHighlights(@NonNull String blockKey) {
                return highlightsManager.getForBlock(blockKey);
            }
        };
    }

    UserNotesAggregator(@NonNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Construye una instantánea nueva a partir de las fuentes de verdad actuales. */
    @NonNull
    public List<ArticleNotesEntry> build() {
        LegalIndex index = buildLegalIndex(dataSource.getArticleBlocks());
        Map<String, Accumulator> entries = new LinkedHashMap<>();

        collectFavorites(entries, index);
        collectHighlightsAndNotes(entries, index);

        List<Accumulator> populated = new ArrayList<>();
        for (Accumulator accumulator : entries.values()) {
            if (accumulator.hasContent()) {
                accumulator.highlights.sort(HIGHLIGHT_COMPARATOR);
                accumulator.articleNotes.sort(NOTE_COMPARATOR);
                populated.add(accumulator);
            }
        }
        populated.sort(ARTICLE_COMPARATOR);

        List<ArticleNotesEntry> result = new ArrayList<>(populated.size());
        for (Accumulator accumulator : populated) {
            result.add(accumulator.toEntry());
        }
        return result;
    }

    private void collectFavorites(
            Map<String, Accumulator> entries,
            LegalIndex index
    ) {
        int unresolvedOrder = 0;
        for (FavoriteItem favorite : dataSource.getFavorites()) {
            String destination = FavoriteDestinationMapper.normalizeDestinationId(
                    favorite.getDestinationId());
            if (!destination.startsWith(FavoriteDestinationMapper.ARTICLE_PREFIX)) {
                continue;
            }
            String number = ArticleRepository.normalizeArticleNumber(destination.substring(
                    FavoriteDestinationMapper.ARTICLE_PREFIX.length()));
            if (number.isEmpty()) {
                continue;
            }
            LegalArticle legalArticle = index.uniqueArticlesByNumber.get(number);
            Accumulator accumulator = entries.get(number);
            if (accumulator == null) {
                accumulator = legalArticle == null
                        ? Accumulator.unresolved(number, unresolvedOrder)
                        : Accumulator.resolved(legalArticle);
                entries.put(number, accumulator);
            }
            accumulator.favorite = true;
            unresolvedOrder++;
        }
    }

    private void collectHighlightsAndNotes(
            Map<String, Accumulator> entries,
            LegalIndex index
    ) {
        for (LegalBlock legalBlock : index.blocks) {
            String content = legalBlock.block.fullText();
            for (Highlight highlight : dataSource.getHighlights(legalBlock.block.key)) {
                String quickNoteNumber = ArticleQuickNotes.articleNumberFromId(
                        highlight.getId());
                if (quickNoteNumber != null) {
                    collectQuickNote(entries, index, highlight, quickNoteNumber);
                    continue;
                }
                int[] range = HighlightsManager.resolveRange(highlight, content);
                LegalArticle legalArticle = findContainingArticle(legalBlock, range);
                if (legalArticle == null) {
                    continue;
                }
                String number = legalArticle.normalizedNumber;
                Accumulator accumulator = entries.computeIfAbsent(
                        number,
                        ignored -> Accumulator.resolved(legalArticle)
                );
                accumulator.highlights.add(new ArticleNotesEntry.HighlightEntry(
                        content.substring(range[0], range[1]),
                        highlight.getNote(),
                        range[0],
                        range[1],
                        highlight.getCreatedAt()
                ));
            }
        }
    }

    private void collectQuickNote(
            Map<String, Accumulator> entries,
            LegalIndex index,
            Highlight highlight,
            String rawArticleNumber
    ) {
        if (!ArticleNotesEntry.hasVisibleText(highlight.getNote())) {
            return;
        }
        String number = ArticleRepository.normalizeArticleNumber(rawArticleNumber);
        if (number.isEmpty()) {
            return;
        }
        LegalArticle legalArticle = index.uniqueArticlesByNumber.get(number);
        Accumulator accumulator = entries.computeIfAbsent(
                number,
                ignored -> legalArticle == null
                        ? Accumulator.unresolved(number, Integer.MAX_VALUE)
                        : Accumulator.resolved(legalArticle)
        );
        accumulator.articleNotes.add(new ArticleNotesEntry.NoteEntry(
                highlight.getNote(),
                highlight.getCreatedAt()
        ));
    }

    private LegalArticle findContainingArticle(LegalBlock block, int[] range) {
        if (range == null) {
            return null;
        }
        for (LegalArticle article : block.articles) {
            if (range[0] >= article.start && range[1] <= article.end) {
                return article;
            }
        }
        return null;
    }

    private LegalIndex buildLegalIndex(Map<String, ArticleBlock> blocks) {
        List<LegalBlock> legalBlocks = new ArrayList<>();
        Map<String, LegalArticle> unique = new HashMap<>();
        Set<String> ambiguous = new HashSet<>();
        int legalOrder = 0;
        for (ArticleBlock block : blocks.values()) {
            List<LegalArticle> articles = new ArrayList<>();
            for (int index = 0; index < block.articles.size(); index++) {
                Article article = block.articles.get(index);
                int end = index + 1 < block.articles.size()
                        ? block.articles.get(index + 1).offsetInBlock
                        : block.fullText().length();
                LegalArticle legalArticle = new LegalArticle(
                        article,
                        ArticleRepository.normalizeArticleNumber(article.number),
                        article.offsetInBlock,
                        end,
                        legalOrder++
                );
                articles.add(legalArticle);
                if (unique.containsKey(legalArticle.normalizedNumber)) {
                    unique.remove(legalArticle.normalizedNumber);
                    ambiguous.add(legalArticle.normalizedNumber);
                } else if (!ambiguous.contains(legalArticle.normalizedNumber)) {
                    unique.put(legalArticle.normalizedNumber, legalArticle);
                }
            }
            legalBlocks.add(new LegalBlock(block, articles));
        }
        return new LegalIndex(legalBlocks, unique);
    }

    private static final Comparator<ArticleNotesEntry.HighlightEntry> HIGHLIGHT_COMPARATOR =
            Comparator.comparingInt(ArticleNotesEntry.HighlightEntry::getStart)
                    .thenComparingInt(ArticleNotesEntry.HighlightEntry::getEnd)
                    .thenComparingLong(ArticleNotesEntry.HighlightEntry::getCreatedAt);

    private static final Comparator<ArticleNotesEntry.NoteEntry> NOTE_COMPARATOR =
            Comparator.comparingLong(ArticleNotesEntry.NoteEntry::getCreatedAt);

    private static final Comparator<Accumulator> ARTICLE_COMPARATOR =
            Comparator.comparingInt((Accumulator entry) -> entry.legalOrder)
                    .thenComparingInt(entry -> entry.unresolvedOrder)
                    .thenComparing(entry -> entry.number.toUpperCase(Locale.ROOT));

    private static final class LegalIndex {
        final List<LegalBlock> blocks;
        final Map<String, LegalArticle> uniqueArticlesByNumber;

        LegalIndex(List<LegalBlock> blocks, Map<String, LegalArticle> uniqueArticlesByNumber) {
            this.blocks = blocks;
            this.uniqueArticlesByNumber = uniqueArticlesByNumber;
        }
    }

    private static final class LegalBlock {
        final ArticleBlock block;
        final List<LegalArticle> articles;

        LegalBlock(ArticleBlock block, List<LegalArticle> articles) {
            this.block = block;
            this.articles = articles;
        }
    }

    private static final class LegalArticle {
        final Article article;
        final String normalizedNumber;
        final int start;
        final int end;
        final int legalOrder;

        LegalArticle(Article article, String normalizedNumber, int start, int end, int legalOrder) {
            this.article = article;
            this.normalizedNumber = normalizedNumber;
            this.start = start;
            this.end = end;
            this.legalOrder = legalOrder;
        }
    }

    private static final class Accumulator {
        final String number;
        final String title;
        final int legalOrder;
        final int unresolvedOrder;
        final List<ArticleNotesEntry.HighlightEntry> highlights = new ArrayList<>();
        final List<ArticleNotesEntry.NoteEntry> articleNotes = new ArrayList<>();
        boolean favorite;

        private Accumulator(String number, String title, int legalOrder, int unresolvedOrder) {
            this.number = number;
            this.title = title;
            this.legalOrder = legalOrder;
            this.unresolvedOrder = unresolvedOrder;
        }

        static Accumulator resolved(LegalArticle article) {
            return new Accumulator(
                    article.article.number,
                    article.article.title,
                    article.legalOrder,
                    Integer.MAX_VALUE
            );
        }

        static Accumulator unresolved(String number, int unresolvedOrder) {
            return new Accumulator(number, "", Integer.MAX_VALUE, unresolvedOrder);
        }

        boolean hasContent() {
            return favorite || !highlights.isEmpty() || !articleNotes.isEmpty();
        }

        ArticleNotesEntry toEntry() {
            return new ArticleNotesEntry(number, title, favorite, highlights, articleNotes);
        }
    }
}
