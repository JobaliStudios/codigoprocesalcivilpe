package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import java.util.function.BooleanSupplier;

/** Ubicaciones explícitas; abrir contenido nunca es una pausa publicitaria. */
public enum InterstitialPlacement {
    ARTICLE_OPEN(false), QUIZ_RESULT_EXIT(true), READER_TO_INDEX(true);

    private final boolean naturalBreak;

    InterstitialPlacement(boolean naturalBreak) {
        this.naturalBreak = naturalBreak;
    }

    public boolean tryShow(BooleanSupplier gate) {
        return tryShow(true, gate);
    }

    public boolean tryShow(boolean canRequestAds, BooleanSupplier gate) {
        return naturalBreak && canRequestAds && gate.getAsBoolean();
    }
}
