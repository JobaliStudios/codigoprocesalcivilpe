package com.jobalistudios.codigoprocesalcivilpe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleShareFormatter;

/**
 * Adaptador Android para Copiar y Compartir el artículo visible.
 * El lector decide cuál es el artículo; este componente no consulta ni
 * modifica favoritos, historial, notas, preferencias o contenido jurídico.
 */
final class ReaderArticleTransferController {
    private final Context context;
    private final ArticleShareFormatter formatter;

    ReaderArticleTransferController(@NonNull Context context) {
        this.context = context;
        this.formatter = new ArticleShareFormatter(context.getString(R.string.app_name));
    }

    /** Devuelve false únicamente si Android no proporciona portapapeles. */
    boolean copy(@NonNull Article article) {
        ClipboardManager clipboard = context.getSystemService(ClipboardManager.class);
        if (clipboard == null) {
            return false;
        }

        String label = context.getString(R.string.article_clipboard_label, article.number);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, formatter.format(article)));
        return true;
    }

    @NonNull
    Intent createShareChooser(@NonNull Article article) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.article_share_subject, article.number)
        );
        shareIntent.putExtra(Intent.EXTRA_TEXT, formatter.format(article));
        return Intent.createChooser(
                shareIntent,
                context.getString(R.string.article_share_chooser_title)
        );
    }
}
