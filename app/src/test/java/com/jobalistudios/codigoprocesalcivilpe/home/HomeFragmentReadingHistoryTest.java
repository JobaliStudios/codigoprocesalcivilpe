package com.jobalistudios.codigoprocesalcivilpe.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;

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

    private Application application;
    private ActivityController<FragmentActivity> controller;
    private HomeFragment fragment;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(ReadingHistoryManager.PREFS_NAME, Context.MODE_PRIVATE)
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
    }
}
