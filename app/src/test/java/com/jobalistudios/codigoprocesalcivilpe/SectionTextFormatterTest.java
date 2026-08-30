package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeCalloutSpan;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceResolver;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SectionTextFormatterTest {

    @Test
    public void normalAndAlphanumericHeaders_areFormattedWithoutChangingText() {
        Context context = ApplicationProvider.getApplicationContext();
        String original = "Artículo 564.- Título normal\n\nTexto.\n\n"
                + "Artículo 506-A.- Título alfanumérico\n\nMás texto.\n\n"
                + "Artículo 647 A.- Variante real con espacio\n\nFin.";

        SpannableString formatted = SectionTextFormatter.buildFormattedText(context, original);

        assertEquals(original, formatted.toString());
        assertBoldAt(formatted, original.indexOf("Artículo 564"));
        assertBoldAt(formatted, original.indexOf("Artículo 506-A"));
        assertBoldAt(formatted, original.indexOf("Artículo 647 A"));
    }

    @Test
    public void normativeAnnotation_receivesCalloutWithoutChangingAnyCharacter() {
        Context context = ApplicationProvider.getApplicationContext();
        String note = "* Artículo modificado por la Ley 32377, publicada el 7 de junio de 2025.";
        String original = "Artículo 834.- Inclusión de otro heredero\n\nTexto normal.\n\n" + note;

        SpannableString formatted = SectionTextFormatter.buildFormattedText(context, original);

        assertEquals(original, formatted.toString());
        int noteStart = original.indexOf(note);
        NormativeCalloutSpan[] noteSpans = formatted.getSpans(
                noteStart,
                noteStart + note.length(),
                NormativeCalloutSpan.class
        );
        assertEquals(1, noteSpans.length);
        assertEquals(noteStart, formatted.getSpanStart(noteSpans[0]));
        assertEquals(noteStart + note.length(), formatted.getSpanEnd(noteSpans[0]));
        assertEquals("ACTUALIZACIÓN NORMATIVA", noteSpans[0].getTitle());
        assertEquals("Ley 32377 · 7 jun. 2025", noteSpans[0].getSummary());
        assertFalse(hasCalloutAt(formatted, original.indexOf("Texto normal")));
    }

    @Test
    public void crossReference_searchAndNormativeCallout_coexistWithoutChangingText() {
        Context context = ApplicationProvider.getApplicationContext();
        String original = "* Artículo modificado por la Ley 32377. Conforme al artículo 424 de este Código.";
        SpannableString formatted = SectionTextFormatter.buildFormattedText(context, original);
        SectionTextFormatter.applyArticleCrossReferenceFormatting(
                context,
                formatted,
                new ArticleCrossReferenceResolver().resolveText(context, "834", original),
                ignored -> { }
        );
        int referenceStart = original.indexOf("artículo 424");
        formatted.setSpan(
                new SectionSearchController.SearchHighlightSpan(0xFFFFFF00),
                referenceStart,
                referenceStart + "artículo 424".length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        assertEquals(original, formatted.toString());
        assertTrue(formatted.getSpans(
                referenceStart,
                referenceStart + 1,
                ArticleCrossReferenceSpan.class
        ).length > 0);
        assertTrue(formatted.getSpans(
                referenceStart,
                referenceStart + 1,
                SectionSearchController.SearchHighlightSpan.class
        ).length > 0);
        assertTrue(formatted.getSpans(
                referenceStart,
                referenceStart + 1,
                NormativeCalloutSpan.class
        ).length > 0);
    }

    private boolean hasCalloutAt(SpannableString text, int offset) {
        return text.getSpans(offset, offset + 1, NormativeCalloutSpan.class).length > 0;
    }

    private void assertBoldAt(SpannableString text, int offset) {
        StyleSpan[] spans = text.getSpans(offset, offset + 1, StyleSpan.class);
        boolean bold = false;
        for (StyleSpan span : spans) {
            bold |= span.getStyle() == Typeface.BOLD;
        }
        assertTrue(bold);
    }
}
