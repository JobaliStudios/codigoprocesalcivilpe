package com.jobalistudios.codigoprocesalcivilpe.referencias;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Deriva relacionados desde el texto del artículo, conservando el orden de aparición. */
public final class RelatedArticlesResolver {
    private final ArticleCrossReferenceResolver crossReferenceResolver;

    public RelatedArticlesResolver() {
        crossReferenceResolver = new ArticleCrossReferenceResolver();
    }

    @NonNull
    public List<ArticleNavigationResolver.Target> resolve(
            @NonNull Context context,
            @NonNull Article source
    ) {
        return uniqueTargets(crossReferenceResolver.resolveArticle(context, source));
    }

    @NonNull
    public List<ArticleNavigationResolver.Target> resolveText(
            @NonNull Context context,
            @NonNull String sourceArticleNumber,
            @NonNull String text
    ) {
        return uniqueTargets(crossReferenceResolver.resolveText(
                context,
                sourceArticleNumber,
                text
        ));
    }

    @NonNull
    private List<ArticleNavigationResolver.Target> uniqueTargets(
            List<ResolvedArticleCrossReference> references
    ) {
        Map<String, ArticleNavigationResolver.Target> unique = new LinkedHashMap<>();
        for (ResolvedArticleCrossReference reference : references) {
            unique.putIfAbsent(reference.target.getNumber(), reference.target);
        }
        return Collections.unmodifiableList(new ArrayList<>(unique.values()));
    }
}
