package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterstitialFrequencyPolicyTest {
    @Test
    public void requiresEightNavigationsAndTenMinuteCooldown() {
        InterstitialFrequencyPolicy policy = new InterstitialFrequencyPolicy();
        for (int index = 0; index < 7; index++) {
            policy.recordNavigation();
        }
        assertFalse(policy.canShow(1_000L));
        policy.recordNavigation();
        assertTrue(policy.canShow(1_000L));

        policy.recordShown(1_000L);
        for (int index = 0; index < 8; index++) {
            policy.recordNavigation();
        }
        assertFalse(policy.canShow(1_000L + InterstitialFrequencyPolicy.MIN_INTERVAL_MS - 1));
        assertTrue(policy.canShow(1_000L + InterstitialFrequencyPolicy.MIN_INTERVAL_MS));
    }

    @Test
    public void limitsSessionToTwoAds() {
        InterstitialFrequencyPolicy policy = new InterstitialFrequencyPolicy();
        long now = 10_000L;
        for (int shown = 0; shown < 2; shown++) {
            for (int index = 0; index < 8; index++) {
                policy.recordNavigation();
            }
            assertTrue(policy.canShow(now));
            policy.recordShown(now);
            now += InterstitialFrequencyPolicy.MIN_INTERVAL_MS;
        }
        for (int index = 0; index < 8; index++) {
            policy.recordNavigation();
        }
        assertFalse(policy.canShow(now));
        assertEquals(2, InterstitialFrequencyPolicy.MAX_ADS_PER_SESSION);
    }
}
