package com.jobalistudios.codigoprocesalcivilpe.anuncios;

/** Política en memoria: discreta, sin perfiles ni persistencia del comportamiento. */
final class InterstitialFrequencyPolicy {
    static final int NAVIGATIONS_BEFORE_FIRST_AD = 8;
    static final int MAX_ADS_PER_SESSION = 2;
    static final long MIN_INTERVAL_MS = 10L * 60L * 1000L;

    private int navigationCount;
    private int shownCount;
    private long lastShownAt = Long.MIN_VALUE;

    void recordNavigation() {
        navigationCount++;
    }

    boolean canShow(long nowMillis) {
        if (navigationCount < NAVIGATIONS_BEFORE_FIRST_AD
                || shownCount >= MAX_ADS_PER_SESSION) {
            return false;
        }
        return lastShownAt == Long.MIN_VALUE || nowMillis - lastShownAt >= MIN_INTERVAL_MS;
    }

    void recordShown(long nowMillis) {
        navigationCount = 0;
        shownCount++;
        lastShownAt = nowMillis;
    }
}
