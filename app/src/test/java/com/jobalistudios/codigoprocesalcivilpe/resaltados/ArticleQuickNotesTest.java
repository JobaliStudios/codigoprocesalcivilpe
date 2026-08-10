package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ArticleQuickNotesTest {
    private static final String PREFS_NAME = "codigoprocesalcivil_highlights";

    private Context context;
    private SharedPreferences preferences;
    private HighlightsManager manager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        manager = new HighlightsManager(context, new FakeNoteCipher());
    }

    @Test
    public void articleWithoutQuickNote_createsHeaderBoundNote() {
        ArticleRepository.Location location = location("564");
        ArticleQuickNotes notes = notes(location);

        notes.save(location.article, "yellow", "Recordatorio");

        Highlight saved = notes.get(location.article);
        assertTrue(notes.has(location.article));
        assertEquals(ArticleQuickNotes.idFor("564"), saved.getId());
        assertEquals(location.article.offsetInBlock, saved.getStart());
        assertEquals(firstLine(location.article.text), saved.getSnippet());
        assertEquals(saved.getStart() + saved.getSnippet().length(), saved.getEnd());
        assertEquals("Recordatorio", saved.getNote());
    }

    @Test
    public void existingQuickNote_isEditedInPlace() {
        ArticleRepository.Location location = location("564");
        ArticleQuickNotes notes = notes(location);
        notes.save(location.article, "yellow", "Original");

        notes.save(location.article, "blue", "Editada");

        Highlight saved = notes.get(location.article);
        assertEquals("Editada", saved.getNote());
        assertEquals("blue", saved.getColorTag());
        assertEquals(1234L, saved.getCreatedAt());
    }

    @Test
    public void repeatedSave_doesNotCreateDuplicates() {
        ArticleRepository.Location location = location("564");
        ArticleQuickNotes notes = notes(location);

        notes.save(location.article, "yellow", "Uno");
        notes.save(location.article, "green", "Dos");

        assertEquals(1, manager.getForBlock(location.block.key).size());
    }

    @Test
    public void remove_deletesQuickNote() {
        ArticleRepository.Location location = location("564");
        ArticleQuickNotes notes = notes(location);
        notes.save(location.article, "yellow", "Nota");

        notes.remove(location.article);

        assertFalse(notes.has(location.article));
        assertNull(notes.get(location.article));
    }

    @Test
    public void alphanumericArticle_usesFullStringIdentifier() {
        ArticleRepository.Location location = location("506-A");
        ArticleQuickNotes notes = notes(location);

        notes.save(location.article, "orange", "Alfanumérica");

        assertEquals("article-quick-note:506-A", notes.get(location.article).getId());
    }

    @Test
    public void quickNote_isPersistedOnlyAsCiphertext() {
        ArticleRepository.Location location = location("564");
        ArticleQuickNotes notes = notes(location);

        notes.save(location.article, "yellow", "dato privado de prueba");

        String raw = preferences.getString(location.block.key, "");
        assertFalse(raw.contains("dato privado de prueba"));
        assertFalse(raw.contains("\"note\""));
        assertTrue(raw.contains("noteCiphertext"));
        assertEquals("dato privado de prueba", notes.get(location.article).getNote());
    }

    @Test
    public void manualSelectionNote_coexistsWithQuickNote() {
        ArticleRepository.Location location = location("564");
        Highlight manual = new Highlight(
                "manual-selection", location.article.offsetInBlock + 20,
                location.article.offsetInBlock + 27, "green", "Manual",
                location.article.text.substring(20, 27), 50L);
        manager.add(location.block.key, manual);

        ArticleQuickNotes notes = notes(location);
        notes.save(location.article, "yellow", "Rápida");

        List<Highlight> stored = manager.getForBlock(location.block.key);
        assertEquals(2, stored.size());
        assertEquals("Manual", manager.find(location.block.key, "manual-selection").getNote());
        assertEquals("Rápida", notes.get(location.article).getNote());
    }

    private ArticleQuickNotes notes(ArticleRepository.Location location) {
        return new ArticleQuickNotes(manager, location.block.key, () -> 1234L);
    }

    private ArticleRepository.Location location(String number) {
        return ArticleRepository.findArticle(context, number).get(0);
    }

    private String firstLine(String text) {
        int newline = text.indexOf('\n');
        return newline >= 0 ? text.substring(0, newline) : text;
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
