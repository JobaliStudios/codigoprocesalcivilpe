package com.jobalistudios.codigoprocesalcivilpe.glosario;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import androidx.annotation.NonNull;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;
import java.util.List;

/**
 * Añade únicamente spans. No modifica contenido legal, referencias, notas ni offsets.
 * Excluye toda referencia/callout interactivo y todo resaltado persistido.
 */
public final class GlossaryTextDecorator {
    private GlossaryTextDecorator() {}

    public static void apply(@NonNull SpannableString text, @NonNull ArticleBlock block,
                             @NonNull List<GlossaryRepository.Term> terms, int primaryColor,
                             @NonNull GlossaryTermSpan.Listener listener) {
        if (terms.isEmpty()) return; // el glosario vacío no recorre artículos ni spans
        for (Article article : block.articles) {
            final int articleStart = article.offsetInBlock;
            final int articleEnd = articleStart + article.text.length();
            if (articleStart < 0 || articleEnd > text.length()) continue;
            List<GlossaryMatcher.Match> matches = GlossaryMatcher.find(
                    article.text, terms, (localStart, localEnd) -> {
                        int start = articleStart + localStart;
                        int end = articleStart + localEnd;
                        // Todo span existente debe tener prioridad: referencias normativas,
                        // enlaces jurídicos, enlaces o resaltados del usuario.
                        return text.getSpans(start, end, ClickableSpan.class).length == 0
                                && text.getSpans(start, end,
                                HighlightController.UserHighlightSpan.class).length == 0;
                    });
            for (GlossaryMatcher.Match match : matches) {
                text.setSpan(new GlossaryTermSpan(match.term.getId(), primaryColor, listener),
                        articleStart + match.start, articleStart + match.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
