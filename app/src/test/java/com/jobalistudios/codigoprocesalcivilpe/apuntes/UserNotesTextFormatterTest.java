package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class UserNotesTextFormatterTest {
    private final UserNotesTextFormatter formatter =
            new UserNotesTextFormatter("Código Procesal Civil PE");

    @Test
    public void favoriteHighlightAndNotes_haveDeterministicProfessionalFormat() {
        ArticleNotesEntry entry = new ArticleNotesEntry(
                "564",
                "Acceso de oficio a información en línea",
                true,
                Collections.singletonList(new ArticleNotesEntry.HighlightEntry(
                        "El Juez puede acceder de oficio...",
                        "Revisar este supuesto para procesal.",
                        1,
                        20,
                        10L
                )),
                Collections.singletonList(new ArticleNotesEntry.NoteEntry(
                        "Comparar con jurisprudencia.",
                        20L
                ))
        );

        assertEquals(
                "MIS APUNTES\n"
                        + "Código Procesal Civil PE\n\n"
                        + "Artículo 564 — Acceso de oficio a información en línea\n\n"
                        + "Favorito\n\n"
                        + "Resaltado:\nEl Juez puede acceder de oficio...\n\n"
                        + "Mi nota:\nRevisar este supuesto para procesal.\n\n"
                        + "Nota del artículo:\nComparar con jurisprudencia.",
                formatter.format(Collections.singletonList(entry))
        );
    }

    @Test
    public void absentFields_neverProduceLiteralNull() {
        ArticleNotesEntry entry = new ArticleNotesEntry(
                "564", null, true, Collections.emptyList(), Collections.emptyList());

        String result = formatter.format(Collections.singletonList(entry));

        assertEquals("MIS APUNTES\nCódigo Procesal Civil PE\n\nArtículo 564\n\nFavorito",
                result);
        assertFalse(result.contains("null"));
    }

    @Test
    public void twoArticles_useClearSeparator() {
        ArticleNotesEntry first = favorite("506");
        ArticleNotesEntry second = favorite("506-A");

        String result = formatter.format(Arrays.asList(first, second));

        assertEquals(1, occurrences(result, "--------------------------------"));
    }

    @Test
    public void multilineNote_isPreservedExactly() {
        ArticleNotesEntry entry = new ArticleNotesEntry(
                "731", "Convocatoria", false, Collections.emptyList(),
                Collections.singletonList(new ArticleNotesEntry.NoteEntry(
                        "Primera línea\nSegunda línea", 1L)));

        String result = formatter.format(Collections.singletonList(entry));

        assertFalse(result.contains("Primera línea Segunda línea"));
        assertEquals(true, result.contains("Primera línea\nSegunda línea"));
    }

    @Test
    public void emptyInput_returnsEmptyText() {
        assertEquals("", formatter.format(Collections.emptyList()));
    }

    private ArticleNotesEntry favorite(String number) {
        return new ArticleNotesEntry(
                number, "", true, Collections.emptyList(), Collections.emptyList());
    }

    private int occurrences(String value, String needle) {
        return (value.length() - value.replace(needle, "").length()) / needle.length();
    }
}
