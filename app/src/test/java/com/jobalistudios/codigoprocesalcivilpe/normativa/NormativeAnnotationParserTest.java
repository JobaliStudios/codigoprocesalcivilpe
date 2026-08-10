package com.jobalistudios.codigoprocesalcivilpe.normativa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class NormativeAnnotationParserTest {

    private final NormativeAnnotationParser parser = new NormativeAnnotationParser();

    @Test
    public void modification_extractsInstrumentDateAndExactOffsets() {
        String text = "Artículo 561.- Caso real\n\nContenido.\n\n"
                + "* Numeral 9 incorporado por la Ley 32266, publicada el 22 de marzo de 2025.";

        NormativeAnnotation annotation = parser.parse(text).get(0);

        assertEquals(NormativeAnnotation.Type.INCORPORATED, annotation.type);
        assertEquals("Ley 32266", annotation.legalInstrument);
        assertEquals("22 de marzo de 2025", annotation.publicationDate);
        assertEquals("Ley 32266 · 22 mar. 2025", annotation.compactSummary());
        assertEquals(text.substring(annotation.start, annotation.end), annotation.rawText);
    }

    @Test
    public void anotherLaw_extractsOnlyDataPresentInText() {
        String text = "* Artículo modificado por la Ley 32377, publicada el 7 de junio de 2025.";

        NormativeAnnotation annotation = parser.parse(text).get(0);

        assertEquals("Ley 32377", annotation.legalInstrument);
        assertEquals("7 de junio de 2025", annotation.publicationDate);
        assertEquals("Ley 32377 · 7 jun. 2025", annotation.compactSummary());
    }

    @Test
    public void annotationWithoutDate_doesNotInventOne() {
        NormativeAnnotation annotation = parser.parse(
                "* Artículo sustituido por el Decreto Legislativo N° 1069."
        ).get(0);

        assertEquals(NormativeAnnotation.Type.SUBSTITUTED, annotation.type);
        assertEquals("Decreto Legislativo N° 1069", annotation.legalInstrument);
        assertNull(annotation.publicationDate);
        assertEquals("Decreto Legislativo N° 1069", annotation.compactSummary());
    }

    @Test
    public void commonTextAndUnrelatedAsterisk_areNotAnnotations() {
        String text = "Esta Ley regula el proceso.\n\n* Nota editorial sin estado normativo.";

        assertTrue(parser.parse(text).isEmpty());
    }

    @Test
    public void severalNotes_areIndependentAndOrdered() {
        String first = "* Artículo modificado por la Ley 32266.";
        String second = "* Sumilla modificada por la Ley 32377.";
        String text = first + "\n\nTexto intermedio.\n\n" + second;

        List<NormativeAnnotation> annotations = parser.parse(text);

        assertEquals(2, annotations.size());
        assertEquals(first, annotations.get(0).rawText);
        assertEquals(second, annotations.get(1).rawText);
        assertTrue(annotations.get(0).start < annotations.get(1).start);
    }

    @Test
    public void multilineAnnotation_preservesEveryDeviceAndStopsAtNextParagraph() {
        String note = "* Artículo modificado por los siguientes dispositivos:\n"
                + "1. Ley 26668, publicada el 3 de octubre de 1996.\n"
                + "2. Ley 32377, publicada el 7 de junio de 2025.";
        String text = "Contenido.\n\n" + note + "\n\nArtículo siguiente.";

        NormativeAnnotation annotation = parser.parse(text).get(0);

        assertEquals(note, annotation.rawText);
        assertEquals(note, text.substring(annotation.start, annotation.end));
        assertEquals("Ley 32377", annotation.legalInstrument);
        assertEquals("7 de junio de 2025", annotation.publicationDate);
    }

    @Test
    public void inlineExplicitAnnotation_isDetectedWithoutTakingEarlierBodyText() {
        String note = "* Sumilla y artículo modificados por el D-L 25940, publicado el 11 de diciembre de 1992.";
        String text = "El auto es apelable. " + note;

        NormativeAnnotation annotation = parser.parse(text).get(0);

        assertEquals(note, annotation.rawText);
        assertEquals(text.indexOf('*'), annotation.start);
        assertEquals("D-L 25940", annotation.legalInstrument);
    }
}
