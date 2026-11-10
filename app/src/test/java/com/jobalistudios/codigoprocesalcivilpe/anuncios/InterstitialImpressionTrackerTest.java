package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterstitialImpressionTrackerTest {
    @Test
    public void attemptWithoutImpressionDoesNotConsumeLimitOrStartCooldown() {
        InterstitialFrequencyPolicy policy = eligiblePolicy();
        new InterstitialImpressionTracker(policy); // Un intento fallido no confirma impresión.

        assertTrue(policy.canShow(2_000L));
    }

    @Test
    public void confirmedImpressionStartsCooldownAndRequiresMoreNavigations() {
        InterstitialFrequencyPolicy policy = eligiblePolicy();
        InterstitialImpressionTracker tracker = new InterstitialImpressionTracker(policy);

        tracker.onImpressionConfirmed(1_000L);
        assertFalse(policy.canShow(1_001L));
        for (int i = 0; i < InterstitialFrequencyPolicy.NAVIGATIONS_BEFORE_FIRST_AD; i++) {
            policy.recordNavigation();
        }
        assertFalse(policy.canShow(1_000L + InterstitialFrequencyPolicy.MIN_INTERVAL_MS - 1));
        assertTrue(policy.canShow(1_000L + InterstitialFrequencyPolicy.MIN_INTERVAL_MS));
    }

    @Test
    public void duplicateConfirmationCountsAsOnlyOneImpression() {
        InterstitialFrequencyPolicy policy = eligiblePolicy();
        InterstitialImpressionTracker tracker = new InterstitialImpressionTracker(policy);
        tracker.onImpressionConfirmed(1_000L);
        tracker.onImpressionConfirmed(2_000L);
        for (int i = 0; i < InterstitialFrequencyPolicy.NAVIGATIONS_BEFORE_FIRST_AD; i++) {
            policy.recordNavigation();
        }
        assertTrue(policy.canShow(1_000L + InterstitialFrequencyPolicy.MIN_INTERVAL_MS));
    }

    private static InterstitialFrequencyPolicy eligiblePolicy() {
        InterstitialFrequencyPolicy policy = new InterstitialFrequencyPolicy();
        for (int i = 0; i < InterstitialFrequencyPolicy.NAVIGATIONS_BEFORE_FIRST_AD; i++) {
            policy.recordNavigation();
        }
        return policy;
    }
}
