package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HighlightsManagerTest {

    private static final String BLOCK = "seccionprimeratit1txt";

    private HighlightsManager manager;
    private SharedPreferences preferences;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        preferences = context.getSharedPreferences(
                "codigoprocesalcivil_highlights", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        manager = new HighlightsManager(context, new FakeNoteCipher());
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

        String raw = preferences.getString(BLOCK, "");
        assertFalse(raw.contains("mi nota"));
        assertFalse(raw.contains("\"note\""));
        assertTrue(raw.contains("noteCiphertext"));
        assertTrue(raw.contains("noteIv"));
        assertTrue(raw.contains("noteVersion"));
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

    @Test
    public void legacyPlaintextNote_isMigratedToEncryptedFormat() {
        preferences.edit().putString(BLOCK,
                "[{\"id\":\"legacy\",\"start\":0,\"end\":4,"
                        + "\"colorTag\":\"yellow\",\"note\":\"dato privado\","
                        + "\"snippet\":\"Todo\",\"createdAt\":123}]").commit();

        List<Highlight> stored = manager.getForBlock(BLOCK);

        assertEquals("dato privado", stored.get(0).getNote());
        String migrated = preferences.getString(BLOCK, "");
        assertFalse(migrated.contains("dato privado"));
        assertFalse(migrated.contains("\"note\""));
        assertTrue(migrated.contains("noteCiphertext"));
    }

    @Test
    public void noteLength_isLimitedToTwoThousandCharacters() {
        String oversized = new String(new char[2100]).replace('\0', 'x');

        manager.add(BLOCK, highlight("h1", 0, 4, oversized, "Todo"));

        assertEquals(Highlight.MAX_NOTE_LENGTH,
                manager.getForBlock(BLOCK).get(0).getNote().length());
    }

    @Test
    public void clearAll_removesEveryBlock() {
        manager.add("bloqueA", highlight("h1", 0, 4, "nota", "Todo"));
        manager.add("bloqueB", highlight("h2", 0, 4, null, "Todo"));

        manager.clearAll();

        assertTrue(manager.getForBlock("bloqueA").isEmpty());
        assertTrue(manager.getForBlock("bloqueB").isEmpty());
    }

    private static final class FakeNoteCipher implements NoteCipher {
        @Override
        public EncryptedNote encrypt(String highlightId, String plaintext) {
            String value = highlightId + ":" + plaintext;
            return new EncryptedNote(
                    Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)),
                    "test-iv");
        }

        @Override
        public String decrypt(String highlightId, EncryptedNote encryptedNote)
                throws GeneralSecurityException {
            String value = new String(Base64.getDecoder().decode(encryptedNote.ciphertext),
                    StandardCharsets.UTF_8);
            String prefix = highlightId + ":";
            if (!value.startsWith(prefix)) {
                throw new GeneralSecurityException("AAD inválido");
            }
            return value.substring(prefix.length());
        }
    }
}
