package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;

/** Un artículo del código. Su texto incluye la línea de encabezado ("Artículo N.- ..."). */
public final class Article {

    /** Número canónico: "647", "647-A". */
    public final String number;
    /** Epígrafe del artículo (puede ser vacío). */
    public final String title;
    /** Texto exacto del artículo tal como aparece en el bloque. */
    public final String text;
    /** Índice donde empieza este artículo dentro del texto completo del bloque. */
    public final int offsetInBlock;

    Article(@NonNull String number, @NonNull String title, @NonNull String text, int offsetInBlock) {
        this.number = number;
        this.title = title;
        this.text = text;
        this.offsetInBlock = offsetInBlock;
    }
}
