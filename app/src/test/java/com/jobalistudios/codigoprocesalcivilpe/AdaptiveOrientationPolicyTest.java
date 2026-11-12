package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import android.content.pm.ActivityInfo;

import org.junit.Test;

public class AdaptiveOrientationPolicyTest {

    @Test
    public void compactPhoneRemainsPortrait() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                AdaptiveOrientationPolicy.orientationFor(393, false));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                AdaptiveOrientationPolicy.orientationFor(599, false));
    }

    @Test
    public void tabletAndFoldableCanRotate() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                AdaptiveOrientationPolicy.orientationFor(600, false));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                AdaptiveOrientationPolicy.orientationFor(840, false));
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                AdaptiveOrientationPolicy.orientationFor(1280, false));
    }

    @Test
    public void multiWindowDoesNotRequestPortraitEvenWhenCompact() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
                AdaptiveOrientationPolicy.orientationFor(480, true));
    }
}
