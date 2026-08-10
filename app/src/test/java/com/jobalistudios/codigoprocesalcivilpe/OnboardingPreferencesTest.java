package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OnboardingPreferencesTest {

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
    public void newUser_shouldShowOnboarding() {
        assertTrue(OnboardingPreferences.shouldShow(context));
    }

    @Test
    public void markCompleted_hidesOnboarding() {
        OnboardingPreferences.markCompleted(context);

        assertFalse(OnboardingPreferences.shouldShow(context));
    }

    @Test
    public void completedValue_remainsCompletedAcrossReads() {
        OnboardingPreferences.markCompleted(context);

        assertFalse(OnboardingPreferences.shouldShow(context));
        assertFalse(OnboardingPreferences.shouldShow(context));
    }
}
