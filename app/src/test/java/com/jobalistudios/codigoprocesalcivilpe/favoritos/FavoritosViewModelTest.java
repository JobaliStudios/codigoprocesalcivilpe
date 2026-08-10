package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.Highlight;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class FavoritosViewModelTest {

    private Application application;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void filtersCombineArticleMetadataAndExcludeLegacyOutsideAll() {
        Fixture fixture = fixtureWithUserContent();
        FavoriteItem legacy = new FavoriteItem(
                "node:sec_1", "Sección Primera", "Disposiciones generales", "Sección",
                "node:sec_1", 0L);
        fixture.viewModel.setFavorites(Arrays.asList(
                favorite("564", 100L),
                favorite("731", 300L),
                favorite("759", 200L),
                favorite("835", 400L),
                legacy
        ));

        assertEquals(5, state(fixture).visibleItems.size());

        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
        assertEquals(Arrays.asList("759", "564"), numbers(state(fixture).visibleItems));

        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_HIGHLIGHTS);
        assertEquals(Arrays.asList("731", "759", "564"),
                numbers(state(fixture).visibleItems));
    }

    @Test
    public void searchMatchesNumberTitleAccentAndAlphanumericWithoutNoteText() {
        Fixture fixture = fixtureWithUserContent();
        FavoriteItem legacy = new FavoriteItem(
                "node:legacy", "Título legacy", "Disposiciones generales", "Título",
                "node:sec_1_tit_1", 0L);
        fixture.viewModel.setFavorites(Arrays.asList(
                favorite("564", 100L),
                favorite("506-A", 200L),
                favorite("836", 300L),
                legacy
        ));

        fixture.viewModel.setQuery("564");
        assertEquals(Collections.singletonList("564"), numbers(state(fixture).visibleItems));

        fixture.viewModel.setQuery("506-A");
        assertEquals(Collections.singletonList("506-A"), numbers(state(fixture).visibleItems));

        fixture.viewModel.setQuery("ejecucion");
        assertEquals(Collections.singletonList("836"), numbers(state(fixture).visibleItems));

        fixture.viewModel.setQuery("secreto escrito por usuario");
        assertEquals(0, state(fixture).visibleItems.size());

        fixture.viewModel.setQuery("disposiciones");
        assertEquals(Collections.singletonList("node:legacy"),
                ids(state(fixture).visibleItems));
    }

    @Test
    public void queryAndFilterAreAppliedTogether() {
        Fixture fixture = fixtureWithUserContent();
        fixture.viewModel.setFavorites(Arrays.asList(
                favorite("564", 100L),
                favorite("731", 300L)
        ));
        fixture.viewModel.setQuery("564");
        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
        assertEquals(Collections.singletonList("564"), numbers(state(fixture).visibleItems));

        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.ALL);
        fixture.viewModel.setQuery("731");
        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
        assertEquals(0, state(fixture).visibleItems.size());
        assertEquals(FavoritosUiState.EmptyState.NO_QUERY_RESULTS,
                state(fixture).emptyState);
    }

    @Test
    public void recentSortUsesAddedAtAndKeepsUndatedFavoritesAfterDatedOnes() {
        FavoritosViewModel viewModel = emptyFixture().viewModel;
        viewModel.setFavorites(Arrays.asList(
                favorite("564", 100L),
                favorite("647-A", 0L),
                favorite("731", 300L),
                favorite("566-A", 0L),
                favorite("759", 200L)
        ));

        assertEquals(
                Arrays.asList("731", "759", "564", "647-A", "566-A"),
                numbers(viewModel.getUiState().getValue().visibleItems)
        );
    }

    @Test
    public void articleNumberSortUsesLegalSequenceAndPlacesLegacyLastByTitle() {
        FavoritosViewModel viewModel = emptyFixture().viewModel;
        FavoriteItem legacyZ = new FavoriteItem(
                "node:z", "Zeta", "", "Sección", "node:sec_1", 0L);
        FavoriteItem legacyA = new FavoriteItem(
                "node:a", "Ámbito", "", "Título", "node:sec_1_tit_1", 0L);
        viewModel.setFavorites(Arrays.asList(
                favorite("507", 1L), legacyZ, favorite("506-A", 2L),
                favorite("505", 3L), legacyA, favorite("506", 4L)
        ));
        viewModel.setSortMode(FavoritosViewModel.SortMode.ARTICLE_NUMBER);

        List<FavoriteListItem> items = viewModel.getUiState().getValue().visibleItems;
        assertEquals(Arrays.asList("505", "506", "506-A", "507"),
                numbers(items.subList(0, 4)));
        assertEquals("Ámbito", items.get(4).getFavorite().getTitle());
        assertEquals("Zeta", items.get(5).getFavorite().getTitle());
    }

    @Test
    public void refreshingDataKeepsQueryFilterAndSortState() {
        Fixture fixture = fixtureWithUserContent();
        fixture.viewModel.setQuery("564");
        fixture.viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
        fixture.viewModel.setSortMode(FavoritosViewModel.SortMode.ARTICLE_NUMBER);

        fixture.viewModel.setFavorites(Arrays.asList(favorite("564", 1L), favorite("731", 2L)));

        FavoritosUiState state = state(fixture);
        assertEquals("564", state.query);
        assertEquals(FavoritosViewModel.FilterMode.WITH_NOTES, state.filterMode);
        assertEquals(FavoritosViewModel.SortMode.ARTICLE_NUMBER, state.sortMode);
        assertEquals(Collections.singletonList("564"), numbers(state.visibleItems));
    }

    @Test
    public void filterEmptyStatesExplainNotesAndHighlights() {
        FavoritosViewModel viewModel = emptyFixture().viewModel;
        viewModel.setFavorites(Collections.singletonList(favorite("835", 1L)));

        viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
        assertEquals(FavoritosUiState.EmptyState.NO_NOTES,
                viewModel.getUiState().getValue().emptyState);

        viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_HIGHLIGHTS);
        assertEquals(FavoritosUiState.EmptyState.NO_HIGHLIGHTS,
                viewModel.getUiState().getValue().emptyState);
    }

    private Fixture fixtureWithUserContent() {
        Map<String, List<Highlight>> highlights = new HashMap<>();
        addHighlight(highlights, "564", "Nota privada 564");
        addHighlight(highlights, "731", null);
        addHighlight(highlights, "759", "Nota privada 759");
        ArticleUserContentResolver resolver = new ArticleUserContentResolver(
                blockKey -> highlights.getOrDefault(blockKey, Collections.emptyList())
        );
        return new Fixture(new FavoritosViewModel(application, resolver));
    }

    private Fixture emptyFixture() {
        return new Fixture(new FavoritosViewModel(
                application,
                new ArticleUserContentResolver(blockKey -> Collections.emptyList())
        ));
    }

    private void addHighlight(Map<String, List<Highlight>> target, String number, String note) {
        ArticleRepository.Location location = location(number);
        String snippet = header(location.article.text);
        int start = location.article.offsetInBlock;
        target.computeIfAbsent(location.block.key, ignored -> new ArrayList<>()).add(
                new Highlight("h-" + number, start, start + snippet.length(),
                        "yellow", note, snippet, 1L)
        );
    }

    private FavoriteItem favorite(String number, long addedAt) {
        Article article = location(number).article;
        String destination = FavoriteDestinationMapper.destinationForArticle(number);
        return new FavoriteItem(
                destination,
                "Artículo " + number,
                article.title,
                "Artículo",
                destination,
                addedAt
        );
    }

    private ArticleRepository.Location location(String number) {
        return ArticleRepository.findArticle(application, number).get(0);
    }

    private String header(String text) {
        int newline = text.indexOf('\n');
        return newline < 0 ? text : text.substring(0, newline);
    }

    private FavoritosUiState state(Fixture fixture) {
        return fixture.viewModel.getUiState().getValue();
    }

    private List<String> numbers(List<FavoriteListItem> items) {
        List<String> result = new ArrayList<>();
        for (FavoriteListItem item : items) {
            if (item.getArticleNumber() != null) {
                result.add(item.getArticleNumber());
            }
        }
        return result;
    }

    private List<String> ids(List<FavoriteListItem> items) {
        List<String> result = new ArrayList<>();
        for (FavoriteListItem item : items) {
            result.add(item.getFavorite().getId());
        }
        return result;
    }

    private static final class Fixture {
        final FavoritosViewModel viewModel;

        Fixture(FavoritosViewModel viewModel) {
            this.viewModel = viewModel;
        }
    }
}
