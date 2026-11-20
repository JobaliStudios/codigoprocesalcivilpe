package com.jobalistudios.codigoprocesalcivilpe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Las acciones ejecutadas por Espresso disparan comprobaciones reales
 * mediante AccessibilityChecks. No equivale a una sesión de TalkBack humano.
 */
@RunWith(AndroidJUnit4.class)
public final class ReaderPremiumAccessibilityInstrumentedTest {
    @Before
    public void configureAccessibilityChecks() {
        AccessibilityChecks.enable().setRunChecksFromRootView(false);
    }

    @After
    public void restoreOtherInstrumentedTests() {
        // Esta validación es global en Espresso: no debe contaminar el flujo
        // del cuestionario ni las pruebas existentes de otras pantallas.
        AccessibilityChecks.disable();
    }

    @Test
    public void inicioBusquedaYConfiguracionHaveAccessibleActions() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.cardArticleOfTheDay)).perform(scrollTo())
                    .check(matches(isDisplayed()));
            onView(withId(R.id.buttonHomeNotes)).perform(scrollTo())
                    .check(matches(isDisplayed()));
            onView(withId(R.id.buttonHomeSearch)).perform(scrollTo(), click());
            onView(withId(R.id.searchView)).check(matches(isDisplayed()));

            onView(withId(R.id.navigation_configuracion)).perform(click());
            onView(withId(R.id.cardAppAppearance)).check(matches(isDisplayed()));
            onView(withId(R.id.configReaderThemeRow)).perform(scrollTo(), click());
            onView(withId(R.id.readerAuto)).perform(click());
            Espresso.pressBack();
            onView(withId(R.id.buttonReadingReset)).perform(scrollTo(), click());

            // El regreso a Inicio queda validado por MainNavigationInstrumentedTest;
            // esta prueba se centra en las acciones accesibles de Inicio y Ajustes.
        }
    }

    @Test
    public void lectorYHojasHaveAccessibleInteractiveControls() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(context, "564");
        assertNotNull(target);
        Intent intent = target.createIntent(context);
        try (ActivityScenario<SectionContentActivity> ignored =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnTamanoLetra)).perform(click());
            onView(withId(R.id.readerAuto)).perform(click());
            Espresso.pressBack();
            onView(withId(R.id.btnReaderChapterIndex)).perform(click());
            onView(withId(R.id.chapterIndexSearch)).check(matches(isDisplayed()));
            onView(withId(R.id.chapterIndexClose)).perform(click());
            onView(withId(R.id.btnNextArticle)).check(matches(isDisplayed()));
        }
    }
}
