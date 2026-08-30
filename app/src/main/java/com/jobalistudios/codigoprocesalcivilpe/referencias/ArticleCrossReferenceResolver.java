package com.jobalistudios.codigoprocesalcivilpe.referencias;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.VisibleArticleResolver;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Filtra falsos positivos y resuelve citas contra el índice local del CPC. */
public final class ArticleCrossReferenceResolver {
    private final ArticleCrossReferenceParser parser;

    public ArticleCrossReferenceResolver() {
        this(new ArticleCrossReferenceParser());
    }

    ArticleCrossReferenceResolver(@NonNull ArticleCrossReferenceParser parser) {
        this.parser = parser;
    }

    @NonNull
    public List<ResolvedArticleCrossReference> resolveBlock(
            @NonNull Context context,
            @NonNull ArticleBlock block
    ) {
        List<ResolvedArticleCrossReference> result = new ArrayList<>();
        for (ArticleCrossReference reference : parser.parse(block.fullText())) {
            Article source = VisibleArticleResolver.findAtOffset(block, reference.start);
            ResolvedArticleCrossReference resolved = resolveOne(
                    context,
                    reference,
                    source == null ? null : source.number
            );
            if (resolved != null) {
                result.add(resolved);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @NonNull
    public List<ResolvedArticleCrossReference> resolveArticle(
            @NonNull Context context,
            @NonNull Article source
    ) {
        List<ResolvedArticleCrossReference> result = new ArrayList<>();
        for (ArticleCrossReference reference : parser.parse(source.text)) {
            ResolvedArticleCrossReference resolved = resolveOne(
                    context,
                    reference,
                    source.number
            );
            if (resolved != null) {
                result.add(resolved);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Variante pura de modelo útil para validar textos sin construir objetos Android UI. */
    @NonNull
    public List<ResolvedArticleCrossReference> resolveText(
            @NonNull Context context,
            @Nullable String sourceArticleNumber,
            @NonNull String text
    ) {
        List<ResolvedArticleCrossReference> result = new ArrayList<>();
        for (ArticleCrossReference reference : parser.parse(text)) {
            ResolvedArticleCrossReference resolved = resolveOne(
                    context,
                    reference,
                    sourceArticleNumber
            );
            if (resolved != null) {
                result.add(resolved);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Nullable
    private ResolvedArticleCrossReference resolveOne(
            Context context,
            ArticleCrossReference reference,
            @Nullable String sourceArticleNumber
    ) {
        if (reference.externalContext) {
            return null;
        }
        String canonical = ArticleRepository.normalizeArticleNumber(reference.articleNumber);
        if (sourceArticleNumber != null
                && ArticleRepository.normalizeArticleNumber(sourceArticleNumber).equals(canonical)) {
            return null;
        }
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(context, canonical);
        return target == null ? null : new ResolvedArticleCrossReference(reference, target);
    }
}
