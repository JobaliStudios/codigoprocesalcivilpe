package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** La restauración de vistas no puede sobrescribir app_settings. */
@RunWith(RobolectricTestRunner.class)
public class ConfigurationSwitchSavedStateTest {
    @Test public void selectorsIgnoreObsoleteSavedViewState() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Context themed = new ContextThemeWrapper(appContext,
                R.style.Theme_codigoprocesalcivilpe);
        LayoutInflater inflater = LayoutInflater.from(themed);
        View original = inflater.inflate(R.layout.fragment_configuracion,
                new FrameLayout(themed), false);
        MaterialButtonToggleGroup oldTheme = original.findViewById(R.id.appThemeToggle);
        SwitchMaterial oldReading = original.findViewById(R.id.switch_modo_lectura);
        assertFalse(oldTheme.isSaveEnabled());
        assertFalse(oldReading.isSaveEnabled());
        oldTheme.check(R.id.themeDark);
        oldReading.setChecked(true);

        SparseArray<Parcelable> previousUiState = new SparseArray<>();
        original.saveHierarchyState(previousUiState);
        View recreated = inflater.inflate(R.layout.fragment_configuracion,
                new FrameLayout(themed), false);
        MaterialButtonToggleGroup newTheme = recreated.findViewById(R.id.appThemeToggle);
        SwitchMaterial newReading = recreated.findViewById(R.id.switch_modo_lectura);
        recreated.restoreHierarchyState(previousUiState);

        assertFalse(newTheme.getCheckedButtonId() == R.id.themeDark);
        assertFalse(newReading.isChecked());
    }
}
