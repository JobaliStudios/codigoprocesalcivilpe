package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SearchTextNormalizerTest {

    @Test
    public void mapsAccentInsensitiveMatchBackToOriginalUtf16Range() {
        String original = "EJECUCIÓN y jurisdicción";

        SearchTextNormalizer.Range range = SearchTextNormalizer.findFirst(
                original, "ejecucion", 0);

        assertNotNull(range);
        assertEquals("EJECUCIÓN", original.substring(range.start, range.end));
    }

    @Test
    public void collapsedWhitespace_stillMapsToTheOriginalPhrase() {
        String original = "medida\n\n cautelar";

        SearchTextNormalizer.Range range = SearchTextNormalizer.findFirst(
                original, "medida cautelar", 0);

        assertNotNull(range);
        assertEquals(original, original.substring(range.start, range.end));
    }
}
