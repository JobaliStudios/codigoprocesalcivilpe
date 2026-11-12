package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.privacidad.LocalDataManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QuizLocalMetricsStoreTest {
    private Context context;
    private QuizLocalMetricsStore store;

    @Before public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        store = new QuizLocalMetricsStore(context);
        store.clear();
    }

    @After public void tearDown() {
        store.clear();
    }

    @Test public void disabledByDefaultAndDoesNotSaveCompletedSessions() {
        assertFalse(store.isEnabled());
        assertFalse(store.recordCompleted(2, 5));
        assertEquals(0L, store.snapshot().getSessions());
        assertTrue(context.getSharedPreferences("quiz_local_metrics", 0).getAll().isEmpty());
    }

    @Test public void optInAggregatesOnlyValidCompletedSessions() {
        store.setEnabled(true);
        assertTrue(store.recordCompleted(3, 5));
        assertTrue(store.recordCompleted(8, 10));
        assertFalse(store.recordCompleted(5, 4));
        assertFalse(store.recordCompleted(-1, 5));
        assertFalse(store.recordCompleted(2, 0));
        assertFalse(store.recordCompleted(120, 121));

        QuizLocalMetricsStore.Snapshot summary = store.snapshot();
        assertEquals(2L, summary.getSessions());
        assertEquals(15L, summary.getAnswered());
        assertEquals(11L, summary.getCorrect());
        assertEquals(73, summary.getAccuracyPercent());
    }

    @Test public void optingOutErasesOldCountersAndNewSessionsAreNotStored() {
        store.setEnabled(true);
        store.recordCompleted(2, 5);
        store.setEnabled(false);

        assertFalse(store.isEnabled());
        assertFalse(store.recordCompleted(5, 5));
        store.setEnabled(true);
        assertEquals(0L, store.snapshot().getSessions());
        assertEquals(0, store.snapshot().getAccuracyPercent());
    }

    @Test public void clearAllLocalDataRemovesConsentAndLocalMetrics() {
        store.setEnabled(true);
        store.recordCompleted(10, 10);
        new LocalDataManager(context).clearAllLocalData();

        assertFalse(store.isEnabled());
        assertEquals(0L, store.snapshot().getSessions());
        assertTrue(context.getSharedPreferences("quiz_local_metrics", 0).getAll().isEmpty());
    }
}
