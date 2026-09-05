package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;

import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.appbar.MaterialToolbar;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class MyNotesActivityTest {
    private static final String FAVORITES_PREFS = "codigoprocesalcivil_favorites";
    private static final String HIGHLIGHTS_PREFS = "codigoprocesalcivil_highlights";

    private Application application;
    private ActivityController<MyNotesActivity> controller;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
                .edit().clear().commit();
        application.getSharedPreferences(HIGHLIGHTS_PREFS, Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void emptyState_hidesShareAction() {
        MyNotesActivity activity = launch();

        assertEquals(View.VISIBLE, activity.findViewById(R.id.myNotesEmptyState).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.buttonShareMyNotes).getVisibility());
        activity.findViewById(R.id.buttonShareMyNotes).performClick();
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void toolbar_hasScreenTitleAndAccessibleBackAction() {
        MyNotesActivity activity = launch();
        MaterialToolbar toolbar = activity.findViewById(R.id.myNotesToolbar);

        assertEquals("Mis apuntes", toolbar.getTitle());
        assertNotNull(toolbar.getNavigationContentDescription());
        assertFalse(toolbar.getNavigationContentDescription().toString().trim().isEmpty());
    }

    @Test
    public void returningFromReader_refreshesRemovedFavoriteWithoutChangingStoredData() {
        addFavorite("564");
        MyNotesActivity activity = launch();
        String before = application.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
                .getString("items", "");
        activity.findViewById(R.id.buttonShareMyNotes).performClick();
        assertEquals(before, application.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
                .getString("items", ""));

        controller.pause().stop();
        new FavoritesManager(application).remove("article:564");
        controller.restart().start().resume().visible();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(View.VISIBLE, activity.findViewById(R.id.myNotesEmptyState).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.buttonShareMyNotes).getVisibility());
    }

    @Test
    public void share_opensNativeChooserWithTextOnly() {
        addFavorite("564");
        assertEquals(1, new FavoritesManager(application).getAll().size());
        assertEquals(1, new UserNotesAggregator(application).build().size());
        MyNotesActivity activity = launch();

        assertSame(application, activity.getApplication());
        assertEquals(1, new UserNotesAggregator(activity).build().size());
        View shareButton = activity.findViewById(R.id.buttonShareMyNotes);
        assertEquals(View.VISIBLE, shareButton.getVisibility());
        assertTrue(shareButton.performClick());

        Intent chooser = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(chooser);
        assertEquals(Intent.ACTION_CHOOSER, chooser.getAction());
        assertEquals(application.getString(R.string.my_notes_share_chooser),
                chooser.getStringExtra(Intent.EXTRA_TITLE));
        Intent send = IntentCompat.getParcelableExtra(
                chooser, Intent.EXTRA_INTENT, Intent.class);
        assertNotNull(send);
        assertEquals(Intent.ACTION_SEND, send.getAction());
        assertEquals("text/plain", send.getType());
        assertTrue(send.getStringExtra(Intent.EXTRA_TEXT).contains("Artículo 564"));
        assertFalse(send.hasExtra(Intent.EXTRA_STREAM));
    }

    @Test
    public void tappingArticleHeader_usesResolvedReaderDestination() {
        addFavorite("564");
        assertEquals(1, new FavoritesManager(application).getAll().size());
        assertEquals(1, new UserNotesAggregator(application).build().size());
        MyNotesActivity activity = launch();
        assertSame(application, activity.getApplication());
        assertEquals(1, new UserNotesAggregator(activity).build().size());
        RecyclerView recycler = activity.findViewById(R.id.myNotesRecycler);
        assertEquals(1, recycler.getAdapter().getItemCount());
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        RecyclerView.ViewHolder holder = recycler.findViewHolderForAdapterPosition(0);
        assertNotNull(holder);

        holder.itemView.findViewById(R.id.myNotesArticleHeader).performClick();

        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(SectionContentActivity.class.getName(),
                started.getComponent().getClassName());
        assertTrue(started.hasExtra(SectionContentActivity.EXTRA_SCROLL_TO_OFFSET));
    }

    private MyNotesActivity launch() {
        controller = Robolectric.buildActivity(MyNotesActivity.class)
                .create().start().resume().visible();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        return controller.get();
    }

    private void addFavorite(String number) {
        Article article = ArticleRepository.findArticle(application, number).get(0).article;
        new FavoritesManager(application).add(ArticleFavorites.itemForArticle(article));
    }
}
