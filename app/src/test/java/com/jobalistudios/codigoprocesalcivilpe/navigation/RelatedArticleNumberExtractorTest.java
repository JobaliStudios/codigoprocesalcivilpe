package com.jobalistudios.codigoprocesalcivilpe.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RelatedArticleNumberExtractorTest {

    @Test
    public void modifiedNumericArticle_extractsNumber() {
        assertEquals("6", RelatedArticleNumberExtractor.extract(
                "Artículo 6 modificado por D-L 25940"
        ));
    }

    @Test
    public void alphanumericArticle_extractsCanonicalUppercaseNumber() {
        assertEquals("506-A", RelatedArticleNumberExtractor.extract("Artículo 506-A"));
    }

    @Test
    public void lowercaseArticleAndSuffix_areAccepted() {
        assertEquals("566-A", RelatedArticleNumberExtractor.extract(
                "artículo 566-a modificado por..."
        ));
    }

    @Test
    public void spacesAroundHyphen_areAccepted() {
        assertEquals("320-B", RelatedArticleNumberExtractor.extract("ARTICULO 320 - b"));
    }

    @Test
    public void textBeforeArticle_isAccepted() {
        assertEquals("17", RelatedArticleNumberExtractor.extract(
                "Consulta el Artículo 17 modificado"
        ));
    }

    @Test
    public void invalidRelatedText_returnsNull() {
        assertNull(RelatedArticleNumberExtractor.extract("Capítulo 6"));
        assertNull(RelatedArticleNumberExtractor.extract("Artículo sin número"));
    }

    @Test
    public void missingRelatedText_returnsNull() {
        assertNull(RelatedArticleNumberExtractor.extract(null));
        assertNull(RelatedArticleNumberExtractor.extract(""));
        assertNull(RelatedArticleNumberExtractor.extract("   "));
    }
}
