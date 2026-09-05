package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Vista inmutable del contenido personal asociado a un artículo. */
public final class ArticleNotesEntry {
    private final String articleNumber;
    private final String articleTitle;
    private final boolean favorite;
    private final List<HighlightEntry> highlights;
    private final List<NoteEntry> articleNotes;

    public ArticleNotesEntry(
            @NonNull String articleNumber,
            @Nullable String articleTitle,
            boolean favorite,
            @NonNull List<HighlightEntry> highlights,
            @NonNull List<NoteEntry> articleNotes
    ) {
        this.articleNumber = articleNumber;
        this.articleTitle = articleTitle == null ? "" : articleTitle;
        this.favorite = favorite;
        this.highlights = Collections.unmodifiableList(new ArrayList<>(highlights));
        this.articleNotes = Collections.unmodifiableList(new ArrayList<>(articleNotes));
    }

    @NonNull
    public String getArticleNumber() {
        return articleNumber;
    }

    @NonNull
    public String getArticleTitle() {
        return articleTitle;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @NonNull
    public List<HighlightEntry> getHighlights() {
        return highlights;
    }

    @NonNull
    public List<NoteEntry> getArticleNotes() {
        return articleNotes;
    }

    public boolean hasContent() {
        return favorite || !highlights.isEmpty() || !articleNotes.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ArticleNotesEntry)) {
            return false;
        }
        ArticleNotesEntry other = (ArticleNotesEntry) object;
        return favorite == other.favorite
                && articleNumber.equals(other.articleNumber)
                && articleTitle.equals(other.articleTitle)
                && highlights.equals(other.highlights)
                && articleNotes.equals(other.articleNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleNumber, articleTitle, favorite, highlights, articleNotes);
    }

    /** Resaltado vigente; la nota asociada se conserva junto a él para mantener su contexto. */
    public static final class HighlightEntry {
        private final String text;
        private final String note;
        private final int start;
        private final int end;
        private final long createdAt;

        public HighlightEntry(
                @NonNull String text,
                @Nullable String note,
                int start,
                int end,
                long createdAt
        ) {
            this.text = text;
            this.note = hasVisibleText(note) ? note : "";
            this.start = start;
            this.end = end;
            this.createdAt = createdAt;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public String getNote() {
            return note;
        }

        public boolean hasNote() {
            return !note.isEmpty();
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof HighlightEntry)) {
                return false;
            }
            HighlightEntry other = (HighlightEntry) object;
            return start == other.start
                    && end == other.end
                    && createdAt == other.createdAt
                    && text.equals(other.text)
                    && note.equals(other.note);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, note, start, end, createdAt);
        }
    }

    /** Nota general creada con la acción de nota rápida del artículo. */
    public static final class NoteEntry {
        private final String text;
        private final long createdAt;

        public NoteEntry(@NonNull String text, long createdAt) {
            this.text = text;
            this.createdAt = createdAt;
        }

        @NonNull
        public String getText() {
            return text;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof NoteEntry)) {
                return false;
            }
            NoteEntry other = (NoteEntry) object;
            return createdAt == other.createdAt && text.equals(other.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, createdAt);
        }
    }

    static boolean hasVisibleText(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }
}
