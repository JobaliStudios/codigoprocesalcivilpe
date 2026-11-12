package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;

/**
 * La lectura de bloques con registros cifrados ilegibles nunca debe eliminar
 * ciphertext ni los demás resaltados por una escritura accidental.
 */
@RunWith(RobolectricTestRunner.class)
public class HighlightsManagerDataProtectionTest {
    private static final String PREFS = "codigoprocesalcivil_highlights";
    private static final String BLOCK = "seccionprimeratit1txt";

    private SharedPreferences preferences;
    private HighlightsManager manager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        manager = new HighlightsManager(context, new FakeNoteCipher());
    }

    @Test
    public void unreadableEncryptedNoteBlocksAddUpdateAndRemoveWithoutLosingOriginalBytes()
            throws JSONException {
        manager.add(BLOCK, highlight("h1", "nota importante"));
        manager.add(BLOCK, highlight("h2", "nota visible"));

        JSONArray stored = new JSONArray(preferences.getString(BLOCK, ""));
        JSONObject first = stored.getJSONObject(0);
        first.put("noteVersion", 99); // versión que la app no sabe descifrar
        preferences.edit().putString(BLOCK, stored.toString()).commit();
        String original = preferences.getString(BLOCK, "");

        List<Highlight> recovered = manager.getForBlock(BLOCK);
        assertEquals(2, recovered.size());
        assertNull(recovered.get(0).getNote());
        assertEquals("nota visible", recovered.get(1).getNote());

        manager.add(BLOCK, highlight("h3", "nueva nota"));
        assertEquals(original, preferences.getString(BLOCK, ""));
        manager.update(BLOCK, highlight("h2", "nota modificada"));
        assertEquals(original, preferences.getString(BLOCK, ""));
        manager.remove(BLOCK, "h2");
        assertEquals(original, preferences.getString(BLOCK, ""));

        // Cuando vuelve a ser compatible, la misma información se recupera.
        first.put("noteVersion", 1);
        preferences.edit().putString(BLOCK, stored.toString()).commit();
        assertEquals("nota importante", manager.find(BLOCK, "h1").getNote());
        manager.update(BLOCK, highlight("h2", "nota nueva"));
        assertEquals("nota nueva", manager.find(BLOCK, "h2").getNote());
    }

    @Test
    public void failedDecryptionAndLegacyPlaintextDoNotTriggerDestructiveMigration()
            throws JSONException {
        JSONObject legacy = highlight("legacy", "dato antiguo").toJson();
        legacy.put("note", "dato antiguo");

        manager.add(BLOCK, highlight("encrypted", "dato cifrado"));
        JSONArray stored = new JSONArray(preferences.getString(BLOCK, ""));
        stored.getJSONObject(0).put("noteVersion", 99);
        stored.put(legacy);
        preferences.edit().putString(BLOCK, stored.toString()).commit();
        String original = preferences.getString(BLOCK, "");

        List<Highlight> readable = manager.getForBlock(BLOCK);
        assertEquals(2, readable.size());
        assertNull(readable.get(0).getNote());
        assertEquals("dato antiguo", readable.get(1).getNote());
        assertEquals("La lectura no debe migrar un bloque incompleto",
                original, preferences.getString(BLOCK, ""));

        manager.remove(BLOCK, "legacy");
        assertEquals(original, preferences.getString(BLOCK, ""));
    }

    @Test
    public void invalidJsonIsNotReplacedWhenUserTriesToAddOrRemove() {
        String original = "{json incompleto con datos";
        preferences.edit().putString(BLOCK, original).commit();

        assertTrue(manager.getForBlock(BLOCK).isEmpty());
        manager.add(BLOCK, highlight("nuevo", "nueva"));
        manager.remove(BLOCK, "anterior");

        assertEquals(original, preferences.getString(BLOCK, ""));
    }

    @Test
    public void invalidRecordPreservesWholeBlockButValidRecordsRemainReadable()
            throws JSONException {
        manager.add(BLOCK, highlight("h1", "nota"));
        JSONArray stored = new JSONArray(preferences.getString(BLOCK, ""));
        stored.put(new JSONObject()); // no tiene ID, se preserva para una futura recuperación
        preferences.edit().putString(BLOCK, stored.toString()).commit();
        String original = preferences.getString(BLOCK, "");

        assertEquals(1, manager.getForBlock(BLOCK).size());
        assertEquals("nota", manager.getForBlock(BLOCK).get(0).getNote());
        manager.update(BLOCK, highlight("h1", "cambio"));
        assertEquals(original, preferences.getString(BLOCK, ""));
    }

    @Test
    public void healthyEncryptedBlockStillSupportsEditsAndNeverPersistsPlaintext() {
        manager.add(BLOCK, highlight("h1", "primera"));
        manager.update(BLOCK, highlight("h1", "segunda"));
        assertEquals("segunda", manager.find(BLOCK, "h1").getNote());
        String raw = preferences.getString(BLOCK, "");
        assertFalse(raw.contains("segunda"));
        assertTrue(raw.contains("noteCiphertext"));

        manager.remove(BLOCK, "h1");
        assertFalse(preferences.contains(BLOCK));
    }

    private static Highlight highlight(String id, String note) {
        return new Highlight(id, 0, 4, "green", note, "Todo", 123L);
    }

    private static final class FakeNoteCipher implements NoteCipher {
        @Override
        public EncryptedNote encrypt(String id, String note) {
            return new EncryptedNote(
                    Base64.getEncoder().encodeToString(
                            (id + ":" + note).getBytes(StandardCharsets.UTF_8)),
                    "test-iv");
        }

        @Override
        public String decrypt(String id, EncryptedNote encrypted)
                throws GeneralSecurityException {
            String decoded = new String(Base64.getDecoder().decode(
                    encrypted.ciphertext), StandardCharsets.UTF_8);
            if (!decoded.startsWith(id + ":")) {
                throw new GeneralSecurityException("Identidad de nota incorrecta");
            }
            return decoded.substring((id + ":").length());
        }
    }
}
