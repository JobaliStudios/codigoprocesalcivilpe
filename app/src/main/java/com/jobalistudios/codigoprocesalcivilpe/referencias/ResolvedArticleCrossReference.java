package com.jobalistudios.codigoprocesalcivilpe.referencias;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

/** Referencia interna que existe exactamente una vez y puede abrirse en el lector. */
public final class ResolvedArticleCrossReference {
    @NonNull public final ArticleCrossReference reference;
    @NonNull public final ArticleNavigationResolver.Target target;

    ResolvedArticleCrossReference(
            @NonNull ArticleCrossReference reference,
            @NonNull ArticleNavigationResolver.Target target
    ) {
        this.reference = reference;
        this.target = target;
    }
}
