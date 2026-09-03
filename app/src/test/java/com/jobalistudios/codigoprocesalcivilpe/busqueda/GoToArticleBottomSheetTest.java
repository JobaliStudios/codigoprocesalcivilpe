package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

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
public class GoToArticleBottomSheetTest {

    private Application application;
    private ActivityController<FragmentActivity> controller;
    private FragmentActivity activity;
    private BusquedaFragment fragment;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(BusquedaViewModel.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();

        controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.get().setTheme(R.style.Theme_codigoprocesalcivilpe);
        controller.create().start().resume().visible();
        activity = controller.get();

        FrameLayout container = new FrameLayout(activity);
        container.setId(View.generateViewId());
        activity.setContentView(container);
        fragment = new BusquedaFragment();
        activity.getSupportFragmentManager().beginTransaction()
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
    public void visibleAction_opensAccessibleFocusedBottomSheet() {
        View action = fragment.requireView().findViewById(R.id.buttonGoToArticle);
        assertEquals(View.VISIBLE, action.getVisibility());
        assertEquals(
                "Abrir acceso rápido para ir a un artículo",
                action.getContentDescription().toString()
        );

        GoToArticleBottomSheet sheet = openSheet();
        TextInputLayout layout = sheet.requireView().findViewById(
                R.id.goToArticleInputLayout
        );
        TextInputEditText input = sheet.requireView().findViewById(R.id.goToArticleInput);

        assertEquals("Número de artículo", layout.getHint().toString());
        assertEquals(EditorInfo.IME_ACTION_GO, input.getImeOptions() & EditorInfo.IME_MASK_ACTION);
        assertTrue(input.hasFocus());
    }

    @Test
    public void validation_isInlineAndKeepsEnteredText() {
        GoToArticleBottomSheet sheet = openSheet();
        TextInputLayout layout = sheet.requireView().findViewById(
                R.id.goToArticleInputLayout
        );
        TextInputEditText input = sheet.requireView().findViewById(R.id.goToArticleInput);

        sheet.requireView().findViewById(R.id.goToArticleSubmit).performClick();
        assertEquals("Ingresa un número de artículo.", layout.getError().toString());
        assertTrue(input.hasFocus());

        input.setText("hola");
        assertNull(layout.getError());
        sheet.requireView().findViewById(R.id.goToArticleSubmit).performClick();
        assertEquals("Ingresa un número de artículo válido.", layout.getError().toString());
        assertEquals("hola", input.getText().toString());

        input.setText("9999");
        sheet.requireView().findViewById(R.id.goToArticleSubmit).performClick();
        assertEquals("No encontramos el artículo 9999.", layout.getError().toString());
        assertEquals("9999", input.getText().toString());
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void validInput_opensExactArticleWithoutSearchOrBackStackSideEffects() {
        SearchView searchView = fragment.requireView().findViewById(R.id.searchView);
        searchView.setQuery("embargo", false);
        fragment.requireView().findViewById(R.id.chipSearchText).performClick();
        GoToArticleBottomSheet sheet = openSheet();
        TextInputEditText input = sheet.requireView().findViewById(R.id.goToArticleInput);
        input.setText("506-a");

        sheet.requireView().findViewById(R.id.goToArticleSubmit).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        ArticleNavigationResolver.Target expected = ArticleNavigationResolver.resolve(
                application,
                "506-A"
        );
        assertNotNull(started);
        assertNotNull(expected);
        assertEquals(SectionContentActivity.class.getName(), started.getComponent().getClassName());
        assertEquals(expected.getOffsetInBlock(), started.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,
                -1
        ));
        int destructiveFlags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP;
        assertEquals(0, started.getFlags() & destructiveFlags);
        assertFalse(activity.isFinishing());
        assertEquals("embargo", searchView.getQuery().toString());
        assertFalse(application.getSharedPreferences(
                BusquedaViewModel.PREFS_NAME,
                Context.MODE_PRIVATE
        ).contains(BusquedaViewModel.PREF_RECENT));
        assertNull(fragment.getChildFragmentManager().findFragmentByTag(
                GoToArticleBottomSheet.TAG
        ));
    }

    @Test
    public void imeGo_hasSameNavigationAsVisualButton() {
        GoToArticleBottomSheet sheet = openSheet();
        TextInputEditText input = sheet.requireView().findViewById(R.id.goToArticleInput);
        input.setText("731");

        input.onEditorAction(EditorInfo.IME_ACTION_GO);

        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        ArticleNavigationResolver.Target expected = ArticleNavigationResolver.resolve(
                application,
                "731"
        );
        assertNotNull(started);
        assertNotNull(expected);
        assertEquals(expected.getOffsetInBlock(), started.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET,
                -1
        ));
    }

    @Test
    public void cancelAndSystemBack_dismissWithoutNavigation() {
        SearchView searchView = fragment.requireView().findViewById(R.id.searchView);
        searchView.setQuery("embargo", false);
        GoToArticleBottomSheet cancelled = openSheet();
        ((TextInputEditText) cancelled.requireView().findViewById(
                R.id.goToArticleInput)).setText("564");

        cancelled.requireView().findViewById(R.id.goToArticleCancel).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertNull(fragment.getChildFragmentManager().findFragmentByTag(
                GoToArticleBottomSheet.TAG
        ));
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
        assertEquals("embargo", searchView.getQuery().toString());

        GoToArticleBottomSheet backed = openSheet();
        backed.requireDialog().onBackPressed();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertNull(fragment.getChildFragmentManager().findFragmentByTag(
                GoToArticleBottomSheet.TAG
        ));
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    private GoToArticleBottomSheet openSheet() {
        fragment.requireView().findViewById(R.id.buttonGoToArticle).performClick();
        fragment.getChildFragmentManager().executePendingTransactions();
        GoToArticleBottomSheet sheet = (GoToArticleBottomSheet) fragment
                .getChildFragmentManager()
                .findFragmentByTag(GoToArticleBottomSheet.TAG);
        assertNotNull(sheet);
        assertNotNull(sheet.getView());
        return sheet;
    }
}
