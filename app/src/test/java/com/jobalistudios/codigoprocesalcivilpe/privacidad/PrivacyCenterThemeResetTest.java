package com.jobalistudios.codigoprocesalcivilpe.privacidad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
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

/** La prueba corre en Robolectric; jamás borra datos de un teléfono real. */
@RunWith(RobolectricTestRunner.class)
// Una actividad con el modo nocturno ya activo evita recreaciones recursivas
// de AppCompat antes de que el test pulse «Borrar todos los datos».
@Config(sdk = 30, qualifiers = "night")
public class PrivacyCenterThemeResetTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().clear().commit();
        ThemePreferenceManager.setDarkModeEnabled(context, true);
        ReadingPreferenceManager.setKeepScreenOnEnabled(context, true);
        ThemePreferenceManager.applySavedTheme(context);
    }

    @After
    public void tearDown() {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().clear().commit();
        ThemePreferenceManager.applySavedTheme(context);
    }

    @Test
    public void clearAllRestoresThemeAfterConfirmationDialogIsClosed() {
        PrivacyCenterActivity activity = Robolectric.buildActivity(PrivacyCenterActivity.class)
                .setup().get();

        assertTrue(ThemePreferenceManager.isDarkModeEnabled(context));
        activity.findViewById(R.id.buttonClearAll).performClick();

        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertTrue(dialog.isShowing());
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertFalse(ThemePreferenceManager.isDarkModeEnabled(context));
        assertFalse(ReadingPreferenceManager.isKeepScreenOnEnabled(context));
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.getDefaultNightMode());
    }
}
