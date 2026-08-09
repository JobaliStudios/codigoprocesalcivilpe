package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArticleShareFormatterTest {
    private static final String FOOTER = "Código Procesal Civil PE";
    private ArticleShareFormatter formatter;

    @Before
    public void setUp() {
        formatter = new ArticleShareFormatter(FOOTER);
    }

    @Test
    public void normalArticle_startsWithCleanNumberAndTitle() {
        Article article = article(
                "564",
                "Acceso de oficio a información en línea",
                "Artículo 564.- Acceso de oficio a información en línea\n\nEl juez accede."
        );

        assertTrue(formatter.format(article).startsWith(
                "Artículo 564 — Acceso de oficio a información en línea\n\n"
        ));
    }

    @Test
    public void duplicatedInitialHeader_isIncludedOnlyOnce() {
        Article article = article(
                "564",
                "Acceso de oficio",
                "Artículo 564.- Acceso de oficio\n\nTexto completo."
        );

        String formatted = formatter.format(article);

        assertEquals(1, countOccurrences(formatted, "Artículo 564"));
        assertFalse(formatted.contains("Artículo 564.-"));
        assertTrue(formatted.contains("Texto completo."));
    }

    @Test
    public void alphanumericArticle_preservesCanonicalStringNumber() {
        Article article = article(
                "506-A",
                "Audiencia",
                "Artículo 506-A.- Audiencia\n\nContenido."
        );

        assertTrue(formatter.format(article).startsWith("Artículo 506-A — Audiencia"));
    }

    @Test
    public void emptyTitle_doesNotProduceDanglingDash() {
        Article article = article("123", "", "Artículo 123.-\n\nContenido.");

        String formatted = formatter.format(article);

        assertTrue(formatted.startsWith("Artículo 123\n\nContenido."));
        assertFalse(formatted.startsWith("Artículo 123 —"));
    }

    @Test
    public void repealedArticle_preservesStatusAndLegalNote() {
        Article article = article(
                "835",
                "[Derogado]",
                "Artículo 835.- [Derogado]\n\n"
                        + "* Artículo derogado por la Ley 32377, publicada el 7 de junio de 2025."
        );

        String formatted = formatter.format(article);

        assertTrue(formatted.startsWith("Artículo 835 — [Derogado]"));
        assertTrue(formatted.contains(
                "* Artículo derogado por la Ley 32377, publicada el 7 de junio de 2025."
        ));
    }

    @Test
    public void modificationNotes_areNotRemovedOrRewritten() {
        String note = "* Artículo modificado por el D-L 25940, publicado el 11 de diciembre de 1992.";
        Article article = article(
                "6",
                "Principio de legalidad",
                "Artículo 6.- Principio de legalidad\n\nTexto.\n\n" + note
        );

        assertTrue(formatter.format(article).contains("Texto.\n\n" + note));
    }

    @Test
    public void footer_isAlwaysLastAndSeparatedByBlankLine() {
        Article article = article("4", "Título", "Artículo 4.- Título\n\nCuerpo.");

        assertTrue(formatter.format(article).endsWith("Cuerpo.\n\n" + FOOTER));
    }

    @Test
    public void longBody_isReturnedCompletelyWithoutTruncation() {
        StringBuilder body = new StringBuilder();
        for (int index = 0; index < 2_000; index++) {
            body.append("Numeral ").append(index).append(": contenido jurídico.\n");
        }
        body.append("FIN DEL ARTÍCULO");
        Article article = article(
                "700",
                "Artículo extenso",
                "Artículo 700.- Artículo extenso\n\n" + body
        );

        String formatted = formatter.format(article);

        assertTrue(formatted.contains(body.toString()));
        assertTrue(formatted.contains("FIN DEL ARTÍCULO\n\n" + FOOTER));
    }

    private Article article(String number, String title, String text) {
        return new Article(number, title, text, 0);
    }

    private int countOccurrences(String text, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = text.indexOf(needle, offset)) >= 0) {
            count++;
            offset += needle.length();
        }
        return count;
    }
}
