package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.ArticleQuickNotes;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Índice basado en artículos y estados persistentes; no modifica datos del usuario. */
public final class ChapterIndexStateBuilder {
    public enum Filter { ALL, FAVORITES, NOTES, HIGHLIGHTS }

    public static final class Entry {
        @NonNull public final String number;
        @NonNull public final String summary;
        public final int position;
        public final boolean favorite;
        public final boolean note;
        public final boolean highlight;
        public final boolean current;

        Entry(Article article, int position, boolean favorite, boolean note,
              boolean highlight, boolean current) {
            this.number = article.number;
            this.summary = article.title.trim().isEmpty()
                    ? article.number : article.title.trim();
            this.position = position;
            this.favorite = favorite;
            this.note = note;
            this.highlight = highlight;
            this.current = current;
        }
    }

    private final Context context;
    private final FavoritesManager favorites;
    private final HighlightsManager highlights;

    public ChapterIndexStateBuilder(@NonNull Context context) {
        this.context = context.getApplicationContext();
        favorites = new FavoritesManager(this.context);
        highlights = new HighlightsManager(this.context);
    }

    @NonNull
    public List<Entry> build(@NonNull String blockKey, @Nullable String currentNumber,
                             @Nullable String search, @NonNull Filter filter) {
        ArticleBlock block = ArticleRepository.getBlock(context, blockKey);
        if (block == null) return Collections.emptyList();
        return build(block, currentNumber, search, filter, favorites, highlights);
    }

    @NonNull
    static List<Entry> build(@NonNull ArticleBlock block, @Nullable String currentNumber,
                             @Nullable String search, @NonNull Filter filter,
                             FavoritesManager favorites, HighlightsManager manager) {
        String query = SearchTextNormalizer.normalizePlain(search == null ? "" : search);
        List<Highlight> saved = manager.getForBlock(block.key);
        String content = block.fullText();
        List<Entry> result = new ArrayList<>();
        for (int i = 0; i < block.articles.size(); i++) {
            Article article = block.articles.get(i);
            boolean favorite = favorites.isFavorite(
                    FavoriteDestinationMapper.destinationForArticle(article.number));
            boolean note = false;
            boolean highlight = false;
            int begin = article.offsetInBlock;
            int end = begin + article.text.length();
            for (Highlight stored : saved) {
                String quickNumber = ArticleQuickNotes.articleNumberFromId(stored.getId());
                boolean quickForArticle = article.number.equals(quickNumber);
                int[] range = quickForArticle ? null : HighlightsManager.resolveRange(stored, content);
                boolean inArticle = quickForArticle
                        || (range != null && range[0] < end && range[1] > begin);
                if (!inArticle) continue;
                note |= stored.hasNote() || quickForArticle;
                highlight |= !quickForArticle;
            }
            if (filter == Filter.FAVORITES && !favorite
                    || filter == Filter.NOTES && !note
                    || filter == Filter.HIGHLIGHTS && !highlight) continue;
            if (!query.isEmpty() && !SearchTextNormalizer.normalizePlain(
                    article.number + " " + article.title).contains(query)) continue;
            result.add(new Entry(article, i + 1, favorite, note, highlight,
                    article.number.equals(currentNumber)));
        }
        return result;
    }
}
