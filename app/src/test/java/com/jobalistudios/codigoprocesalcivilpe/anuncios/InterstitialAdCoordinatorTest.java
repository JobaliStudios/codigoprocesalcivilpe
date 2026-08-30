package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterstitialAdCoordinatorTest {
    private static final String PRODUCTION_ID = "ca-app-pub-6018202881039088/1234567890";

    @Test
    public void debugAlwaysUsesOfficialGoogleTestInterstitial() {
        assertEquals(
                InterstitialAdCoordinator.GOOGLE_TEST_INTERSTITIAL_AD_UNIT_ID,
                InterstitialAdCoordinator.resolveAdUnitId(true, PRODUCTION_ID)
        );
        assertEquals(PRODUCTION_ID,
                InterstitialAdCoordinator.resolveAdUnitId(false, PRODUCTION_ID));
    }

    @Test
    public void rejectsPlaceholderOrWrongFormat() {
        assertFalse(InterstitialAdCoordinator.isUsableProductionId("pending"));
        assertFalse(InterstitialAdCoordinator.isUsableProductionId(
                "ca-app-pub-6018202881039088~9210656478"));
        assertTrue(InterstitialAdCoordinator.isUsableProductionId(PRODUCTION_ID));
    }
}
