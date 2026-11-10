package com.jobalistudios.codigoprocesalcivilpe.anuncios;

/** Descarta resultados de cargas iniciadas bajo opciones de privacidad anteriores. */
final class AdRequestGeneration {
    private long value;
    long current() { return value; }
    void invalidate() { value++; }
    boolean isCurrent(long request) { return request == value; }
}
