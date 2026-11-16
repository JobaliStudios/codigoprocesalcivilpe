package com.jobalistudios.codigoprocesalcivilpe.home;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ReaderChapterProgress;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class HomePremiumDashboardTest {
    private Application app;
    private ActivityController<FragmentActivity> controller;
    private HomeFragment home;

    @Before public void before() {
        app = ApplicationProvider.getApplicationContext();
        app.getSharedPreferences(ReadingHistoryManager.PREFS_NAME,Context.MODE_PRIVATE)
                .edit().clear().commit();
        app.getSharedPreferences("codigoprocesalcivil_favorites",Context.MODE_PRIVATE)
                .edit().clear().commit();
        controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.get().setTheme(R.style.Theme_codigoprocesalcivilpe);
        controller.create().start().resume().visible();
        FrameLayout container = new FrameLayout(controller.get());
        container.setId(View.generateViewId());
        controller.get().setContentView(container);
        home = new HomeFragment();
        controller.get().getSupportFragmentManager().beginTransaction()
                .replace(container.getId(),home).commitNow();
    }

    @After public void after() {
        if(controller != null) controller.pause().stop().destroy();
    }

    @Test public void articleOfDayIsVisibleOpensExactlyAndFavoriteStateRefreshes() {
        View root = home.requireView();
        View daily = root.findViewById(R.id.cardArticleOfTheDay);
        assertEquals(View.VISIBLE,daily.getVisibility());
        TextView label = daily.findViewById(R.id.dailyArticleNumber);
        String number = label.getText().toString().replace("Artículo ", "");
        assertNotNull(ArticleRepository.findArticle(app,number));
        View star = daily.findViewById(R.id.buttonDailyFavorite);
        FavoritesManager manager = new FavoritesManager(app);
        String id = FavoriteDestinationMapper.destinationForArticle(number);
        assertFalse(manager.isFavorite(id));
        star.performClick();
        assertTrue(manager.isFavorite(id));
        assertTrue(star.isSelected());
        controller.pause().resume();
        assertTrue(star.isSelected());

        root.findViewById(R.id.buttonDailyRead).performClick();
        Intent intent = Shadows.shadowOf(app).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(SectionContentActivity.class.getName(),
                intent.getComponent().getClassName());
        assertEquals(ArticleRepository.findArticle(app,number).get(0).article.offsetInBlock,
                intent.getIntExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,-1));
    }

    @Test public void continueProgressUsesChapterOrdinalAndHistoryChipsRemain() {
        ReadingHistoryManager history = new ReadingHistoryManager(app);
        history.recordArticle("564","");
        history.recordArticle("565","");
        controller.pause().resume();
        View root = home.requireView();
        assertEquals(View.VISIBLE,root.findViewById(
                R.id.continue_reading_section).getVisibility());
        ReaderChapterProgress expected = ReaderChapterProgress.of(
                ArticleRepository.findArticle(app,"565").get(0).block, "565");
        com.google.android.material.progressindicator.LinearProgressIndicator progress =
                root.findViewById(R.id.continueReadingProgress);
        assertEquals(expected.total,progress.getMax());
        assertEquals(expected.position,progress.getProgress());
        assertTrue(((TextView)root.findViewById(R.id.continueReadingProgressLabel))
                .getText().toString().contains(expected.position + " de " + expected.total));
        android.widget.LinearLayout chips = root.findViewById(R.id.recent_articles_container);
        assertEquals(1,chips.getChildCount());
        assertEquals(View.VISIBLE,root.findViewById(
                R.id.recently_viewed_section).getVisibility());

        root.findViewById(R.id.buttonContinueReading).performClick();
        Intent intent = Shadows.shadowOf(app).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(ArticleRepository.findArticle(app,"565").get(0).article.offsetInBlock,
                intent.getIntExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,-1));
    }
}
