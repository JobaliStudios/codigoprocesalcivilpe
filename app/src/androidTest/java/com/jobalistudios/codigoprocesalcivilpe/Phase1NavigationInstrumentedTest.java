package com.jobalistudios.codigoprocesalcivilpe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizSessionContract;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCResult;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Phase1NavigationInstrumentedTest {
    @Test
    public void quizResults_areVisibleBeforeReturningToPracticeStart() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, QuizzCPCResult.class)
                .putExtra(QuizSessionContract.EXTRA_SCORE, 7)
                .putExtra(QuizSessionContract.EXTRA_TOTAL, 10);
        try (ActivityScenario<QuizzCPCResult> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvResult)).check(matches(isDisplayed()));
            onView(withId(R.id.btnBackToStart)).perform(scrollTo(), click());
            onView(withId(R.id.btnQuickReview)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void readerBackToIndex_opensTheArticlesParentIndex() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(context, "506-A");
        assertNotNull(target);
        Intent intent = target.createIntent(context);
        LegalHierarchyRepository.Node leaf = LegalHierarchyRepository.findNodeByTextRes(
                intent.getIntExtra(SectionContentActivity.EXTRA_TEXT_RES_ID, 0));
        assertNotNull(leaf);
        LegalHierarchyRepository.Node parent = LegalHierarchyRepository.findParentNode(leaf.id);
        assertNotNull(parent);
        try (ActivityScenario<SectionContentActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnBackToIndex)).check(matches(isDisplayed())).perform(click());
            onView(withId(R.id.sectionListTitle)).check(matches(withText(parent.title)));
            onView(withId(R.id.sectionRecyclerView)).check(matches(isDisplayed()));
        }
    }
}
