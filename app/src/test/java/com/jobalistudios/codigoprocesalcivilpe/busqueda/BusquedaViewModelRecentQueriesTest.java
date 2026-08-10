package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BusquedaViewModelRecentQueriesTest {
    private Application application;
    private BusquedaViewModel viewModel;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(BusquedaViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        new FavoritesManager(application).clearAll();
        viewModel = new BusquedaViewModel(application);
    }

    @Test
    public void submit_savesNonEmptyTrimmedQuery() {
        viewModel.updateQuery("  embargo  ");
        viewModel.submitCurrentQuery();

        assertEquals("embargo", state().recentQueries.get(0));
    }

    @Test
    public void typing_preservesTrailingSpaceForMultiWordQueries() {
        viewModel.updateQuery("medida ");

        assertEquals("medida ", state().query);
    }

    @Test
    public void recentQueries_areLimitedToEight() {
        for (int index = 0; index < 10; index++) {
            viewModel.updateQuery("consulta " + index);
            viewModel.submitCurrentQuery();
        }

        assertEquals(BusquedaViewModel.MAX_RECENT, state().recentQueries.size());
        assertEquals("consulta 9", state().recentQueries.get(0));
        assertEquals("consulta 2", state().recentQueries.get(7));
    }

    @Test
    public void semanticDuplicate_movesToFrontAndKeepsLatestSpelling() {
        submit("Embargo");
        submit("competencia");
        submit("embargo");

        assertEquals(2, state().recentQueries.size());
        assertEquals("embargo", state().recentQueries.get(0));
        assertEquals("competencia", state().recentQueries.get(1));
    }

    @Test
    public void accentVariants_doNotCreateDuplicate() {
        submit("ejecución");
        submit("EJECUCION");

        assertEquals(1, state().recentQueries.size());
        assertEquals("EJECUCION", state().recentQueries.get(0));
    }

    @Test
    public void removeRecent_deletesOnlySelectedQuery() {
        submit("embargo");
        submit("competencia");

        viewModel.removeRecentQuery("EMBARGO");

        assertEquals(1, state().recentQueries.size());
        assertEquals("competencia", state().recentQueries.get(0));
    }

    @Test
    public void clearAllAndPrivacyEntryPoint_removeSameStorage() {
        submit("embargo");
        viewModel.clearAllRecentQueries();
        assertTrue(state().recentQueries.isEmpty());

        submit("competencia");
        BusquedaViewModel.clearRecentQueries(application);
        assertFalse(application.getSharedPreferences(
                BusquedaViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                .contains(BusquedaViewModel.PREF_RECENT));
    }

    @Test
    public void favoritesState_refreshesAfterReturningFromReader() {
        viewModel.setFilter(SearchFilter.FAVORITES);
        assertEquals(BusquedaViewModel.EmptyState.NO_FAVORITES, state().emptyState);

        FavoritesManager favorites = new FavoritesManager(application);
        favorites.add(ArticleFavorites.itemForArticle(
                ArticleRepository.findArticle(application, "564").get(0).article));
        viewModel.refreshFavorites();
        assertEquals(1, state().results.size());
        assertEquals("564", state().results.get(0).item.article.number);

        favorites.remove("article:564");
        viewModel.refreshFavorites();
        assertTrue(state().results.isEmpty());
    }

    private void submit(String query) {
        viewModel.updateQuery(query);
        viewModel.submitCurrentQuery();
    }

    private BusquedaViewModel.BusquedaUiState state() {
        return viewModel.getUiState().getValue();
    }
}
