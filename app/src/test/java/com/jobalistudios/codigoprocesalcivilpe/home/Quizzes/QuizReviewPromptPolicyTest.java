package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.privacidad.LocalDataManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QuizReviewPromptPolicyTest {
    private Context context;
    private QuizReviewPromptPolicy policy;
    private static final long TODAY = 2_000_000_000_000L;

    @Before public void setup() {
        context = ApplicationProvider.getApplicationContext();
        policy = new QuizReviewPromptPolicy(context);
        policy.clear();
    }

    @After public void cleanup() {
        policy.clear();
    }

    @Test public void needsThreeCompletedSessionsRegardlessOfCorrectness() {
        assertFalse(policy.claimIfEligible(TODAY));
        policy.onSessionCompleted();
        policy.onSessionCompleted();
        assertFalse(policy.claimIfEligible(TODAY));
        policy.onSessionCompleted();

        assertTrue(policy.claimIfEligible(TODAY));
        assertFalse(policy.claimIfEligible(TODAY));
    }

    @Test public void minimumNinetyDaysAndAtMostTwoInvitations() {
        for (int i = 0; i < 3; i++) {
            policy.onSessionCompleted();
        }
        assertTrue(policy.claimIfEligible(TODAY));
        assertFalse(policy.claimIfEligible(TODAY + QuizReviewPromptPolicy.COOLDOWN_MS - 1));
        assertFalse(policy.claimIfEligible(TODAY - 10));
        assertTrue(policy.claimIfEligible(TODAY + QuizReviewPromptPolicy.COOLDOWN_MS));
        assertFalse(policy.claimIfEligible(TODAY + 3 * QuizReviewPromptPolicy.COOLDOWN_MS));
    }

    @Test public void neverAskAgainSuppressesEvenAfterCooldown() {
        for (int i = 0; i < 3; i++) {
            policy.onSessionCompleted();
        }
        policy.neverAskAgain();
        assertFalse(policy.claimIfEligible(TODAY));
        assertFalse(policy.claimIfEligible(TODAY + QuizReviewPromptPolicy.COOLDOWN_MS));
    }

    @Test public void clearingAllDataResetsOnlyLocalEligibilityAndNoMoreChoice() {
        for (int i = 0; i < 3; i++) {
            policy.onSessionCompleted();
        }
        policy.neverAskAgain();
        new LocalDataManager(context).clearAllLocalData();

        assertFalse(policy.claimIfEligible(TODAY));
        for (int i = 0; i < 3; i++) {
            policy.onSessionCompleted();
        }
        assertTrue(policy.claimIfEligible(TODAY));
    }
}
