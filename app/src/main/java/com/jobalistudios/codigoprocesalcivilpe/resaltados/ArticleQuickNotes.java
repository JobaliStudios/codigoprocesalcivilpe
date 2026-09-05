package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;

import java.util.function.LongSupplier;

/** Nota rápida de artículo respaldada por la persistencia cifrada de resaltados. */
public final class ArticleQuickNotes {
    private static final String ID_PREFIX = "article-quick-note:";

    private final HighlightsManager manager;
    private final String blockKey;
    private final LongSupplier clock;

    public ArticleQuickNotes(@NonNull Context context, @NonNull String blockKey) {
        this(new HighlightsManager(context), blockKey, System::currentTimeMillis);
    }

    ArticleQuickNotes(
            @NonNull HighlightsManager manager,
            @NonNull String blockKey,
            @NonNull LongSupplier clock
    ) {
        this.manager = manager;
        this.blockKey = blockKey;
        this.clock = clock;
    }

    @NonNull
    public static String idFor(@NonNull String articleNumber) {
        return ID_PREFIX + articleNumber;
    }

    /** Recupera la identidad estable del artículo sin depender de offsets antiguos. */
    @Nullable
    public static String articleNumberFromId(@Nullable String highlightId) {
        if (highlightId == null || !highlightId.startsWith(ID_PREFIX)) {
            return null;
        }
        String number = highlightId.substring(ID_PREFIX.length()).trim();
        return number.isEmpty() ? null : number;
    }

    @Nullable
    public Highlight get(@NonNull Article article) {
        return manager.find(blockKey, idFor(article.number));
    }

    public boolean has(@NonNull Article article) {
        return get(article) != null;
    }

    public void save(
            @NonNull Article article,
            @NonNull String colorTag,
            @Nullable String note
    ) {
        Highlight existing = get(article);
        String header = headerLine(article.text);
        Highlight updated = new Highlight(
                idFor(article.number),
                article.offsetInBlock,
                article.offsetInBlock + header.length(),
                colorTag,
                note,
                header,
                existing == null ? clock.getAsLong() : existing.getCreatedAt()
        );
        if (existing == null) {
            manager.add(blockKey, updated);
        } else {
            manager.update(blockKey, updated);
        }
    }

    public void remove(@NonNull Article article) {
        manager.remove(blockKey, idFor(article.number));
    }

    @NonNull
    private String headerLine(@NonNull String articleText) {
        int newline = articleText.indexOf('\n');
        return newline >= 0 ? articleText.substring(0, newline) : articleText;
    }
}
