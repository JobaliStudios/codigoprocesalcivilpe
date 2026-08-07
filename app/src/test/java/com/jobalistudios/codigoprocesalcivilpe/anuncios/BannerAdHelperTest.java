package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BannerAdHelperTest {

    private static final String PRODUCTION_AD_UNIT_ID =
            "ca-app-pub-6018202881039088/2334541370";

    @Test
    public void debugBuild_usesOfficialGoogleTestBanner() {
        assertEquals(
                BannerAdHelper.GOOGLE_TEST_BANNER_AD_UNIT_ID,
                BannerAdHelper.resolveAdUnitId(true, PRODUCTION_AD_UNIT_ID));
    }

    @Test
    public void releaseBuild_usesConfiguredProductionBanner() {
        assertEquals(
                PRODUCTION_AD_UNIT_ID,
                BannerAdHelper.resolveAdUnitId(false, PRODUCTION_AD_UNIT_ID));
    }
}
