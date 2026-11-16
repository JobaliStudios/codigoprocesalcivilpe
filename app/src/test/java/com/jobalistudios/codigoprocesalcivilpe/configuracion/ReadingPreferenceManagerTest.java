package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertEquals;
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
    public void premiumSettingsHaveSafeDefaults() {
        assertEquals(ReadingPreferenceManager.ReaderTheme.AUTO,
                ReadingPreferenceManager.getReaderTheme(application));
        assertEquals(ReadingPreferenceManager.ReaderFont.SANS,
                ReadingPreferenceManager.getReaderFont(application));
        assertEquals(ReadingPreferenceManager.LineSpacing.NORMAL,
                ReadingPreferenceManager.getLineSpacing(application));
        assertEquals(ReadingPreferenceManager.Margins.NORMAL,
                ReadingPreferenceManager.getMargins(application));
        assertFalse(ReadingPreferenceManager.isJustifyEnabled(application));
        assertFalse(ReadingPreferenceManager.isFocusDefaultEnabled(application));
        assertFalse(ReadingPreferenceManager.isSwipeNavigationEnabled(application));
        assertTrue(ReadingPreferenceManager.isGlossaryEnabled(application));
        assertEquals(1f, ReadingPreferenceManager.getContentFontScale(application), 0.0001f);
    }

    @Test
    public void premiumSettingsPersistAndResetWithoutErasingUnrelatedData() {
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString("app_theme_mode", "DARK").commit();
        ReadingPreferenceManager.setReaderTheme(application,
                ReadingPreferenceManager.ReaderTheme.SEPIA);
        ReadingPreferenceManager.setReaderFont(application,
                ReadingPreferenceManager.ReaderFont.ACCESSIBLE);
        ReadingPreferenceManager.setLineSpacing(application,
                ReadingPreferenceManager.LineSpacing.WIDE);
        ReadingPreferenceManager.setMargins(application,
                ReadingPreferenceManager.Margins.NARROW);
        ReadingPreferenceManager.setContentFontScale(application, 1.37f);
        ReadingPreferenceManager.setJustifyEnabled(application, true);
        ReadingPreferenceManager.setFocusDefaultEnabled(application, true);
        ReadingPreferenceManager.setSwipeNavigationEnabled(application, true);
        ReadingPreferenceManager.setGlossaryEnabled(application, false);
        ReadingPreferenceManager.setKeepScreenOnEnabled(application, true);
        assertEquals(ReadingPreferenceManager.ReaderTheme.SEPIA,
                ReadingPreferenceManager.getReaderTheme(application));
        assertEquals(1.35f, ReadingPreferenceManager.getContentFontScale(application), 0.0001f);

        ReadingPreferenceManager.resetReadingPreferences(application);
        premiumSettingsHaveSafeDefaults();
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(application));
        assertEquals("DARK", application.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE).getString("app_theme_mode", ""));
    }

    @Test
    public void keepScreenOn_defaultsToFalse() {
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(application));
    }

    @Test
    public void viewModel_refreshesAfterDeletingAllPreferencesWithoutRestart() {
        ReadingPreferenceManager.setKeepScreenOnEnabled(application, true);
        ConfiguracionViewModel viewModel = new ConfiguracionViewModel(application);
        assertTrue(Boolean.TRUE.equals(viewModel.getModoLectura().getValue()));

        new com.jobalistudios.codigoprocesalcivilpe.privacidad.LocalDataManager(application)
                .clearAllLocalData();
        viewModel.refreshModoLectura();

        assertFalse(Boolean.TRUE.equals(viewModel.getModoLectura().getValue()));
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
