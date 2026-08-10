package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SearchSnippetBuilderTest {
    private final SearchSnippetBuilder builder = new SearchSnippetBuilder();

    @Test
    public void queryAtStart_hasNoLeadingEllipsis() {
        SearchSnippetBuilder.Snippet snippet = builder.build(
                "Embargo sobre bienes determinados. " + repeatedText(), "embargo");

        assertTrue(snippet.text.startsWith("Embargo"));
        assertFalse(snippet.text.startsWith("…"));
        assertEquals(0, snippet.matchOffsetInSource);
    }

    @Test
    public void queryInMiddle_isCenteredWithContext() {
        String source = repeatedText() + " la medida de embargo recaerá sobre el bien "
                + repeatedText();

        SearchSnippetBuilder.Snippet snippet = builder.build(source, "embargo");

        assertTrue(snippet.text.startsWith("…"));
        assertTrue(snippet.text.endsWith("…"));
        assertTrue(SearchTextNormalizer.normalizePlain(snippet.text).contains("embargo"));
        assertTrue(snippet.text.length() <= SearchSnippetBuilder.MAX_LENGTH_WITHOUT_ELLIPSES + 2);
    }

    @Test
    public void queryAtEnd_hasNoTrailingEllipsis() {
        String source = repeatedText() + repeatedText() + " concluye con ejecución";

        SearchSnippetBuilder.Snippet snippet = builder.build(source, "ejecucion");

        assertTrue(snippet.text.startsWith("…"));
        assertFalse(snippet.text.endsWith("…"));
    }

    @Test
    public void matchIsNeverCutAndHighlightCoversWholePhrase() {
        String phrase = "medida cautelar especialmente extensa";
        String source = repeatedText() + phrase + repeatedText();

        SearchSnippetBuilder.Snippet snippet = builder.build(source, phrase);

        assertFalse(snippet.highlightRanges.isEmpty());
        SearchTextNormalizer.Range range = snippet.highlightRanges.get(0);
        assertEquals(phrase, snippet.text.substring(range.start, range.end));
    }

    @Test
    public void shortText_needsNoEllipsis() {
        String source = "La competencia corresponde al juez civil.";

        SearchSnippetBuilder.Snippet snippet = builder.build(source, "competencia");

        assertEquals(source, snippet.text);
        assertFalse(snippet.text.contains("…"));
    }

    @Test
    public void accentInsensitiveMatch_preservesOriginalAndExactRange() {
        String source = "La ejecución continuará hasta su conclusión.";

        SearchSnippetBuilder.Snippet snippet = builder.build(source, "ejecucion");

        assertTrue(snippet.text.contains("ejecución"));
        SearchTextNormalizer.Range range = snippet.highlightRanges.get(0);
        assertEquals("ejecución", snippet.text.substring(range.start, range.end));
        assertEquals(source.indexOf("ejecución"), snippet.matchOffsetInSource);
    }

    @Test
    public void minimumOffset_skipsAnEarlierOccurrence() {
        String source = "embargo inicial. Texto intermedio y embargo elegido al final.";
        int second = source.lastIndexOf("embargo");

        SearchSnippetBuilder.Snippet snippet = builder.build(source, "embargo", second);

        assertEquals(second, snippet.matchOffsetInSource);
    }

    private String repeatedText() {
        return "El órgano jurisdiccional evaluará los presupuestos procesales y resolverá conforme a ley. ";
    }
}
