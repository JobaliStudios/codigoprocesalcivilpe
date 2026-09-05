package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuizQuestionSelectorTest {
    private QuestionModel error;
    private QuestionModel favorite;
    private QuestionModel general;
    private List<QuestionModel> allQuestions;

    @Before
    public void setUp() {
        error = question("Error", "Artículo 10");
        favorite = question("Favorito", "Artículo 20");
        general = question("General", "Artículo 30");
        allQuestions = Arrays.asList(error, favorite, general);
    }

    @Test
    public void quickReviewUsesErrorsThenFavoritesThenGeneralWithoutDuplicates() {
        Set<String> errors = new HashSet<>(Arrays.asList(
                QuizQuestionIdentity.forQuestion(error), QuizQuestionIdentity.forQuestion(favorite)
        ));
        QuizQuestionSelector selector = selector(
                Collections.singleton("20"), errors, Collections.emptyMap()
        );

        QuizSelection selection = selector.select(new QuizSessionConfig(
                QuizPracticeMode.QUICK_REVIEW, null, 10, false, Collections.emptyList()
        ));

        assertEquals(Arrays.asList(error, favorite, general), selection.getQuestions());
    }

    @Test
    public void favoritesUsesOnlyQuestionsForCurrentFavoriteArticles() {
        QuizQuestionSelector selector = selector(
                Collections.singleton("20"), Collections.emptySet(), Collections.emptyMap()
        );

        QuizSelection selection = selector.select(new QuizSessionConfig(
                QuizPracticeMode.FAVORITES, null, 10, false, Collections.emptyList()
        ));

        assertEquals(Collections.singletonList(favorite), selection.getQuestions());
    }

    @Test
    public void previousErrorsUsesOnlyStoredQuestionsAndRespectsSessionSubset() {
        Set<String> errors = new HashSet<>(Arrays.asList(
                QuizQuestionIdentity.forQuestion(error), QuizQuestionIdentity.forQuestion(favorite)
        ));
        QuizQuestionSelector selector = selector(Collections.emptySet(), errors, Collections.emptyMap());

        QuizSelection selection = selector.select(new QuizSessionConfig(
                QuizPracticeMode.PREVIOUS_ERRORS,
                null,
                10,
                false,
                Collections.singletonList(QuizQuestionIdentity.forQuestion(favorite))
        ));

        assertEquals(Collections.singletonList(favorite), selection.getQuestions());
    }

    @Test
    public void sectionUsesOnlyQuestionsResolvedToRequestedSection() {
        QuizQuestionSelector selector = selector(
                Collections.emptySet(), Collections.emptySet(),
                java.util.Map.of(error, "sec_1", favorite, "sec_2", general, "sec_1")
        );

        QuizSelection selection = selector.select(new QuizSessionConfig(
                QuizPracticeMode.SECTION, "sec_1", 10, false, Collections.emptyList()
        ));

        assertEquals(Arrays.asList(error, general), selection.getQuestions());
    }

    @Test
    public void emptyFavoritesAndSmallPoolReturnTypedStatesWithoutDuplicatingQuestions() {
        QuizQuestionSelector noFavorites = selector(Collections.emptySet(), Collections.emptySet(), Collections.emptyMap());
        assertEquals(QuizSelection.EmptyReason.NO_FAVORITES,
                noFavorites.select(QuizSessionConfig.favorites()).getEmptyReason());

        QuizQuestionSelector smallPool = new QuizQuestionSelector(
                Arrays.asList(error, favorite), Collections.singleton("10"), Collections.emptySet(),
                new Random(0), ignored -> null
        );
        QuizSelection selection = smallPool.select(new QuizSessionConfig(
                QuizPracticeMode.ALL, null, 10, false, Collections.emptyList()
        ));
        assertEquals(2, selection.getQuestions().size());
        assertEquals(2, new HashSet<>(selection.getQuestions()).size());
        assertTrue(selection.isAvailable());
    }

    private QuizQuestionSelector selector(
            Set<String> favorites,
            Set<String> errors,
            java.util.Map<QuestionModel, String> sections
    ) {
        return new QuizQuestionSelector(allQuestions, favorites, errors, new Random(0), sections::get);
    }

    private QuestionModel question(String text, String article) {
        return new QuestionModel(text, Arrays.asList("A", "B"), 0, article);
    }
}
