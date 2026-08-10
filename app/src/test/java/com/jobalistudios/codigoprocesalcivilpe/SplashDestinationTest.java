package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SplashDestinationTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(OnboardingPreferences.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void pendingOnboarding_routesToOnboarding() {
        assertEquals(OnboardingActivity.class, SplashScreen.destinationFor(context));
    }

    @Test
    public void completedOnboarding_routesToMain() {
        OnboardingPreferences.markCompleted(context);

        assertEquals(MainActivity.class, SplashScreen.destinationFor(context));
    }
}
