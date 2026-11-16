package com.jobalistudios.codigoprocesalcivilpe.glosario;

import static org.junit.Assert.*;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spannable;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightsManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GlossaryTextDecoratorTest {
    private Context context;
    private ArticleBlock block;

    @Before public void before() {
        context = ApplicationProvider.getApplicationContext();
        block = ArticleRepository.findArticle(context, "564").get(0).block;
        new HighlightsManager(context).clearAll();
    }

    private List<GlossaryRepository.Term> simulatedTerms() {
        return GlossaryRepository.parseJson(
                "{\"version\":1,\"terms\":[{\"id\":\"articulo\",\"term\":\"Artículo\","
                        + "\"aliases\":[],\"definition\":\"Prueba editorial\","
                        + "\"articles\":[\"564\"],\"related\":[],\"source\":\"Test\","
                        + "\"reviewedBy\":\"Test\",\"reviewedAt\":\"2026-11-16\"}]}");
    }

    @Test public void emptyGlossaryAddsNoSpansAndPreservesEveryCharacter() {
        SpannableString text = new SpannableString(block.fullText());
        GlossaryTextDecorator.apply(text, block, java.util.Collections.emptyList(),
                0xFF6B081D, id -> {});
        assertEquals(block.fullText(),text.toString());
        assertEquals(0, text.getSpans(0,text.length(),GlossaryTermSpan.class).length);
    }

    @Test public void existingLinkAndUserHighlightTakePriority() {
        SpannableString text = new SpannableString(block.fullText());
        int first = block.articles.get(0).offsetInBlock;
        int start = text.toString().indexOf("Artículo",first);
        assertTrue(start >= 0);
        int end = start + "Artículo".length();
        text.setSpan(new ArticleCrossReferenceSpan("424", 0xFF6B081D, id -> {}),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        new HighlightsManager(context).add(block.key, new Highlight(
                "highlight-glossary-test", start, end, "yellow", null,
                "Artículo", 1L));
        HighlightController controller = new HighlightController(
                context, new TextView(context), block.key, () -> {});
        controller.applyHighlights(text);
        assertEquals(1,text.getSpans(start,end,ArticleCrossReferenceSpan.class).length);
        assertEquals(1,text.getSpans(start,end,
                HighlightController.UserHighlightSpan.class).length);

        GlossaryTextDecorator.apply(text,block,simulatedTerms(),0xFF6B081D,id->{});
        assertEquals(block.fullText(),text.toString());
        assertEquals(0,text.getSpans(start,end,GlossaryTermSpan.class).length);
        assertEquals(1,text.getSpans(start,end,
                HighlightController.UserHighlightSpan.class).length);
    }

    @Test public void termAppearsOncePerArticleWithoutDuplicatingLegalText() {
        SpannableString text = new SpannableString(block.fullText());
        GlossaryTextDecorator.apply(text,block,simulatedTerms(),0xFF6B081D,id->{});
        GlossaryTermSpan[] spans = text.getSpans(
                0,text.length(),GlossaryTermSpan.class);
        assertTrue(spans.length > 0);
        assertTrue(spans.length <= block.articles.size());
        assertEquals(block.fullText(),text.toString());
    }
}
