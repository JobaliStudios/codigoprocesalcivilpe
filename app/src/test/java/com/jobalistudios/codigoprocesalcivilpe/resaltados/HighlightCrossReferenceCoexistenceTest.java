package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.text.SpannableString;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.SectionTextFormatter;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceResolver;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
public class HighlightCrossReferenceCoexistenceTest {
    @Test
    public void userHighlightPersistsAndShortTapGivesReferencePriority() {
        Context context = ApplicationProvider.getApplicationContext();
        String blockKey = "cross_reference_test";
        context.getSharedPreferences("codigoprocesalcivil_highlights", Context.MODE_PRIVATE)
                .edit().clear().commit();
        String original = "Conforme al artículo 424 de este Código.";
        int linkStart = original.indexOf("artículo 424");
        AtomicInteger opened = new AtomicInteger();

        SpannableString text = SectionTextFormatter.buildFormattedText(context, original);
        SectionTextFormatter.applyArticleCrossReferenceFormatting(
                context,
                text,
                new ArticleCrossReferenceResolver().resolveText(context, "330", original),
                ignored -> opened.incrementAndGet()
        );
        new HighlightsManager(context).add(blockKey, new Highlight(
                "highlight-link",
                0,
                linkStart + "artículo 424".length(),
                "yellow",
                null,
                original.substring(0, linkStart + "artículo 424".length()),
                1L
        ));
        TextView textView = new TextView(context);
        HighlightController controller = new HighlightController(
                context,
                textView,
                blockKey,
                () -> { }
        );
        controller.applyHighlights(text);
        textView.setText(text);

        assertEquals(original, text.toString());
        assertEquals(1, text.getSpans(
                linkStart,
                linkStart + 1,
                ArticleCrossReferenceSpan.class
        ).length);
        assertEquals(1, text.getSpans(
                linkStart,
                linkStart + 1,
                HighlightController.UserHighlightSpan.class
        ).length);
        assertTrue(HighlightController.openArticleReferenceAt(text, linkStart, textView));
        assertEquals(1, opened.get());
    }
}
