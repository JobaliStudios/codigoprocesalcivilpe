package com.jobalistudios.codigoprocesalcivilpe.lectura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class ArticleSpeechChunkerTest {

    private final ArticleSpeechChunker chunker = new ArticleSpeechChunker();

    @Test
    public void longText_isSplitWithoutLossDuplicationOrReordering() {
        String text = "Primer párrafo con contenido suficiente.\n\n"
                + "Segundo párrafo con otra oración jurídica. "
                + "Tercera oración para forzar más de un fragmento.";

        List<ArticleSpeechChunker.Chunk> chunks = chunker.chunk(text, 48);
        StringBuilder rebuilt = new StringBuilder();
        for (ArticleSpeechChunker.Chunk chunk : chunks) {
            assertTrue(chunk.text.length() <= 48);
            assertEquals(rebuilt.length(), chunk.startOffset);
            rebuilt.append(chunk.text);
            assertEquals(rebuilt.length(), chunk.endOffset);
        }

        assertTrue(chunks.size() > 1);
        assertEquals(text, rebuilt.toString());
    }

    @Test
    public void boundariesPreferWhitespaceOrPunctuation_overSplittingWords() {
        String text = "Una oración corta. Otra oración con palabras separadas. Último tramo.";

        List<ArticleSpeechChunker.Chunk> chunks = chunker.chunk(text, 28);

        for (int index = 0; index + 1 < chunks.size(); index++) {
            char last = chunks.get(index).text.charAt(chunks.get(index).text.length() - 1);
            assertTrue(Character.isWhitespace(last) || last == '.' || last == '!' || last == '?');
        }
    }

    @Test
    public void resumeOffset_prefersNearbySentenceThenWordBoundary() {
        String text = "Primera oración completa. Segunda oración que continúa con más texto.";
        int insideSecondSentence = text.indexOf("continúa") + 3;

        int adjusted = chunker.adjustResumeOffset(text, insideSecondSentence);

        assertEquals(text.indexOf("Segunda"), adjusted);
        assertTrue(adjusted > 0);
        assertFalse(adjusted == insideSecondSentence);
    }
}
