package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/** Neutraliza únicamente fontScale; la densidad y el resto de configuración se preservan. */
public final class AppFontScaleContextWrapper {
    private static final float APP_FONT_SCALE = 1f;

    private AppFontScaleContextWrapper() { }

    @NonNull
    public static Context wrap(@NonNull Context context) {
        Configuration configuration = new Configuration(
                context.getResources().getConfiguration());
        configuration.fontScale = APP_FONT_SCALE;
        return context.createConfigurationContext(configuration);
    }
}
