package com.jobalistudios.codigoprocesalcivilpe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainNavigationInstrumentedTest {

    @Test
    public void bottomTabs_navigateAcrossMainDestinations() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.navigation_busqueda)).perform(click());
        onView(withId(R.id.text_busqueda)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_favoritos)).perform(click());
        onView(withId(R.id.recyclerFavoritos)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_configuracion)).perform(click());
        onView(withId(R.id.card_preferencias)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.card_codigos)).check(matches(isDisplayed()));
    }
}
