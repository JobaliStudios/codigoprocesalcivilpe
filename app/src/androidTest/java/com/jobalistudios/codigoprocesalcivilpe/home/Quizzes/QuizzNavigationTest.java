package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QuizzNavigationTest {

    @Test
    public void backFromQuestionsReturnsToStartScreen() {
        ActivityScenario.launch(QuizzCPCStartScreen.class);

        onView(withId(R.id.btn5Questions)).perform(click());
        onView(withId(R.id.btnNext)).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.btn10Questions)).check(matches(isDisplayed()));
    }
}
