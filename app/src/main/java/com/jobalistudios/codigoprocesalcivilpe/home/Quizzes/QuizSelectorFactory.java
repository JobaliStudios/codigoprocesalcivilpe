package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/** Builds selectors with the current local favorites and review-error state. */
final class QuizSelectorFactory {
    private QuizSelectorFactory() { }

    @NonNull static QuizQuestionSelector create(@NonNull Context context) {
        Set<String> favoriteNumbers = new HashSet<>();
        for (FavoriteItem item : new FavoritesManager(context).getAll()) {
            String destination = item.getDestinationId();
            if (destination.startsWith("article:")) {
                favoriteNumbers.add(ArticleRepository.normalizeArticleNumber(destination.substring(8)));
            }
        }
        return new QuizQuestionSelector(QuestionBank.getQuestions(context), favoriteNumbers,
                new QuizErrorStore(context).getQuestionIds(), new Random(),
                question -> QuizSectionCatalog.resolveSectionId(context, question));
    }
}
