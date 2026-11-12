package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * Sin migraciones: las tres preferencias conservan sus nombres y esquemas
 * existentes, y el contenido jurídico permanece separado de los datos privados.
 */
@RunWith(RobolectricTestRunner.class)
public class PersistenceCompatibilityRegressionTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        for (String name : new String[]{
                "codigoprocesalcivil_highlights",
                "codigoprocesalcivil_favorites",
                ReadingHistoryManager.PREFS_NAME}) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
                    .edit().clear().commit();
        }
    }

    @Test
    public void favoritesHistoryAndEncryptedNotesSurviveNewManagerInstances() {
        ArticleRepository.Location location = ArticleRepository.findArticle(context, "564").get(0);
        String articleNumber = location.article.number;
        String destination = FavoriteDestinationMapper.destinationForArticle(articleNumber);

        FavoritesManager favorites = new FavoritesManager(context);
        favorites.add(new FavoriteItem(destination, "Artículo 564", "",
                "Artículo", destination));
        ReadingHistoryManager history = new ReadingHistoryManager(context);
        history.recordArticle(articleNumber, location.article.title);
        HighlightsManager highlights = new HighlightsManager(context, new FakeCipher());
        ArticleQuickNotes note = new ArticleQuickNotes(highlights, location.block.key, () -> 999L);
        note.save(location.article, "yellow", "nota personal");

        // Se recrean instancias como después de salir del lector o volver a abrir la app.
        assertTrue(new FavoritesManager(context).isFavorite(destination));
        assertEquals(articleNumber,
                new ReadingHistoryManager(context).getLastArticle().getNumber());
        HighlightsManager reopenedHighlights = new HighlightsManager(context, new FakeCipher());
        assertEquals("nota personal",
                reopenedHighlights.find(location.block.key,
                        ArticleQuickNotes.idFor(articleNumber)).getNote());

        String rawNotes = context.getSharedPreferences("codigoprocesalcivil_highlights",
                        Context.MODE_PRIVATE).getString(location.block.key, "");
        assertTrue(rawNotes.contains("noteCiphertext"));
        assertFalse(rawNotes.contains("nota personal"));
        assertNotNull(ArticleRepository.getBlock(context, location.block.key));
    }

    @Test
    public void clearingReadingHistoryDoesNotRemoveFavoritesOrEncryptedNotes() {
        ArticleRepository.Location location = ArticleRepository.findArticle(context, "506-A").get(0);
        String destination = FavoriteDestinationMapper.destinationForArticle("506-A");
        new FavoritesManager(context).add(new FavoriteItem(
                destination, "Artículo 506-A", "", "Artículo", destination));

        ReadingHistoryManager history = new ReadingHistoryManager(context);
        history.recordArticle("506-A", location.article.title);
        HighlightsManager highlights = new HighlightsManager(context, new FakeCipher());
        new ArticleQuickNotes(highlights, location.block.key, () -> 123L)
                .save(location.article, "green", "conservar nota");

        history.clearHistory();

        assertTrue(history.getRecentArticles().isEmpty());
        assertTrue(new FavoritesManager(context).isFavorite(destination));
        assertEquals("conservar nota",
                new HighlightsManager(context, new FakeCipher())
                        .find(location.block.key, ArticleQuickNotes.idFor("506-A")).getNote());
    }

    private static final class FakeCipher implements NoteCipher {
        @Override
        public EncryptedNote encrypt(String id, String plaintext) {
            return new EncryptedNote(Base64.getEncoder().encodeToString(
                    (id + ":" + plaintext).getBytes(StandardCharsets.UTF_8)), "test-iv");
        }

        @Override
        public String decrypt(String id, EncryptedNote note)
                throws GeneralSecurityException {
            String value = new String(Base64.getDecoder().decode(note.ciphertext),
                    StandardCharsets.UTF_8);
            if (!value.startsWith(id + ":")) {
                throw new GeneralSecurityException("Identidad de nota no válida");
            }
            return value.substring((id + ":").length());
        }
    }
}
