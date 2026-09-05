package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalContentCatalog;
import com.jobalistudios.codigoprocesalcivilpe.navigation.RelatedArticleNumberExtractor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resolves quiz questions to existing legal sections without relying on question prose. */
public final class QuizSectionCatalog {
    private QuizSectionCatalog() {
    }

    @Nullable
    public static String resolveSectionId(@NonNull Context context, @NonNull QuestionModel question) {
        String articleNumber = RelatedArticleNumberExtractor.extract(question.getRelatedArticle());
        if (articleNumber == null) {
            return null;
        }
        List<ArticleRepository.Location> locations = ArticleRepository.findArticle(context, articleNumber);
        if (locations.size() != 1) {
            return null;
        }
        LegalContentCatalog.Entry entry = findEntryForBlock(context, locations.get(0).block.key);
        return entry == null ? null : context.getResources().getResourceEntryName(entry.sectionNameRes);
    }

    @NonNull
    public static List<QuizSection> getAvailableSections(
            @NonNull Context context,
            @NonNull List<QuestionModel> questions
    ) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (QuestionModel question : questions) {
            String sectionId = resolveSectionId(context, question);
            if (sectionId != null) {
                counts.put(sectionId, counts.containsKey(sectionId) ? counts.get(sectionId) + 1 : 1);
            }
        }

        List<QuizSection> sections = new ArrayList<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            String id = context.getResources().getResourceEntryName(entry.sectionNameRes);
            Integer count = counts.remove(id);
            if (count != null && count > 0) {
                sections.add(new QuizSection(id, context.getString(entry.sectionNameRes), count));
            }
        }
        return sections;
    }

    @Nullable
    private static LegalContentCatalog.Entry findEntryForBlock(
            @NonNull Context context,
            @NonNull String blockKey
    ) {
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            String entryKey = context.getResources().getResourceEntryName(entry.textRes);
            if (blockKey.equals(entryKey)) {
                return entry;
            }
        }
        return null;
    }
}
