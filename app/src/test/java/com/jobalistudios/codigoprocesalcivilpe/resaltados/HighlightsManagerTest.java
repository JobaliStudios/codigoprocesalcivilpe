package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HighlightsManagerTest {

    private static final String BLOCK = "seccionprimeratit1txt";

    private HighlightsManager manager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        manager = new HighlightsManager(context);
    }

    private static Highlight highlight(String id, int start, int end, String note, String snippet) {
        return new Highlight(id, start, end, "green", note, snippet, 123L);
    }

    @Test
    public void addAndGet_roundTripPreservesAllFields() {
        manager.add(BLOCK, highlight("h1", 5, 12, "mi nota", "Derecho"));

        List<Highlight> stored = manager.getForBlock(BLOCK);
        assertEquals(1, stored.size());
        Highlight h = stored.get(0);
        assertEquals("h1", h.getId());
        assertEquals(5, h.getStart());
        assertEquals(12, h.getEnd());
        assertEquals("green", h.getColorTag());
        assertEquals("mi nota", h.getNote());
        assertEquals("Derecho", h.getSnippet());
        assertEquals(123L, h.getCreatedAt());
        assertTrue(h.hasNote());
    }

    @Test
    public void highlightWithoutNote_hasNoteIsFalse() {
        manager.add(BLOCK, highlight("h1", 0, 4, null, "Todo"));
        manager.add(BLOCK, highlight("h2", 5, 9, "", "pers"));

        List<Highlight> stored = manager.getForBlock(BLOCK);
        assertFalse(stored.get(0).hasNote());
        assertFalse(stored.get(1).hasNote());
    }

    @Test
    public void update_replacesHighlightById() {
        manager.add(BLOCK, highlight("h1", 5, 12, "original", "Derecho"));
        manager.update(BLOCK, new Highlight("h1", 5, 12, "blue", "editada", "Derecho", 123L));

        List<Highlight> stored = manager.getForBlock(BLOCK);
        assertEquals(1, stored.size());
        assertEquals("editada", stored.get(0).getNote());
        assertEquals("blue", stored.get(0).getColorTag());
    }

    @Test
    public void remove_deletesOnlyTheGivenHighlight() {
        manager.add(BLOCK, highlight("h1", 0, 4, null, "Todo"));
        manager.add(BLOCK, highlight("h2", 5, 9, null, "pers"));

        manager.remove(BLOCK, "h1");

        List<Highlight> stored = manager.getForBlock(BLOCK);
        assertEquals(1, stored.size());
        assertEquals("h2", stored.get(0).getId());
    }

    @Test
    public void find_returnsHighlightOrNull() {
        manager.add(BLOCK, highlight("h1", 0, 4, null, "Todo"));

        assertEquals("h1", manager.find(BLOCK, "h1").getId());
        assertNull(manager.find(BLOCK, "inexistente"));
    }

    @Test
    public void blocksAreIsolatedFromEachOther() {
        manager.add("bloqueA", highlight("h1", 0, 4, null, "Todo"));

        assertTrue(manager.getForBlock("bloqueB").isEmpty());
    }

    @Test
    public void resolveRange_usesStoredOffsetsWhenSnippetStillMatches() {
        String content = "Artículo 1.- Derecho a la tutela";
        Highlight h = highlight("h1", 13, 20, null, "Derecho");

        assertArrayEquals(new int[]{13, 20}, HighlightsManager.resolveRange(h, content));
    }

    @Test
    public void resolveRange_relocatesWhenTextShifted() {
        // El texto ganó un prefijo: los offsets guardados ya no calzan, pero el fragmento sí existe.
        String content = "(Texto nuevo) Artículo 1.- Derecho a la tutela";
        Highlight h = highlight("h1", 13, 20, null, "Derecho");

        assertArrayEquals(new int[]{27, 34}, HighlightsManager.resolveRange(h, content));
    }

    @Test
    public void resolveRange_returnsNullWhenSnippetDisappeared() {
        Highlight h = highlight("h1", 13, 20, null, "Derecho");

        assertNull(HighlightsManager.resolveRange(h, "Texto totalmente distinto"));
    }

    @Test
    public void corruptStorage_returnsEmptyListInsteadOfCrashing() {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("codigoprocesalcivil_highlights", Context.MODE_PRIVATE)
                .edit().putString(BLOCK, "{esto no es json valido").apply();

        assertTrue(manager.getForBlock(BLOCK).isEmpty());
    }
}
