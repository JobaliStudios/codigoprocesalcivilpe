package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Asocia notas y resaltados persistentes con el rango vigente de cada artículo. */
public final class ArticleUserContentResolver {

    interface HighlightSource {
        @NonNull List<Highlight> getForBlock(@NonNull String blockKey);
    }

    private final HighlightSource highlightSource;

    public ArticleUserContentResolver(@NonNull Context context) {
        HighlightsManager manager = new HighlightsManager(context);
        highlightSource = manager::getForBlock;
    }

    ArticleUserContentResolver(@NonNull HighlightSource highlightSource) {
        this.highlightSource = highlightSource;
    }

    /** Crea una caché efímera: cada bloque se descifra como máximo una vez por refresco. */
    @NonNull
    public Session newSession() {
        return new Session();
    }

    public final class Session {
        private final Map<String, List<Highlight>> highlightsByBlock = new HashMap<>();

        private Session() {
        }

        @NonNull
        public ArticleUserContentState resolve(
                @NonNull ArticleBlock block,
                @NonNull Article article
        ) {
            int articleIndex = findArticleIndex(block, article);
            if (articleIndex < 0) {
                return ArticleUserContentState.EMPTY;
            }
            int articleStart = article.offsetInBlock;
            int articleEnd = articleIndex + 1 < block.articles.size()
                    ? block.articles.get(articleIndex + 1).offsetInBlock
                    : block.fullText().length();
            String content = block.fullText();
            List<Highlight> highlights = highlightsByBlock.computeIfAbsent(
                    block.key,
                    highlightSource::getForBlock
            );

            boolean hasHighlight = false;
            boolean hasNote = false;
            for (Highlight highlight : highlights) {
                int[] range = HighlightsManager.resolveRange(highlight, content);
                if (range == null || range[1] <= articleStart || range[0] >= articleEnd) {
                    continue;
                }
                hasHighlight = true;
                hasNote |= highlight.hasNote();
                if (hasNote) {
                    break;
                }
            }
            return hasHighlight
                    ? new ArticleUserContentState(hasNote, true)
                    : ArticleUserContentState.EMPTY;
        }

        private int findArticleIndex(ArticleBlock block, Article article) {
            for (int index = 0; index < block.articles.size(); index++) {
                Article candidate = block.articles.get(index);
                if (candidate == article
                        || (candidate.offsetInBlock == article.offsetInBlock
                        && candidate.number.equals(article.number))) {
                    return index;
                }
            }
            return -1;
        }
    }
}
