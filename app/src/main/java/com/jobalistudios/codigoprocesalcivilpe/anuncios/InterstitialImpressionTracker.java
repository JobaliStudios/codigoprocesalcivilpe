package com.jobalistudios.codigoprocesalcivilpe.anuncios;

/** Registra una impresión solo cuando el SDK confirma que el intersticial se mostró. */
final class InterstitialImpressionTracker {
    private final InterstitialFrequencyPolicy frequencyPolicy;
    private boolean confirmed;

    InterstitialImpressionTracker(InterstitialFrequencyPolicy frequencyPolicy) {
        this.frequencyPolicy = frequencyPolicy;
    }

    synchronized void onImpressionConfirmed(long timestampMillis) {
        if (confirmed) {
            return;
        }
        frequencyPolicy.recordShown(timestampMillis);
        confirmed = true;
    }
}
