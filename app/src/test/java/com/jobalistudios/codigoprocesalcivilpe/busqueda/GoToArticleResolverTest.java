package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GoToArticleResolverTest {

    private Application application;
    private GoToArticleResolver resolver;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(BusquedaViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        resolver = new GoToArticleResolver();
    }

    @Test
    public void emptyInput_returnsEmptyWithoutTarget() {
        assertStatus(GoToArticleResolver.Status.EMPTY, resolver.resolve(application, null));
        assertStatus(GoToArticleResolver.Status.EMPTY, resolver.resolve(application, ""));
        assertStatus(GoToArticleResolver.Status.EMPTY, resolver.resolve(application, "   "));
    }

    @Test
    public void numericInputAndSpaces_resolveExactArticle() {
        assertValid("564", "564");
        assertValid("  564  ", "564");
        assertValid("731", "731");
    }

    @Test
    public void supportedPrefixes_areNormalizedByExistingParser() {
        assertValid("Artículo 564", "564");
        assertValid("articulo 564", "564");
        assertValid("ARTÍCULO 564", "564");
        assertValid("Art. 564", "564");
        assertValid("Art 564", "564");
    }

    @Test
    public void alphanumericVariants_resolveCanonicalArticle() {
        assertValid("506-A", "506-A");
        assertValid("506-a", "506-A");
        assertValid("506 - A", "506-A");
    }

    @Test
    public void repealedArticle_isStillAValidDestination() {
        assertValid("835", "835");
    }

    @Test
    public void invalidFormatAndMissingArticle_areDifferentResults() {
        GoToArticleResolver.Result invalid = resolver.resolve(application, "hola");
        assertStatus(GoToArticleResolver.Status.INVALID_FORMAT, invalid);
        assertNull(invalid.normalizedNumber);

        GoToArticleResolver.Result missing = resolver.resolve(application, "9999");
        assertStatus(GoToArticleResolver.Status.NOT_FOUND, missing);
        assertEquals("9999", missing.normalizedNumber);
    }

    @Test
    public void validResult_reusesArticleNavigationTargetAndItsIntent() {
        GoToArticleResolver.Result result = resolver.resolve(application, "506-a");
        ArticleNavigationResolver.Target expected = ArticleNavigationResolver.resolve(
                application,
                "506-A"
        );

        assertNotNull(result.target);
        assertNotNull(expected);
        assertEquals(expected.getNumber(), result.target.getNumber());
        assertEquals(expected.getOffsetInBlock(), result.target.getOffsetInBlock());
        Intent intent = result.target.createIntent(application);
        assertEquals(SectionContentActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(expected.getOffsetInBlock(), intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,
                -1
        ));
    }

    @Test
    public void resolvingQuickJump_doesNotChangeSearchStateOrRecents() {
        BusquedaViewModel viewModel = new BusquedaViewModel(application);
        viewModel.updateQuery("embargo");
        viewModel.setFilter(SearchFilter.TEXT);
        viewModel.toggleSection("Sección Primera");

        GoToArticleResolver.Result result = resolver.resolve(application, "564");
        BusquedaViewModel.BusquedaUiState state = viewModel.getUiState().getValue();

        assertEquals(GoToArticleResolver.Status.VALID, result.status);
        assertNotNull(state);
        assertEquals("embargo", state.query);
        assertEquals(SearchFilter.TEXT, state.filter);
        assertTrue(state.selectedSections.contains("Sección Primera"));
        assertTrue(state.recentQueries.isEmpty());
    }

    private void assertValid(String input, String expectedNumber) {
        GoToArticleResolver.Result result = resolver.resolve(application, input);
        assertEquals(GoToArticleResolver.Status.VALID, result.status);
        assertEquals(expectedNumber, result.normalizedNumber);
        assertNotNull(result.target);
        assertEquals(expectedNumber, result.target.getNumber());
    }

    private void assertStatus(
            GoToArticleResolver.Status expected,
            GoToArticleResolver.Result result
    ) {
        assertEquals(expected, result.status);
        assertNull(result.target);
    }
}
