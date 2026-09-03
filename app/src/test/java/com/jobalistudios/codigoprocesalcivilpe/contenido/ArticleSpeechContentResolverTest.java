package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.jobalistudios.codigoprocesalcivilpe.lectura.ArticleSpeechContent;
import com.jobalistudios.codigoprocesalcivilpe.lectura.ArticleSpeechContentResolver;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ArticleSpeechContentResolverTest {

    private ArticleBlock block;
    private ArticleSpeechContentResolver resolver;
    private String text563;
    private String text564;
    private String text565;

    @Before
    public void setUp() {
        String preamble = "CAPÍTULO DE PRUEBA\n\n";
        text563 = "Artículo 563.- Primero\n\nContenido 563.\n\n";
        text564 = "Artículo 564.- Acceso\n\nContenido exclusivo 564.\n\n";
        text565 = "Artículo 565.- Siguiente\n\nContenido 565.\n\n";
        Article first = new Article("563", "Primero", text563, preamble.length());
        Article middle = new Article(
                "564",
                "Acceso",
                text564,
                preamble.length() + text563.length()
        );
        Article last = new Article(
                "565",
                "Siguiente",
                text565,
                preamble.length() + text563.length() + text564.length()
        );
        block = new ArticleBlock("prueba", preamble, Arrays.asList(first, middle, last));
        resolver = new ArticleSpeechContentResolver();
    }

    @Test
    public void middleArticle_containsOnlyRequestedRange() {
        ArticleSpeechContent content = resolver.resolve(block, "564");

        assertNotNull(content);
        assertEquals("564", content.articleNumber);
        assertEquals(text564, content.rawText);
        assertFalse(content.rawText.contains("Artículo 565"));
    }

    @Test
    public void firstArticle_usesFirstAndSecondOffsets() {
        ArticleSpeechContent content = resolver.resolve(block, "563");

        assertNotNull(content);
        assertEquals(text563, content.rawText);
    }

    @Test
    public void lastArticle_usesEndOfBlock() {
        ArticleSpeechContent content = resolver.resolve(block, "565");

        assertNotNull(content);
        assertEquals(text565, content.rawText);
    }

    @Test
    public void alphanumericArticle_isResolvedWithoutIntegerParsing() {
        String raw = "Artículo 506-A.- Regla especial\n\nTexto.\n\n";
        Article alpha = new Article("506-A", "Regla especial", raw, 0);
        ArticleBlock alphaBlock = new ArticleBlock(
                "alfanumerico",
                "",
                java.util.Collections.singletonList(alpha)
        );

        ArticleSpeechContent content = resolver.resolve(alphaBlock, "506-a");

        assertNotNull(content);
        assertEquals("506-A", content.articleNumber);
        assertEquals(raw, content.rawText);
    }

    @Test
    public void unknownArticle_returnsNull() {
        assertNull(resolver.resolve(block, "999"));
    }
}
