package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import android.content.Intent;

import androidx.annotation.NonNull;

/** Crea el Intent de texto en memoria, sin archivos temporales ni permisos de almacenamiento. */
public final class UserNotesShareIntentFactory {
    private UserNotesShareIntentFactory() {
    }

    @NonNull
    public static Intent create(@NonNull String text) {
        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, text);
    }
}
