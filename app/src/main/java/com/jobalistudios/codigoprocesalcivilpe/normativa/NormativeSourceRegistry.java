package com.jobalistudios.codigoprocesalcivilpe.normativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Registro cerrado de enlaces oficiales previamente verificados en LEGAL_SOURCES.md.
 * Nunca construye ni busca URLs a partir del número de una norma.
 */
public final class NormativeSourceRegistry {

    private static final Map<String, String> OFFICIAL_SOURCES;

    static {
        Map<String, String> sources = new LinkedHashMap<>();
        sources.put("DECRETO LEGISLATIVO 768",
                "https://www.leyes.congreso.gob.pe/Documentos/DecretosLegislativos/00768.pdf");
        sources.put("LEY 32266",
                "https://api.congreso.gob.pe/spley-portal-service/archivo/MjY5MjM3/pdf");
        sources.put("LEY 32297",
                "https://busquedas.elperuano.pe/dispositivo/NL/2389754-2");
        sources.put("LEY 32377",
                "https://busquedas.elperuano.pe/dispositivo/NL/2407453-7");
        OFFICIAL_SOURCES = Collections.unmodifiableMap(sources);
    }

    @Nullable
    public String findOfficialSource(@Nullable String legalInstrument) {
        if (legalInstrument == null) {
            return null;
        }
        return OFFICIAL_SOURCES.get(normalizeIdentifier(legalInstrument));
    }

    @NonNull
    static String normalizeIdentifier(@NonNull String identifier) {
        return identifier.trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\s+N(?:\\.º|[.°º])?\\s*", " ")
                .replaceAll("\\s+", " ");
    }
}
