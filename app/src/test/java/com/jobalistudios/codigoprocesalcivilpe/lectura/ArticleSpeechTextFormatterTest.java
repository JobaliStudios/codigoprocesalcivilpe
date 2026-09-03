package com.jobalistudios.codigoprocesalcivilpe.lectura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ArticleSpeechTextFormatterTest {

    @Test
    public void formatterImprovesDerogatedHeader_withoutMutatingRawText() {
        String raw = "Artículo 835.- [Derogado]\n\nNota normativa raw.";

        String formatted = new ArticleSpeechTextFormatter().format(raw);

        assertEquals("Artículo 835. Derogado.\n\nNota normativa raw.", formatted);
        assertEquals("Artículo 835.- [Derogado]\n\nNota normativa raw.", raw);
        assertFalse(formatted.contains("["));
    }

    @Test
    public void formatterDoesNotParaphraseLegalBody() {
        String raw = "Artículo 506-A.- Regla\n\nConforme al artículo 424, corresponde resolver.";

        String formatted = new ArticleSpeechTextFormatter().format(raw);

        assertEquals(
                "Artículo 506-A. Regla\n\nConforme al artículo 424, corresponde resolver.",
                formatted
        );
    }
}
