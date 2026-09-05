package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/** Salida de texto plano; puede convivir luego con otro exportador sobre el mismo modelo. */
public final class UserNotesTextFormatter {
    private static final String ARTICLE_SEPARATOR = "--------------------------------";

    private final String appName;

    public UserNotesTextFormatter(@NonNull String appName) {
        this.appName = appName;
    }

    @NonNull
    public String format(@NonNull List<ArticleNotesEntry> entries) {
        if (entries.isEmpty()) {
            return "";
        }
        List<String> articles = new ArrayList<>();
        for (ArticleNotesEntry entry : entries) {
            articles.add(formatArticle(entry));
        }
        return "MIS APUNTES\n" + appName + "\n\n"
                + String.join("\n\n" + ARTICLE_SEPARATOR + "\n\n", articles);
    }

    private String formatArticle(ArticleNotesEntry entry) {
        List<String> blocks = new ArrayList<>();
        String heading = "Artículo " + entry.getArticleNumber();
        if (!entry.getArticleTitle().isEmpty()) {
            heading += " — " + entry.getArticleTitle();
        }
        blocks.add(heading);
        if (entry.isFavorite()) {
            blocks.add("Favorito");
        }
        for (ArticleNotesEntry.HighlightEntry highlight : entry.getHighlights()) {
            blocks.add("Resaltado:\n" + highlight.getText());
            if (highlight.hasNote()) {
                blocks.add("Mi nota:\n" + highlight.getNote());
            }
        }
        for (ArticleNotesEntry.NoteEntry note : entry.getArticleNotes()) {
            blocks.add("Nota del artículo:\n" + note.getText());
        }
        return String.join("\n\n", blocks);
    }
}
