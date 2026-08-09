package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import java.util.List;

/** Resuelve un número de artículo al flujo de navegación de contenido ya existente. */
public final class ArticleNavigationResolver {

    private ArticleNavigationResolver() {
    }

    @Nullable
    public static Target resolve(@NonNull Context context, @Nullable String articleNumber) {
        if (articleNumber == null) {
            return null;
        }
        List<ArticleRepository.Location> locations =
                ArticleRepository.findArticle(context, articleNumber);
        if (locations.size() != 1) {
            return null;
        }

        ArticleRepository.Location location = locations.get(0);
        LegalContentCatalog.Entry entry = findCatalogEntry(context, location.block.key);
        return entry == null ? null : new Target(location.article, entry);
    }

    @Nullable
    private static LegalContentCatalog.Entry findCatalogEntry(Context context, String blockKey) {
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            String entryKey = context.getResources().getResourceEntryName(entry.textRes);
            if (blockKey.equals(entryKey)) {
                return entry;
            }
        }
        return null;
    }

    public static final class Target {
        private final Article article;
        private final LegalContentCatalog.Entry entry;

        private Target(Article article, LegalContentCatalog.Entry entry) {
            this.article = article;
            this.entry = entry;
        }

        @NonNull
        public String getNumber() {
            return article.number;
        }

        @NonNull
        public String getTitle() {
            return article.title;
        }

        public int getOffsetInBlock() {
            return article.offsetInBlock;
        }

        @NonNull
        public Intent createIntent(@NonNull Context context) {
            LegalHierarchyRepository.Node node =
                    LegalHierarchyRepository.findNodeByTextRes(entry.textRes);
            Intent intent = node != null
                    ? LegalHierarchyRepository.buildIntentForNode(context, node)
                    : SectionContentActivity.createIntent(context,
                    R.layout.activity_section_content, entry.textRes, entry.titleRes, entry.subtitleRes);
            return intent.putExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,
                    article.offsetInBlock);
        }
    }
}
