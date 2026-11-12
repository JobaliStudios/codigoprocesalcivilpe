package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/**
 * Transición hacia pantallas adaptables: teléfonos compactos permanecen verticales;
 * tablets, pantallas interiores plegables y ventanas múltiples siguen al usuario.
 * Android 16 y posteriores pueden ignorar bloqueos en pantallas grandes.
 */
final class AdaptiveOrientationPolicy {
    static final int LARGE_SCREEN_DP = 600;

    private AdaptiveOrientationPolicy() {
    }

    static int orientationFor(int smallestScreenWidthDp, boolean inMultiWindow) {
        if (inMultiWindow || smallestScreenWidthDp >= LARGE_SCREEN_DP) {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    static void apply(@NonNull Activity activity) {
        Configuration configuration = activity.getResources().getConfiguration();
        int requested = orientationFor(configuration.smallestScreenWidthDp,
                activity.isInMultiWindowMode());
        if (activity.getRequestedOrientation() != requested) {
            activity.setRequestedOrientation(requested);
        }
    }
}
