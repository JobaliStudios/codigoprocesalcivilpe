package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Application;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.busqueda.ArticleSearchNavigation;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.ArticleSearchResult;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.LegalSearchEngine;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchFilter;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLooper;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityGlobalSearchTest {
    private ActivityController<SectionContentActivity> controller;

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void textualResult_keepsSelectedArticleHeaderAndInternalMatchQuery() {
        Application application = ApplicationProvider.getApplicationContext();
        ArticleSearchResult result = LegalSearchEngine.create(application)
                .search("embargo", SearchFilter.TEXT,
                        Collections.emptySet(), Collections.emptySet())
                .results.get(0);
        Intent intent = ArticleSearchNavigation.createIntent(
                application, result, "embargo");
        assertNotNull(intent);

        controller = Robolectric.buildActivity(SectionContentActivity.class, intent)
                .create().start().resume().visible();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        SectionContentActivity activity = controller.get();

        TextView articleNumber = activity.findViewById(R.id.currentArticleNumber);
        EditText internalQuery = activity.findViewById(R.id.edtBusqueda);
        assertEquals("Artículo " + result.item.article.number,
                articleNumber.getText().toString());
        assertEquals("embargo", internalQuery.getText().toString());
    }
}
