package com.jobalistudios.codigoprocesalcivilpe.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleLegalStatusResolver;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Sorteo determinista, NO una recomendación jurídica.
 * Solo se apoya en el orden de articles.json y en la fecha del dispositivo.
 * Sin red, tareas programadas, notificaciones ni estado persistido.
 */
public final class ArticleOfTheDaySelector {
    static final int MAX_EXCERPT_CHARS = 180;
    private final Clock clock;

    public ArticleOfTheDaySelector(@NonNull Clock clock) {
        this.clock = clock;
    }

    /** Entradas en orden del corpus; permite casos vacíos en tests. */
    @Nullable
    public Selection select(@NonNull Collection<ArticleBlock> orderedBlocks) {
        List<Article> eligible = new ArrayList<>();
        for (ArticleBlock block : orderedBlocks) {
            for (Article article : block.articles) {
                if (article.title == null || article.title.trim().isEmpty()
                        || ArticleLegalStatusResolver.isRepealed(article)) {
                    continue;
                }
                eligible.add(article);
            }
        }
        if (eligible.isEmpty()) return null;
        LocalDate localDate = LocalDate.now(clock);
        String dateKey = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // String.hashCode es estable entre ejecuciones, versiones de Java y zonas horarias.
        int chosen = Math.floorMod(dateKey.hashCode(), eligible.size());
        return new Selection(eligible.get(chosen), dateKey,
                localDate.format(DateTimeFormatter.ofPattern("d MMM", new Locale("es", "PE"))),
                excerpt(eligible.get(chosen).text));
    }

    @NonNull
    static String excerpt(@NonNull String articleText) {
        int firstLineEnd = articleText.indexOf('\n');
        // La línea Artículo N.- título es encabezado, no parte de la vista previa.
        String body = firstLineEnd >= 0
                ? articleText.substring(firstLineEnd + 1) : articleText;
        body = body.replaceAll("\\s+", " ").trim();
        if (body.length() <= MAX_EXCERPT_CHARS) return body;
        int allowed = MAX_EXCERPT_CHARS - 1; // deja sitio al carácter de elipsis
        int boundary = body.lastIndexOf(' ', allowed);
        return body.substring(0, boundary > 0 ? boundary : allowed).trim() + "…";
    }

    public static final class Selection {
        @NonNull public final Article article;
        @NonNull public final String localDate;
        @NonNull public final String dateLabel;
        @NonNull public final String excerpt;

        private Selection(Article article, String dateKey, String dateLabel, String excerpt) {
            this.article = article;
            this.localDate = dateKey;
            this.dateLabel = dateLabel;
            this.excerpt = excerpt;
        }
    }
}
