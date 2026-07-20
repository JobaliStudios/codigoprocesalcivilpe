package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityReadingModeTest {

    private static final String PREFS_NAME = "app_settings";

    private Application application;
    private ActivityController<SectionContentActivity> controller;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void readingMode_addsAndClearsKeepScreenOnFlag() {
        ReadingPreferenceManager.setKeepScreenOnEnabled(application, true);
        Intent intent = SectionContentActivity.createIntent(
                application,
                R.layout.activity_section_content,
                R.string.seccionprimeratit1txt,
                R.string.app_name,
                R.string.app_name
        );

        controller = Robolectric.buildActivity(SectionContentActivity.class, intent)
                .create()
                .start()
                .resume()
                .visible();
        SectionContentActivity activity = controller.get();

        assertNotEquals(0, keepScreenOnFlag(activity));

        ReadingPreferenceManager.setKeepScreenOnEnabled(application, false);
        controller.pause().resume();

        assertEquals(0, keepScreenOnFlag(activity));
    }

    private int keepScreenOnFlag(SectionContentActivity activity) {
        return activity.getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
    }
}
