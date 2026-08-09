package com.jobalistudios.codigoprocesalcivilpe.historial;

import androidx.annotation.NonNull;

/** Artículo consultado recientemente y almacenado sólo en el dispositivo. */
public final class RecentArticle {

    private final String number;
    private final String title;
    private final long lastViewedAt;

    RecentArticle(@NonNull String number, @NonNull String title, long lastViewedAt) {
        this.number = number;
        this.title = title;
        this.lastViewedAt = lastViewedAt;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }
}
