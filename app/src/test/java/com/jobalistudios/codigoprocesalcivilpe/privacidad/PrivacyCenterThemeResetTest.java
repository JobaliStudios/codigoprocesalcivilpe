package com.jobalistudios.codigoprocesalcivilpe.privacidad;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.AppThemeMode;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ThemePreferenceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowDialog;

/** Prueba Robolectric: jamás borra datos de un teléfono real. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, qualifiers = "night")
public class PrivacyCenterThemeResetTest {
    private Context context;
    @Before public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().clear().commit();
        ThemePreferenceManager.setAppThemeMode(context, AppThemeMode.DARK);
        ReadingPreferenceManager.setKeepScreenOnEnabled(context, true);
        ReadingPreferenceManager.setReaderTheme(context,
                ReadingPreferenceManager.ReaderTheme.SEPIA);
        ThemePreferenceManager.applySavedTheme(context);
    }
    @After public void tearDown() {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().clear().commit();
        ThemePreferenceManager.applySavedTheme(context);
    }

    @Test public void clearAllRestoresThemeAfterConfirmationDialogIsClosed() {
        PrivacyCenterActivity activity = Robolectric.buildActivity(PrivacyCenterActivity.class)
                .setup().get();
        assertEquals(AppThemeMode.DARK, ThemePreferenceManager.getAppThemeMode(context));
        activity.findViewById(R.id.buttonClearAll).performClick();
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertTrue(dialog.isShowing());
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertEquals(AppThemeMode.SYSTEM, ThemePreferenceManager.getAppThemeMode(context));
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(context));
        assertEquals(ReadingPreferenceManager.ReaderTheme.AUTO,
                ReadingPreferenceManager.getReaderTheme(context));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.getDefaultNightMode());
    }
}
