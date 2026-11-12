package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SplashNavigationGateTest {

    @Test
    public void doesNotNavigateBeforeConsentCompletes() {
        SplashNavigationGate gate = new SplashNavigationGate();

        assertFalse(gate.tryNavigate(false));
    }

    @Test
    public void navigatesImmediatelyAfterConsentCompletesAndOnlyOnce() {
        SplashNavigationGate gate = new SplashNavigationGate();

        gate.onConsentCompleted();

        assertTrue(gate.tryNavigate(false));
        assertFalse(gate.tryNavigate(false));
    }

    @Test
    public void doesNotNavigateWhenActivityIsFinishingOrDestroyed() {
        SplashNavigationGate gate = new SplashNavigationGate();
        gate.onConsentCompleted();

        assertFalse(gate.tryNavigate(true));
        assertTrue(gate.tryNavigate(false));
    }
}
