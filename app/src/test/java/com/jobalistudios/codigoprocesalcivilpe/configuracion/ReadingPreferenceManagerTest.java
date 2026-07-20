package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ReadingPreferenceManagerTest {

    private static final String PREFS_NAME = "app_settings";

    private Application application;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void keepScreenOn_defaultsToFalse() {
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(application));
    }

    @Test
    public void viewModel_persistsAndRestoresReadingMode() {
        ConfiguracionViewModel firstViewModel = new ConfiguracionViewModel(application);
        assertFalse(Boolean.TRUE.equals(firstViewModel.getModoLectura().getValue()));

        firstViewModel.setModoLectura(true);
        assertTrue(ReadingPreferenceManager.isKeepScreenOnEnabled(application));

        ConfiguracionViewModel restoredViewModel = new ConfiguracionViewModel(application);
        assertTrue(Boolean.TRUE.equals(restoredViewModel.getModoLectura().getValue()));

        restoredViewModel.setModoLectura(false);
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(application));
    }
}
