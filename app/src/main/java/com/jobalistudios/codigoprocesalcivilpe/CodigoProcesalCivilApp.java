package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Application;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ThemePreferenceManager;

public class CodigoProcesalCivilApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ThemePreferenceManager.applySavedTheme(this);
    }
}
