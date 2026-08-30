package com.jobalistudios.codigoprocesalcivilpe.referencias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class ArticleCrossReferenceParserTest {
    private final ArticleCrossReferenceParser parser = new ArticleCrossReferenceParser();

    @Test
    public void singular_preservesExactRangeAndRawText() {
        String text = "Conforme a lo dispuesto en el artículo 424 de este Código.";

        List<ArticleCrossReference> result = parser.parse(text);

        assertEquals(1, result.size());
        assertEquals("424", result.get(0).articleNumber);
        assertEquals("artículo 424", result.get(0).rawText);
        assertEquals(result.get(0).rawText,
                text.substring(result.get(0).start, result.get(0).end));
        assertFalse(result.get(0).externalContext);
    }

    @Test
    public void caseInsensitiveAndAlphanumeric_areRecognized() {
        String text = "Según el Artículo 424, el ARTÍCULO 425 y el artículo 506-a.";

        List<ArticleCrossReference> result = parser.parse(text);

        assertEquals(3, result.size());
        assertEquals("424", result.get(0).articleNumber);
        assertEquals("425", result.get(1).articleNumber);
        assertEquals("506-A", result.get(2).articleNumber);
    }

    @Test
    public void pluralList_createsIndependentNumberRanges() {
        String text = "Se aplican los artículos 424, 425 y 426 de este Código.";

        List<ArticleCrossReference> result = parser.parse(text);

        assertEquals(3, result.size());
        assertEquals("424", result.get(0).rawText);
        assertEquals("425", result.get(1).rawText);
        assertEquals("426", result.get(2).rawText);
        for (ArticleCrossReference reference : result) {
            assertEquals(reference.rawText, text.substring(reference.start, reference.end));
        }
    }

    @Test
    public void header_isNotAReference() {
        assertTrue(parser.parse("Artículo 424.- Requisitos de la demanda\n\nTexto.").isEmpty());
        assertTrue(parser.parse("Artículo 647 A.- Secuestro\n\nTexto.").isEmpty());
    }

    @Test
    public void realExternalQualifiers_areClassifiedConservatively() {
        assertTrue(parser.parse("artículo 139 de la Constitución").get(0).externalContext);
        assertTrue(parser.parse("artículo 10 del Código Civil").get(0).externalContext);
        assertTrue(parser.parse("artículo 5 de la Ley 32377").get(0).externalContext);
        assertTrue(parser.parse("artículo 7 del Decreto Legislativo 1070").get(0).externalContext);
        assertFalse(parser.parse("artículo 23 de este Código").get(0).externalContext);
        assertFalse(parser.parse("artículo 326 del Código Procesal Civil").get(0).externalContext);
        List<ArticleCrossReference> mixed = parser.parse(
                "artículo 5 de la Ley y artículo 424 de este Código"
        );
        assertTrue(mixed.get(0).externalContext);
        assertFalse(mixed.get(1).externalContext);
    }

    @Test
    public void numberPrefixesAndRangeEndpoints_keepOriginalCharacters() {
        String text = "Los artículos Nº 93 a 95 y el artículo N° 506-A son aplicables.";

        List<ArticleCrossReference> result = parser.parse(text);

        assertEquals(3, result.size());
        assertEquals("93", result.get(0).articleNumber);
        assertEquals("95", result.get(1).articleNumber);
        assertEquals("506-A", result.get(2).articleNumber);
        for (ArticleCrossReference reference : result) {
            assertEquals(reference.rawText, text.substring(reference.start, reference.end));
        }
    }
}
