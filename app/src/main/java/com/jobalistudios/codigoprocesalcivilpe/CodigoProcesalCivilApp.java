package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ThemePreferenceManager;

public class CodigoProcesalCivilApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        boolean darkModeEnabled = ThemePreferenceManager.isDarkModeEnabled(this);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
