package com.jobalistudios.codigoprocesalcivilpe.glosario;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.FrameLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.jobalistudios.codigoprocesalcivilpe.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(RobolectricTestRunner.class)
public class GlossaryBottomSheetTest {
    private ActivityController<FragmentActivity> controller;
    private FragmentActivity activity;
    private Field cache;
    private Object originalCache;

    @Before public void setUp() throws Exception {
        Context application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().remove("glossary_enabled").commit();
        cache = GlossaryRepository.class.getDeclaredField("cachedTerms");
        cache.setAccessible(true);
        originalCache = cache.get(null);
        cache.set(null, GlossaryRepository.parseJson(GlossaryRepositoryTest.FIXTURE));

        controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.get().setTheme(R.style.Theme_codigoprocesalcivilpe);
        controller.create().start().resume().visible();
        activity = controller.get();
        FrameLayout content = new FrameLayout(activity);
        content.setId(View.generateViewId());
        activity.setContentView(content);
    }

    @After public void tearDown() throws Exception {
        if (controller != null) controller.pause().stop().destroy();
        if (cache != null) cache.set(null, originalCache);
    }

    private GlossaryBottomSheet openTerm() {
        GlossaryBottomSheet sheet = GlossaryBottomSheet.newInstance("debido-proceso");
        sheet.show(activity.getSupportFragmentManager(), GlossaryBottomSheet.TAG);
        activity.getSupportFragmentManager().executePendingTransactions();
        assertNotNull(sheet.getView());
        return sheet;
    }

    @Test public void sheetShowsEditorialProvenanceAndReview() {
        GlossaryBottomSheet sheet = openTerm();
        View root = sheet.requireView();
        assertEquals("Debido proceso",
                ((TextView)root.findViewById(R.id.glossaryTerm)).getText().toString());
        assertTrue(((TextView)root.findViewById(R.id.glossaryDisclaimer))
                .getText().toString().contains("no forma parte del texto legal"));
        assertTrue(((TextView)root.findViewById(R.id.glossaryDisclaimer))
                .getText().toString().contains("2026-11-16"));
        assertTrue(((TextView)root.findViewById(R.id.glossaryReviewer))
                .getText().toString().contains("Revisor de test"));
    }

    @Test public void listSearchFiltersAndCanReopenDetail() {
        GlossaryListBottomSheet list = new GlossaryListBottomSheet();
        list.show(activity.getSupportFragmentManager(), GlossaryListBottomSheet.TAG);
        activity.getSupportFragmentManager().executePendingTransactions();
        TextInputEditText input = list.requireView().findViewById(R.id.glossaryListSearch);
        input.setText("termino inexistente");
        assertEquals(View.VISIBLE, list.requireView().findViewById(
                R.id.glossaryListEmpty).getVisibility());
        input.setText("DEBIDO");
        assertEquals(View.GONE, list.requireView().findViewById(
                R.id.glossaryListEmpty).getVisibility());
        android.widget.LinearLayout items = list.requireView()
                .findViewById(R.id.glossaryListItems);
        assertEquals(1,items.getChildCount());
        items.getChildAt(0).performClick();
        activity.getSupportFragmentManager().executePendingTransactions();
        assertNotNull(activity.getSupportFragmentManager()
                .findFragmentByTag(GlossaryBottomSheet.TAG));
    }

    @Test public void glossaryDisabledShowsNoDetail() throws Exception {
        ApplicationProvider.getApplicationContext()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().putBoolean("glossary_enabled",false).commit();
        GlossaryBottomSheet sheet = GlossaryBottomSheet.newInstance("debido-proceso");
        sheet.show(activity.getSupportFragmentManager(), GlossaryBottomSheet.TAG);
        activity.getSupportFragmentManager().executePendingTransactions();
        assertFalse(sheet.isVisible());
        ApplicationProvider.getApplicationContext()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().remove("glossary_enabled").commit();
    }

    @Test public void emptyRepositoryNeverDisplaysStaleDefinition() throws Exception {
        cache.set(null, Collections.emptyList());
        GlossaryBottomSheet sheet = GlossaryBottomSheet.newInstance("debido-proceso");
        sheet.show(activity.getSupportFragmentManager(), GlossaryBottomSheet.TAG);
        activity.getSupportFragmentManager().executePendingTransactions();
        assertFalse(sheet.isVisible());
    }
}
