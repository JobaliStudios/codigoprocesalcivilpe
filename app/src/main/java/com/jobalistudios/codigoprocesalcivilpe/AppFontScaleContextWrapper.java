package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Respeta la escala de fuente configurada por la persona en Android.
 * No sustituye densidad, ventana, orientación ni accesibilidad del sistema.
 */
public final class AppFontScaleContextWrapper {

    private AppFontScaleContextWrapper() { }

    @NonNull
    public static Context wrap(@NonNull Context context) {
        return context;
    }
}
