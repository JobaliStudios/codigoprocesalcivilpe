package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

/** Construye la navegación desde un resultado global hacia el artículo y coincidencia exactos. */
public final class ArticleSearchNavigation {
    private ArticleSearchNavigation() {
    }

    @Nullable
    public static Intent createIntent(
            @NonNull Context context,
            @NonNull ArticleSearchResult result,
            @NonNull String query
    ) {
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                context, result.item.article.number);
        if (target == null) {
            return null;
        }
        Intent intent = target.createIntent(context);
        int articleOffset = result.item.article.offsetInBlock;
        if (result.matchOffsetInArticle < 0) {
            return intent.putExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, articleOffset);
        }

        int matchOffsetInBlock = articleOffset + result.matchOffsetInArticle;
        return intent
                .putExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, matchOffsetInBlock)
                .putExtra(SectionContentActivity.EXTRA_INITIAL_QUERY, query.trim())
                .putExtra(SectionContentActivity.EXTRA_INITIAL_QUERY_OFFSET, matchOffsetInBlock);
    }
}
