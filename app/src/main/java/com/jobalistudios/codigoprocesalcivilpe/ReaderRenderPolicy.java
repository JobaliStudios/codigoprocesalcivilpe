package com.jobalistudios.codigoprocesalcivilpe;

import androidx.annotation.NonNull;

/**
 * Evita recomponer ~18 mil caracteres de texto jurídico si solo cambian
 * propiedades de TextView: fuente, interlineado, márgenes o justificación.
 * El color, la escala de los callouts, el glosario sí cambian los spans.
 */
public final class ReaderRenderPolicy {
    private ReaderRenderPolicy() {}

    @NonNull
    public static String spanSignature(@NonNull ReaderAppearancePalette.ResolvedTheme theme,
                                       float bodySizePx, boolean glossaryEnabled) {
        return theme.name() + ":" + Float.floatToIntBits(bodySizePx)
                + ":" + glossaryEnabled;
    }

    public static boolean needsRebuild(String previousSignature, String currentSignature) {
        return previousSignature == null || !previousSignature.equals(currentSignature);
    }
}
