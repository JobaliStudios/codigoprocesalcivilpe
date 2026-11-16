package com.jobalistudios.codigoprocesalcivilpe.glosario;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import android.text.TextPaint;
import android.view.View;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class GlossaryMatcherTest {
    private List<GlossaryRepository.Term> fixture() {
        return GlossaryRepository.parseJson(GlossaryRepositoryTest.FIXTURE);
    }

    @Test public void caseDiacriticsAliasesAndFirstOccurrence() {
        List<GlossaryMatcher.Match> matches = GlossaryMatcher.find(
                "DEBIDO PROCESO y debido proceso; Garantía procesal.",
                fixture(), (start,end) -> true);
        assertEquals(1, matches.size());
        assertEquals(0, matches.get(0).start);
        assertEquals("debido-proceso", matches.get(0).term.getId());

        List<GlossaryMatcher.Match> alias = GlossaryMatcher.find(
                "Garantia procesal; garantía procesal", fixture(), (s,e) -> true);
        assertEquals(1, alias.size());
        assertEquals(0, alias.get(0).start);
    }

    @Test public void wordBoundaryRejectsPartialWordAndCombiningCharacter() {
        assertTrue(GlossaryMatcher.find("Indebidoproceso",fixture(), (s,e)->true).isEmpty());
        assertTrue(GlossaryMatcher.find("debidoprocesos",fixture(), (s,e)->true).isEmpty());
        assertEquals(1, GlossaryMatcher.find("El debido proceso.",
                fixture(), (s,e)->true).size());
    }

    @Test public void longestMatchWinsOverNestedTermAndNoDuplicatePerArticle() {
        String extended = GlossaryRepositoryTest.FIXTURE.replace("]}",
                ",{\"id\":\"proceso\",\"term\":\"proceso\",\"aliases\":[],"
                + "\"definition\":\"Prueba\",\"articles\":[\"I\"],\"related\":[],"
                + "\"source\":\"Test\",\"reviewedBy\":\"Test\",\"reviewedAt\":\"2026-11-16\"}]}");
        List<GlossaryRepository.Term> terms = GlossaryRepository.parseJson(extended);
        assertEquals(2,terms.size());
        List<GlossaryMatcher.Match> result = GlossaryMatcher.find(
                "debido proceso, proceso, proceso", terms, (s,e)->true);
        assertEquals(2, result.size());
        assertEquals(0,result.get(0).start);
        assertEquals("debido-proceso", result.get(0).term.getId());
        assertEquals("proceso",result.get(1).term.getId());
        assertTrue(result.get(1).start > result.get(0).end);
    }

    @Test public void exclusionAllowsNextValidMatchWithoutCrossingProtectedRanges() {
        List<GlossaryMatcher.Match> matches = GlossaryMatcher.find(
                "debido proceso; debido proceso", fixture(), (start,end) -> start >= 14);
        assertEquals(1, matches.size());
        assertTrue(matches.get(0).start > 14);
    }

    @Test public void emptyTermsNeverTouchMatcherAndInvalidRangesAreSafe() {
        assertTrue(GlossaryMatcher.find("Debido proceso", java.util.Collections.emptyList(),
                (a,b)->{ throw new AssertionError("No debe recorrer spans"); }).isEmpty());
    }

    @Test public void spanIsAccessibleAndUsesThemedUnderline() {
        Context c = ApplicationProvider.getApplicationContext();
        AtomicReference<String> chosen = new AtomicReference<>();
        GlossaryTermSpan span = new GlossaryTermSpan("debido-proceso", 0xFF6B081D,
                chosen::set);
        span.onClick(new View(c));
        assertEquals("debido-proceso",chosen.get());
        TextPaint paint = new TextPaint();
        span.updateDrawState(paint);
        assertEquals(0xFF6B081D,paint.getColor());
        assertEquals(0xFF6B081D,paint.underlineColor);
        assertTrue(paint.underlineThickness > 0);
    }
}
