package com.jobalistudios.codigoprocesalcivilpe.normativa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NormativeSourceRegistryTest {

    private final NormativeSourceRegistry registry = new NormativeSourceRegistry();

    @Test
    public void registeredLaw_returnsOnlyRepositoryVerifiedUrl() {
        assertEquals(
                "https://busquedas.elperuano.pe/dispositivo/NL/2407453-7",
                registry.findOfficialSource("Ley N.º 32377")
        );
    }

    @Test
    public void unknownLaw_doesNotInferUrl() {
        assertNull(registry.findOfficialSource("Ley 99999"));
    }

    @Test
    public void missingInstrument_hasNoSource() {
        assertNull(registry.findOfficialSource(null));
    }
}
