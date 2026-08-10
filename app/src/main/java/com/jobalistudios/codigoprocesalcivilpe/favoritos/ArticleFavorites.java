package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;

/** Construye favoritos estables que apuntan a un artículo, no al nodo contenedor. */
public final class ArticleFavorites {
    private ArticleFavorites() {
    }

    @NonNull
    public static FavoriteItem itemForArticle(@NonNull Article article) {
        String destination = FavoriteDestinationMapper.destinationForArticle(article.number);
        return new FavoriteItem(
                destination,
                "Artículo " + article.number,
                article.title,
                "Artículo",
                destination
        );
    }
}
