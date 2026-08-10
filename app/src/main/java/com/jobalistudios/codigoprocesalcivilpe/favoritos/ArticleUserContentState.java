package com.jobalistudios.codigoprocesalcivilpe.favoritos;

/** Presencia de datos personales asociados a un artículo, sin exponer su contenido. */
public final class ArticleUserContentState {
    public static final ArticleUserContentState EMPTY =
            new ArticleUserContentState(false, false);

    private final boolean hasNote;
    private final boolean hasHighlight;

    public ArticleUserContentState(boolean hasNote, boolean hasHighlight) {
        this.hasNote = hasNote;
        this.hasHighlight = hasHighlight;
    }

    public boolean hasNote() {
        return hasNote;
    }

    public boolean hasHighlight() {
        return hasHighlight;
    }
}
