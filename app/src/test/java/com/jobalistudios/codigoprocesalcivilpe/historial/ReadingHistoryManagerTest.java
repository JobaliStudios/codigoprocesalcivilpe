package com.jobalistudios.codigoprocesalcivilpe.historial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(RobolectricTestRunner.class)
public class ReadingHistoryManagerTest {

    private Context context;
    private ReadingHistoryManager manager;
    private AtomicLong clock;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        clock = new AtomicLong(1_000L);
        manager = new ReadingHistoryManager(context, clock::getAndIncrement);
    }

    @Test
    public void recordArticle_createsHistory() {
        manager.recordArticle("731", "Convocatoria");

        List<RecentArticle> recent = manager.getRecentArticles();
        assertEquals(1, recent.size());
        assertEquals("731", recent.get(0).getNumber());
        assertEquals("Convocatoria", recent.get(0).getTitle());
        assertEquals(1_000L, recent.get(0).getLastViewedAt());
        assertEquals("731", new ReadingHistoryManager(context).getLastArticle().getNumber());
    }

    @Test
    public void lastArticle_isMostRecentlyRecorded() {
        manager.recordArticle("561", "Representación procesal");
        manager.recordArticle("731", "Convocatoria");

        assertEquals("731", manager.getLastArticle().getNumber());
    }

    @Test
    public void duplicateArticle_movesToFrontWithoutDuplicating() {
        manager.recordArticle("561", "Representación procesal");
        manager.recordArticle("731", "Convocatoria");
        manager.recordArticle("561", "Representación procesal");

        List<RecentArticle> recent = manager.getRecentArticles();
        assertEquals(2, recent.size());
        assertEquals("561", recent.get(0).getNumber());
        assertEquals("731", recent.get(1).getNumber());
        assertEquals(1_002L, recent.get(0).getLastViewedAt());
    }

    @Test
    public void history_neverStoresMoreThanFiveArticles() {
        for (int number = 1; number <= 7; number++) {
            manager.recordArticle(String.valueOf(number), "Título " + number);
        }

        List<RecentArticle> recent = manager.getRecentArticles();
        assertEquals(ReadingHistoryManager.MAX_RECENT_ARTICLES, recent.size());
        assertEquals("7", recent.get(0).getNumber());
        assertEquals("3", recent.get(4).getNumber());
    }

    @Test
    public void history_isOrderedFromNewestToOldest() {
        manager.recordArticle("561", "Representación procesal");
        manager.recordArticle("731", "Convocatoria");
        manager.recordArticle("759", "Intervención del Ministerio Público");

        List<RecentArticle> recent = manager.getRecentArticles();
        assertEquals("759", recent.get(0).getNumber());
        assertEquals("731", recent.get(1).getNumber());
        assertEquals("561", recent.get(2).getNumber());
    }

    @Test
    public void alphanumericArticle_isCanonicalizedAndPersisted() {
        manager.recordArticle(" 506-a ", "Emplazamiento excepcional");

        assertEquals("506-A", manager.getLastArticle().getNumber());
    }

    @Test
    public void clearHistory_removesLastAndRecentArticles() {
        manager.recordArticle("731", "Convocatoria");

        manager.clearHistory();

        assertNull(manager.getLastArticle());
        assertTrue(manager.getRecentArticles().isEmpty());
    }

    @Test
    public void invalidStoredEntry_doesNotCrashAndIsDiscarded() {
        context.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(ReadingHistoryManager.KEY_RECENT_ARTICLES, "{invalid json")
                .commit();

        assertTrue(manager.getRecentArticles().isEmpty());
        assertNull(manager.getLastArticle());

        manager.recordArticle("not-an-article", "Inválido");
        assertTrue(manager.getRecentArticles().isEmpty());
    }
}
