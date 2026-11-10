package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * El estado guardado de las vistas nunca debe reescribir preferencias eliminadas
 * al recrear Configuración por un cambio de tema DayNight.
 */
@RunWith(RobolectricTestRunner.class)
public class ConfigurationSwitchSavedStateTest {

    @Test
    public void preferenceSwitchesIgnoreObsoleteSavedViewState() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Context themed = new ContextThemeWrapper(
                appContext, R.style.Theme_codigoprocesalcivilpe);
        LayoutInflater inflater = LayoutInflater.from(themed);

        View original = inflater.inflate(R.layout.fragment_configuracion,
                new FrameLayout(themed), false);
        SwitchMaterial oldTheme = original.findViewById(R.id.switch_modo_oscuro);
        SwitchMaterial oldReading = original.findViewById(R.id.switch_modo_lectura);

        assertFalse(oldTheme.isSaveEnabled());
        assertFalse(oldReading.isSaveEnabled());
        oldTheme.setChecked(true);
        oldReading.setChecked(true);
        assertTrue(oldTheme.isChecked());
        assertTrue(oldReading.isChecked());

        SparseArray<Parcelable> previousUiState = new SparseArray<>();
        original.saveHierarchyState(previousUiState);

        View recreated = inflater.inflate(R.layout.fragment_configuracion,
                new FrameLayout(themed), false);
        SwitchMaterial newTheme = recreated.findViewById(R.id.switch_modo_oscuro);
        SwitchMaterial newReading = recreated.findViewById(R.id.switch_modo_lectura);
        recreated.restoreHierarchyState(previousUiState);

        // La configuración real se aplicará desde ThemePreferenceManager/LiveData.
        assertFalse(newTheme.isChecked());
        assertFalse(newReading.isChecked());
    }
}
