package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.ArticleQuickNotes;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
public class ArticleUserContentResolverTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void articleWithoutHighlights_hasNoUserContent() {
        ArticleRepository.Location location = location("564");
        ArticleUserContentState state = resolver(Collections.emptyMap())
                .newSession().resolve(location.block, location.article);

        assertFalse(state.hasHighlight());
        assertFalse(state.hasNote());
    }

    @Test
    public void highlightWithoutNote_setsOnlyHighlight() {
        ArticleRepository.Location location = location("564");
        ArticleUserContentState state = resolver(mapFor(
                location,
                highlight(location, "manual", null)
        )).newSession().resolve(location.block, location.article);

        assertTrue(state.hasHighlight());
        assertFalse(state.hasNote());
    }

    @Test
    public void highlightWithNote_andQuickNote_setBothFlags() {
        ArticleRepository.Location location = location("564");
        Highlight manualNote = highlight(location, "manual-note", "Nota privada");
        Highlight quickNote = highlight(
                location,
                ArticleQuickNotes.idFor("564"),
                "Nota rápida privada"
        );

        ArticleUserContentState manualState = resolver(mapFor(location, manualNote))
                .newSession().resolve(location.block, location.article);
        ArticleUserContentState quickState = resolver(mapFor(location, quickNote))
                .newSession().resolve(location.block, location.article);

        assertTrue(manualState.hasHighlight());
        assertTrue(manualState.hasNote());
        assertTrue(quickState.hasHighlight());
        assertTrue(quickState.hasNote());
    }

    @Test
    public void highlightFromAnotherArticle_doesNotContaminateCurrentOne() {
        ArticleRepository.Location article564 = location("564");
        ArticleRepository.Location article565 = location("565");
        assertEquals(article564.block.key, article565.block.key);
        Map<String, List<Highlight>> data = mapFor(
                article564,
                highlight(article565, "other", "Nota privada")
        );

        ArticleUserContentState state = resolver(data).newSession()
                .resolve(article564.block, article564.article);

        assertFalse(state.hasHighlight());
        assertFalse(state.hasNote());
    }

    @Test
    public void shiftedHighlight_isRelocatedAndInvalidHighlightIsIgnored() {
        ArticleRepository.Location location = location("564");
        String header = header(location.article.text);
        Highlight shifted = new Highlight(
                "shifted", 0, header.length(), "yellow", "Nota privada", header, 1L);
        Highlight invalid = new Highlight(
                "invalid", -1, -1, "yellow", "No debe contar", "texto inexistente", 2L);
        Map<String, List<Highlight>> data = new HashMap<>();
        data.put(location.block.key, java.util.Arrays.asList(invalid, shifted));

        ArticleUserContentState state = resolver(data).newSession()
                .resolve(location.block, location.article);

        assertTrue(state.hasHighlight());
        assertTrue(state.hasNote());
    }

    @Test
    public void crossingHighlight_intersectsBothArticlesAndBlockIsReadOnce() {
        ArticleRepository.Location article564 = location("564");
        ArticleRepository.Location article565 = location("565");
        int boundary = article565.article.offsetInBlock;
        String content = article564.block.fullText();
        int start = boundary - 6;
        int end = boundary + 6;
        Highlight crossing = new Highlight(
                "crossing", start, end, "green", null,
                content.substring(start, end), 1L);
        AtomicInteger reads = new AtomicInteger();
        ArticleUserContentResolver resolver = new ArticleUserContentResolver(blockKey -> {
            reads.incrementAndGet();
            return Collections.singletonList(crossing);
        });
        ArticleUserContentResolver.Session session = resolver.newSession();

        assertTrue(session.resolve(article564.block, article564.article).hasHighlight());
        assertTrue(session.resolve(article565.block, article565.article).hasHighlight());
        assertEquals(1, reads.get());
    }

    private ArticleUserContentResolver resolver(Map<String, List<Highlight>> data) {
        return new ArticleUserContentResolver(
                blockKey -> data.getOrDefault(blockKey, Collections.emptyList())
        );
    }

    private Map<String, List<Highlight>> mapFor(
            ArticleRepository.Location location,
            Highlight highlight
    ) {
        Map<String, List<Highlight>> result = new HashMap<>();
        result.put(location.block.key, new ArrayList<>(Collections.singletonList(highlight)));
        return result;
    }

    private Highlight highlight(
            ArticleRepository.Location location,
            String id,
            String note
    ) {
        String snippet = header(location.article.text);
        int start = location.article.offsetInBlock;
        return new Highlight(id, start, start + snippet.length(), "yellow", note, snippet, 1L);
    }

    private String header(String text) {
        int newline = text.indexOf('\n');
        return newline < 0 ? text : text.substring(0, newline);
    }

    private ArticleRepository.Location location(String number) {
        return ArticleRepository.findArticle(context, number).get(0);
    }
}
