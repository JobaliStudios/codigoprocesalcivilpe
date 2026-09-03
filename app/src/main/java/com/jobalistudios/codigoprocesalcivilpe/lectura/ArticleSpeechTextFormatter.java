package com.jobalistudios.codigoprocesalcivilpe.lectura;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/** Normalización mínima aplicada solo a la copia enviada al motor de voz. */
public final class ArticleSpeechTextFormatter {

    private static final Pattern ARTICLE_HEADER_PUNCTUATION = Pattern.compile(
            "(?iu)(Artículo\\s+\\d+(?:-[A-Z])?)\\s*\\.\\-"
    );
    private static final Pattern BRACKETED_DEROGATION = Pattern.compile(
            "(?iu)\\[\\s*(Derogad[oa])\\s*]\\.?"
    );

    @NonNull
    public String format(@NonNull String rawText) {
        String speechCopy = ARTICLE_HEADER_PUNCTUATION.matcher(rawText)
                .replaceAll("$1.");
        speechCopy = BRACKETED_DEROGATION.matcher(speechCopy)
                .replaceAll("$1.");
        return speechCopy.trim();
    }
}
