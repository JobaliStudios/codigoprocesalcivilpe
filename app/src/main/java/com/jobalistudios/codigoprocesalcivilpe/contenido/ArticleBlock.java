package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;

import java.util.List;

/** Bloque de contenido (título/capítulo/subcapítulo) segmentado por artículo. */
public final class ArticleBlock {

    /** Nombre del recurso string original (clave estable entre builds). */
    public final String key;
    /** Texto previo al primer artículo ("" si no hay). */
    public final String preamble;
    public final List<Article> articles;

    private volatile String fullText;

    ArticleBlock(@NonNull String key, @NonNull String preamble, @NonNull List<Article> articles) {
        this.key = key;
        this.preamble = preamble;
        this.articles = articles;
    }

    /** Texto completo del bloque; idéntico byte a byte al string compilado en resources. */
    @NonNull
    public String fullText() {
        String cached = fullText;
        if (cached == null) {
            StringBuilder builder = new StringBuilder(preamble);
            for (Article article : articles) {
                builder.append(article.text);
            }
            cached = builder.toString();
            fullText = cached;
        }
        return cached;
    }
}
