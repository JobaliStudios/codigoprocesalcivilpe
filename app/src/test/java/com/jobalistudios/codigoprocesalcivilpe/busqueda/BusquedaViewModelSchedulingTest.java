package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class BusquedaViewModelSchedulingTest {
    private RecordingExecutor background;
    private BusquedaViewModel viewModel;

    @Before
    public void setUp() {
        Application application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(BusquedaViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        background = new RecordingExecutor();
        viewModel = new BusquedaViewModel(application, background,
                new Handler(Looper.getMainLooper()));
    }

    @After
    public void tearDown() {
        viewModel.onCleared();
    }

    @Test
    public void indexPreloadIsQueuedWithoutBlockingFirstFrame() {
        assertEquals(1, background.queued());
        assertEquals(BusquedaViewModel.EmptyState.PROMPT, state().emptyState);
        assertFalse(state().isLoading);
    }

    @Test
    public void typingDebounces275msAndOnlySearchesLastQuery() {
        viewModel.updateQuery("em");
        assertTrue(state().isLoading);
        tick(150);
        assertEquals(1, background.queued()); // Solo el precálculo del índice

        viewModel.updateQuery("embargo");
        tick(274);
        assertEquals(1, background.queued());

        tick(1);
        assertEquals(2, background.queued());
        background.runAll();
        ShadowLooper.idleMainLooper();

        assertEquals("embargo", state().query);
        assertFalse(state().isLoading);
        assertEquals("642", state().results.get(0).item.article.number);
    }

    @Test
    public void staleResponseNeverOverridesNewerQuery() {
        background.runNext(); // Carga el índice en el ejecutor de prueba.
        viewModel.updateQuery("embargo");
        tick(275);
        assertEquals(1, background.queued());

        background.runNext(); // Encola la publicación de "embargo" en el hilo UI.
        viewModel.updateQuery("competencia");
        ShadowLooper.idleMainLooper(); // La respuesta anterior queda descartada.
        assertTrue(state().isLoading);
        assertTrue(state().results.isEmpty());

        tick(275);
        background.runAll();
        ShadowLooper.idleMainLooper();
        assertEquals("competencia", state().query);
        assertEquals("396", state().results.get(0).item.article.number);
        assertFalse(state().isLoading);
    }

    @Test
    public void enterCancelsDebounceAndRunsImmediatelyWithoutDoubleDispatch() {
        viewModel.updateQuery("ejecucion");
        viewModel.submitCurrentQuery();

        assertEquals(2, background.queued()); // precálculo + búsqueda inmediata
        tick(500);
        assertEquals(2, background.queued()); // no quedó una consulta diferida
        background.runAll();
        ShadowLooper.idleMainLooper();

        assertEquals("ejecucion", state().recentQueries.get(0));
        assertFalse(state().isLoading);
        assertFalse(state().results.isEmpty());
    }

    @Test
    public void clearedQueryCannotShowObsoleteResults() {
        background.runNext();
        viewModel.updateQuery("competencia");
        tick(275);
        viewModel.updateQuery("");
        assertEquals(BusquedaViewModel.EmptyState.PROMPT, state().emptyState);
        assertFalse(state().isLoading);

        background.runAll();
        ShadowLooper.idleMainLooper();

        assertTrue(state().results.isEmpty());
        assertEquals("", state().query);
    }

    private BusquedaViewModel.BusquedaUiState state() {
        return viewModel.getUiState().getValue();
    }

    private void tick(long milliseconds) {
        org.robolectric.Shadows.shadowOf(Looper.getMainLooper())
                .idleFor(milliseconds, TimeUnit.MILLISECONDS);
    }

    private static final class RecordingExecutor implements Executor {
        private final ArrayDeque<Runnable> tasks = new ArrayDeque<>();

        @Override
        public void execute(Runnable command) {
            tasks.add(command);
        }

        int queued() {
            return tasks.size();
        }

        void runNext() {
            tasks.remove().run();
        }

        void runAll() {
            while (!tasks.isEmpty()) {
                runNext();
            }
        }
    }
}
