package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Intent;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.busqueda.ArticleSearchNavigation;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.ArticleSearchResult;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.LegalSearchEngine;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchFilter;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeCalloutSpan;

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

    @Test
    public void searchInsideAnnotation_highlightsMatchAndClosingKeepsCallout() {
        Application application = ApplicationProvider.getApplicationContext();
        Article article = ArticleRepository.findArticle(application, "834").get(0).article;
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                application,
                "834"
        );
        assertNotNull(target);
        Intent intent = target.createIntent(application)
                .putExtra(SectionContentActivity.EXTRA_INITIAL_QUERY, "32377")
                .putExtra(
                        SectionContentActivity.EXTRA_INITIAL_QUERY_OFFSET,
                        article.offsetInBlock + article.text.indexOf("32377")
                );

        controller = Robolectric.buildActivity(SectionContentActivity.class, intent)
                .create().start().resume().visible();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        SectionContentActivity activity = controller.get();
        TextView content = activity.findViewById(R.id.textView2);

        Spanned searching = (Spanned) content.getText();
        assertTrue(searching.getSpans(
                0,
                searching.length(),
                NormativeCalloutSpan.class
        ).length > 0);
        assertTrue(searching.getSpans(
                0,
                searching.length(),
                SectionSearchController.SearchHighlightSpan.class
        ).length > 0);

        activity.findViewById(R.id.btnCerrarBusqueda).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Spanned closed = (Spanned) content.getText();
        assertTrue(closed.getSpans(
                0,
                closed.length(),
                NormativeCalloutSpan.class
        ).length > 0);
        assertEquals(0, closed.getSpans(
                0,
                closed.length(),
                SectionSearchController.SearchHighlightSpan.class
        ).length);
    }
}
