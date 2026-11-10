package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AdRequestGenerationTest {
    @Test public void consentChange_rejectsOldLoadButAcceptsNewLoad() {
        AdRequestGeneration generation = new AdRequestGeneration();
        long oldRequest = generation.current();
        generation.invalidate();
        assertFalse(generation.isCurrent(oldRequest));
        assertTrue(generation.isCurrent(generation.current()));
    }
}
