package com.jobalistudios.codigoprocesalcivilpe;

/** Evita navegar antes de que UMP termine y evita navegaciones duplicadas. */
final class SplashNavigationGate {
    private boolean consentCompleted;
    private boolean navigationStarted;

    void onConsentCompleted() {
        consentCompleted = true;
    }

    boolean tryNavigate(boolean activityUnavailable) {
        if (!consentCompleted || activityUnavailable || navigationStarted) {
            return false;
        }
        navigationStarted = true;
        return true;
    }
}
