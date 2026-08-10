package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SectionTextFormatterTest {

    @Test
    public void normalAndAlphanumericHeaders_areFormattedWithoutChangingText() {
        Context context = ApplicationProvider.getApplicationContext();
        String original = "Artículo 564.- Título normal\n\nTexto.\n\n"
                + "Artículo 506-A.- Título alfanumérico\n\nMás texto.";

        SpannableString formatted = SectionTextFormatter.buildFormattedText(context, original);

        assertEquals(original, formatted.toString());
        assertBoldAt(formatted, original.indexOf("Artículo 564"));
        assertBoldAt(formatted, original.indexOf("Artículo 506-A"));
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
