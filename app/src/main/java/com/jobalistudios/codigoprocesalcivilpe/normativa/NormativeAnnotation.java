package com.jobalistudios.codigoprocesalcivilpe.normativa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Metadatos extraídos exclusivamente del texto de una anotación normativa. */
public final class NormativeAnnotation {

    public enum Type {
        MODIFIED,
        INCORPORATED,
        SUBSTITUTED,
        REPEALED,
        OTHER
    }

    private static final Map<String, String> ABBREVIATED_MONTHS;

    static {
        Map<String, String> months = new LinkedHashMap<>();
        months.put("enero", "ene.");
        months.put("febrero", "feb.");
        months.put("marzo", "mar.");
        months.put("abril", "abr.");
        months.put("mayo", "may.");
        months.put("junio", "jun.");
        months.put("julio", "jul.");
        months.put("agosto", "ago.");
        months.put("setiembre", "set.");
        months.put("septiembre", "set.");
        months.put("octubre", "oct.");
        months.put("noviembre", "nov.");
        months.put("diciembre", "dic.");
        ABBREVIATED_MONTHS = Collections.unmodifiableMap(months);
    }

    public final int start;
    public final int end;
    @NonNull public final String rawText;
    @NonNull public final Type type;
    @Nullable public final String legalInstrument;
    @Nullable public final String publicationDate;

    public NormativeAnnotation(
            int start,
            int end,
            @NonNull String rawText,
            @NonNull Type type,
            @Nullable String legalInstrument,
            @Nullable String publicationDate
    ) {
        this.start = start;
        this.end = end;
        this.rawText = rawText;
        this.type = type;
        this.legalInstrument = legalInstrument;
        this.publicationDate = publicationDate;
    }

    /** Resumen puramente visual; nunca se inserta en el contenido jurídico. */
    @Nullable
    public String compactSummary() {
        if (legalInstrument == null) {
            return null;
        }
        String compactDate = compactPublicationDate();
        return compactDate == null
                ? legalInstrument
                : legalInstrument + " · " + compactDate;
    }

    @Nullable
    private String compactPublicationDate() {
        if (publicationDate == null) {
            return null;
        }
        String[] parts = publicationDate.trim().split("\\s+");
        if (parts.length != 5
                || !"de".equalsIgnoreCase(parts[1])
                || !"de".equalsIgnoreCase(parts[3])) {
            return publicationDate;
        }
        String month = ABBREVIATED_MONTHS.get(parts[2].toLowerCase(Locale.ROOT));
        if (month == null) {
            return publicationDate;
        }
        return parts[0] + " " + month + " " + parts[4];
    }
}
