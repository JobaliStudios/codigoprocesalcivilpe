package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.RecentArticle;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityReadingHistoryTest {

    private Application application;
    private ActivityController<SectionContentActivity> controller;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.stop().destroy();
        }
    }

    @Test
    public void openingResolvedArticle_recordsItOnlyWhenContentActivityDisplaysIt() {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(application, "506-A");
        assertNotNull(target);

        controller = Robolectric.buildActivity(SectionContentActivity.class,
                        target.createIntent(application))
                .create().start().resume().visible();
        controller.pause();

        RecentArticle last = new ReadingHistoryManager(application).getLastArticle();
        assertNotNull(last);
        assertEquals("506-A", last.getNumber());
    }
}
