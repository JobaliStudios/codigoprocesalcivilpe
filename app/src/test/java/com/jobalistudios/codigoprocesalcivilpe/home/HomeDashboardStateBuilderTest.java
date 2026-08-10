package com.jobalistudios.codigoprocesalcivilpe.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HomeDashboardStateBuilderTest {
    private static final String FAVORITES_PREFS = "codigoprocesalcivil_favorites";

    private Context context;
    private ReadingHistoryManager history;
    private HomeDashboardStateBuilder builder;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
                .edit().clear().commit();
        history = new ReadingHistoryManager(context);
        new FavoritesManager(context).clearAll();
        builder = new HomeDashboardStateBuilder(context);
    }

    @Test
    public void noHistory_hidesContinueAndRecentData() {
        HomeDashboardState state = builder.build();

        assertNull(state.continueReading);
        assertEquals(Collections.emptyList(), state.recentlyViewed);
    }

    @Test
    public void lastArticleIsContinueAndIsExcludedFromThreeRecentItems() {
        history.recordArticle("564", "");
        history.recordArticle("561", "");
        history.recordArticle("759", "");
        history.recordArticle("731", "");

        HomeDashboardState state = builder.build();

        assertEquals("731", state.continueReading.number);
        assertEquals(Arrays.asList("759", "561", "564"), numbers(state.recentlyViewed));
    }

    @Test
    public void historySupportsAlphanumericAndIgnoresInvalidDestination() {
        history.recordArticle("506-A", "Emplazamiento excepcional");
        history.recordArticle("9999", "No existe");

        HomeDashboardState state = builder.build();

        assertEquals("506-A", state.continueReading.number);
        assertEquals(Collections.emptyList(), state.recentlyViewed);
    }

    @Test
    public void recentFavorites_returnsThreeNewestArticlesOnlyAndIgnoresInvalid() {
        List<FavoriteItem> stored = Arrays.asList(
                article("564", 100L),
                new FavoriteItem("node:sec_1", "Sección Primera", "", "Sección",
                        "node:sec_1", 900L),
                article("731", 500L),
                article("9999", 1_000L),
                article("506-A", 300L),
                article("759", 200L),
                article("647-A", 0L)
        );

        List<HomeDashboardState.ArticleEntry> result = builder.resolveRecentFavorites(stored);

        assertEquals(Arrays.asList("731", "506-A", "759"), numbers(result));
    }

    @Test
    public void legacyFavoritesWithoutTimestamp_keepPersistedOrder() {
        List<HomeDashboardState.ArticleEntry> result = builder.resolveRecentFavorites(
                Arrays.asList(article("647-A", 0L), article("566-A", 0L), article("506-A", 0L))
        );

        assertEquals(Arrays.asList("647-A", "566-A", "506-A"), numbers(result));
    }

    @Test
    public void restoredFavorite_keepsItsOriginalPositionAmongRecentHomeFavorites() {
        FavoritesManager manager = new FavoritesManager(context);
        FavoriteItem article564 = article("564", 100L);
        FavoriteItem article731 = article("731", 300L);
        FavoriteItem article759 = article("759", 200L);
        manager.restore(article564);
        manager.restore(article731);
        manager.restore(article759);
        manager.remove(article564.getId());
        manager.restore(article564);

        assertEquals(
                Arrays.asList("731", "759", "564"),
                numbers(builder.resolveRecentFavorites(manager.getAll()))
        );
    }

    private FavoriteItem article(String number, long addedAt) {
        String destination = "article:" + number;
        return new FavoriteItem(
                destination, "Artículo " + number, "", "Artículo", destination, addedAt);
    }

    private List<String> numbers(List<HomeDashboardState.ArticleEntry> articles) {
        java.util.ArrayList<String> result = new java.util.ArrayList<>();
        for (HomeDashboardState.ArticleEntry article : articles) {
            result.add(article.number);
        }
        return result;
    }
}
