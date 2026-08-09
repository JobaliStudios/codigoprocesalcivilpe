package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Selecciona un artículo de un bloque a partir de un offset de carácter visible. */
public final class VisibleArticleResolver {
    private VisibleArticleResolver() {
    }

    @Nullable
    public static Article findAtOffset(@NonNull ArticleBlock block, int characterOffset) {
        if (block.articles.isEmpty()) {
            return null;
        }

        Article visible = block.articles.get(0);
        for (Article article : block.articles) {
            if (article.offsetInBlock > characterOffset) {
                break;
            }
            visible = article;
        }
        return visible;
    }
}
