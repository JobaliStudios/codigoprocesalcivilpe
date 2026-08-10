package com.jobalistudios.codigoprocesalcivilpe.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigoProcesalCivilMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCStartScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class HomeFragmentReadingHistoryTest {

    private static final String FAVORITES_PREFS = "codigoprocesalcivil_favorites";

    private Application application;
    private ActivityController<FragmentActivity> controller;
    private HomeFragment fragment;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
        application.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
                .edit().clear().commit();

        controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.get().setTheme(R.style.Theme_codigoprocesalcivilpe);
        controller.create().start().resume().visible();

        FrameLayout container = new FrameLayout(controller.get());
        container.setId(View.generateViewId());
        controller.get().setContentView(container);
        fragment = new HomeFragment();
        controller.get().getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), fragment)
                .commitNow();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void historySection_isHiddenWithoutHistoryAndRefreshesOnResume() {
        View historySection = fragment.requireView().findViewById(R.id.reading_history_section);
        assertEquals(View.GONE, historySection.getVisibility());

        new ReadingHistoryManager(application)
                .recordArticle("506-A", "Emplazamiento excepcional");
        controller.pause().resume();

        assertEquals(View.VISIBLE, historySection.getVisibility());
        TextView number = fragment.requireView().findViewById(R.id.continue_article_number);
        assertEquals("Artículo 506-A", number.getText().toString());

        fragment.requireView().findViewById(R.id.card_continue_reading).performClick();
        Intent started = Shadows.shadowOf(application).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(SectionContentActivity.class.getName(), started.getComponent().getClassName());
        Article article = ArticleRepository.findArticle(application, "506-A").get(0).article;
        assertEquals(article.offsetInBlock, started.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
    }

    @Test
    public void heroShowsFixedVerificationDateAndOpensCodeAndQuizSetup() {
        TextView date = fragment.requireView().findViewById(R.id.home_verified_date);
        assertEquals("Contenido normativo verificado al 8 de agosto de 2026",
                date.getText().toString());
        assertNotNull(fragment.requireView().findViewById(R.id.adContainer2));

        fragment.requireView().findViewById(R.id.button_consultar_codigo).performClick();
        Intent codeIntent = Shadows.shadowOf(application).getNextStartedActivity();
        assertNotNull(codeIntent);
        assertEquals(CodigoProcesalCivilMain.class.getName(),
                codeIntent.getComponent().getClassName());

        fragment.requireView().findViewById(R.id.button_practice_quiz).performClick();
        Intent quizIntent = Shadows.shadowOf(application).getNextStartedActivity();
        assertNotNull(quizIntent);
        assertEquals(QuizzCPCStartScreen.class.getName(),
                quizIntent.getComponent().getClassName());
    }

    @Test
    public void recentFavorites_refreshOnResumeAndOpenExactArticle() {
        View emptyState = fragment.requireView().findViewById(R.id.favorites_empty_state);
        assertEquals(View.VISIBLE, emptyState.getVisibility());
        Article article = ArticleRepository.findArticle(application, "647-A").get(0).article;

        controller.pause();
        new FavoritesManager(application).add(ArticleFavorites.itemForArticle(article));
        controller.resume();

        LinearLayout favorites = fragment.requireView()
                .findViewById(R.id.favorite_articles_container);
        assertEquals(1, favorites.getChildCount());
        assertEquals(View.GONE, emptyState.getVisibility());
        TextView number = favorites.getChildAt(0).findViewById(R.id.dashboard_article_number);
        assertEquals("Artículo 647-A", number.getText().toString());

        favorites.getChildAt(0).performClick();
        Intent started = Shadows.shadowOf(application).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(article.offsetInBlock, started.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
    }
}
