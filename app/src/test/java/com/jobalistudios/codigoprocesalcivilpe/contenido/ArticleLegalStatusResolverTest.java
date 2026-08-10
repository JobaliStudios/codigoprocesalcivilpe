package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArticleLegalStatusResolverTest {

    @Test
    public void explicitRepealedTitle_isRepealed() {
        Article article = new Article(
                "835",
                "[Derogado]",
                "Artículo 835.- [Derogado]",
                0
        );

        assertEquals(
                ArticleLegalStatusResolver.Status.REPEALED,
                ArticleLegalStatusResolver.resolve(article)
        );
    }

    @Test
    public void bodyMentionAlone_doesNotMarkArticleAsRepealed() {
        Article article = new Article(
                "836",
                "Ejecución",
                "Este texto menciona el artículo derogado sin derogar el actual.",
                0
        );

        assertEquals(
                ArticleLegalStatusResolver.Status.ACTIVE,
                ArticleLegalStatusResolver.resolve(article)
        );
    }
}
