package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.navigation.RelatedArticleNumberExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/** Central, local-only selection policy for every practice mode. */
public final class QuizQuestionSelector {
    @NonNull private final List<QuestionModel> allQuestions;
    @NonNull private final Set<String> favoriteArticleNumbers;
    @NonNull private final Set<String> errorQuestionIds;
    @NonNull private final Random random;
    @NonNull private final Function<QuestionModel, String> sectionResolver;

    public QuizQuestionSelector(
            @NonNull List<QuestionModel> allQuestions,
            @NonNull Set<String> favoriteArticleNumbers,
            @NonNull Set<String> errorQuestionIds,
            @NonNull Random random,
            @NonNull Function<QuestionModel, String> sectionResolver
    ) {
        this.allQuestions = new ArrayList<>(allQuestions);
        this.favoriteArticleNumbers = new HashSet<>(favoriteArticleNumbers);
        this.errorQuestionIds = new HashSet<>(errorQuestionIds);
        this.random = random;
        this.sectionResolver = sectionResolver;
    }

    @NonNull
    public QuizSelection select(@NonNull QuizSessionConfig config) {
        switch (config.getMode()) {
            case FAVORITES:
                return favorites(config);
            case PREVIOUS_ERRORS:
                return previousErrors(config);
            case SECTION:
                return section(config);
            case QUICK_REVIEW:
                return quickReview(config);
            case ALL:
            default:
                return selected(allQuestions, config, QuizSelection.EmptyReason.NO_QUESTIONS);
        }
    }

    @NonNull
    private QuizSelection favorites(QuizSessionConfig config) {
        if (favoriteArticleNumbers.isEmpty()) {
            return new QuizSelection(Collections.emptyList(), QuizSelection.EmptyReason.NO_FAVORITES);
        }
        return selected(filterByFavorites(), config, QuizSelection.EmptyReason.NO_FAVORITE_QUESTIONS);
    }

    @NonNull
    private QuizSelection previousErrors(QuizSessionConfig config) {
        Set<String> ids = config.getSessionErrorIds().isEmpty()
                ? errorQuestionIds : new HashSet<>(config.getSessionErrorIds());
        return selected(filterByIds(ids), config, QuizSelection.EmptyReason.NO_PREVIOUS_ERRORS);
    }

    @NonNull
    private QuizSelection section(QuizSessionConfig config) {
        String sectionId = config.getSectionId();
        if (sectionId == null || sectionId.trim().isEmpty()) {
            return new QuizSelection(Collections.emptyList(), QuizSelection.EmptyReason.NO_SECTION_QUESTIONS);
        }
        List<QuestionModel> filtered = new ArrayList<>();
        for (QuestionModel question : allQuestions) {
            if (sectionId.equals(sectionResolver.apply(question))) {
                filtered.add(question);
            }
        }
        return selected(filtered, config, QuizSelection.EmptyReason.NO_SECTION_QUESTIONS);
    }

    @NonNull
    private QuizSelection quickReview(QuizSessionConfig config) {
        List<QuestionModel> picked = new ArrayList<>();
        Set<String> used = new LinkedHashSet<>();
        append(picked, used, filterByIds(errorQuestionIds), config);
        append(picked, used, filterByFavorites(), config);
        append(picked, used, allQuestions, config);
        return new QuizSelection(picked, picked.isEmpty()
                ? QuizSelection.EmptyReason.NO_QUESTIONS : QuizSelection.EmptyReason.NONE);
    }

    @NonNull
    private QuizSelection selected(
            List<QuestionModel> candidates,
            QuizSessionConfig config,
            QuizSelection.EmptyReason emptyReason
    ) {
        List<QuestionModel> picked = new ArrayList<>();
        append(picked, new LinkedHashSet<>(), candidates, config);
        return new QuizSelection(picked, picked.isEmpty() ? emptyReason : QuizSelection.EmptyReason.NONE);
    }

    private void append(
            List<QuestionModel> destination,
            Set<String> used,
            List<QuestionModel> candidates,
            QuizSessionConfig config
    ) {
        List<QuestionModel> shuffled = new ArrayList<>(candidates);
        if (config.shouldShuffle()) {
            Collections.shuffle(shuffled, random);
        }
        for (QuestionModel question : shuffled) {
            if (destination.size() >= config.getRequestedQuestionCount()) {
                return;
            }
            if (used.add(QuizQuestionIdentity.forQuestion(question))) {
                destination.add(question);
            }
        }
    }

    @NonNull
    private List<QuestionModel> filterByFavorites() {
        List<QuestionModel> filtered = new ArrayList<>();
        for (QuestionModel question : allQuestions) {
            String number = RelatedArticleNumberExtractor.extract(question.getRelatedArticle());
            if (number != null && favoriteArticleNumbers.contains(number)) {
                filtered.add(question);
            }
        }
        return filtered;
    }

    @NonNull
    private List<QuestionModel> filterByIds(@NonNull Set<String> ids) {
        List<QuestionModel> filtered = new ArrayList<>();
        for (QuestionModel question : allQuestions) {
            if (ids.contains(QuizQuestionIdentity.forQuestion(question))) {
                filtered.add(question);
            }
        }
        return filtered;
    }
}
