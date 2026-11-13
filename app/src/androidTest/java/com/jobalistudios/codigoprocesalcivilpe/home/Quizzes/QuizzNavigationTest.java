package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Pruebas de navegación reales adaptadas a la confirmación de salida de Fase 5/Misión 3. */
@RunWith(AndroidJUnit4.class)
public class QuizzNavigationTest {

    @Before
    public void clearOldDraft() {
        new QuizDraftStore(ApplicationProvider.getApplicationContext()).clear();
    }

    @After
    public void clearTestDraft() {
        new QuizDraftStore(ApplicationProvider.getApplicationContext()).clear();
    }

    @Test
    public void backFromQuestionsPromptsAndDiscardReturnsToStartScreen() {
        try (ActivityScenario<QuizzCPCStartScreen> scenario =
                     ActivityScenario.launch(QuizzCPCStartScreen.class)) {
            onView(withId(R.id.btnQuickReview)).perform(scrollTo(), click());
            onView(withId(R.id.tvQuestion)).check(matches(isDisplayed()));

            // No debe salir en silencio: el usuario decide conservar o descartar.
            pressBack();
            onView(withText(R.string.quiz_draft_exit_title)).check(matches(isDisplayed()));
            onView(withText(R.string.quiz_draft_discard)).perform(click());

            onView(withId(R.id.btnQuickReview)).perform(scrollTo())
                    .check(matches(isDisplayed()));
            onView(withId(R.id.cardQuizAll)).perform(scrollTo())
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void backFromQuestionsSaveAllowsResume() {
        try (ActivityScenario<QuizzCPCStartScreen> scenario =
                     ActivityScenario.launch(QuizzCPCStartScreen.class)) {
            onView(withId(R.id.btnQuickReview)).perform(scrollTo(), click());
            onView(withId(R.id.tvQuestion)).check(matches(isDisplayed()));

            pressBack();
            onView(withText(R.string.quiz_draft_exit_title)).check(matches(isDisplayed()));
            onView(withText(R.string.quiz_draft_save_exit)).perform(click());

            onView(withId(R.id.btnQuizResumeDraft)).check(matches(isDisplayed()));
            onView(withId(R.id.btnQuizResumeDraft)).perform(click());
            onView(withId(R.id.tvQuestion)).check(matches(isDisplayed()));
        }
    }
}
