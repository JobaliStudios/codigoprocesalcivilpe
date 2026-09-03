package com.jobalistudios.codigoprocesalcivilpe.lectura;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

/** Obtiene el rango jurídico exacto de un artículo sin consultar ni modificar la UI. */
public final class ArticleSpeechContentResolver {

    @Nullable
    public ArticleSpeechContent resolve(
            @NonNull ArticleBlock block,
            @NonNull String articleNumber
    ) {
        String wanted = ArticleRepository.normalizeArticleNumber(articleNumber);
        String blockText = block.fullText();
        for (int index = 0; index < block.articles.size(); index++) {
            Article article = block.articles.get(index);
            if (!ArticleRepository.normalizeArticleNumber(article.number).equals(wanted)) {
                continue;
            }

            int start = article.offsetInBlock;
            int end = index + 1 < block.articles.size()
                    ? block.articles.get(index + 1).offsetInBlock
                    : blockText.length();
            if (start < 0 || end < start || end > blockText.length()) {
                return null;
            }
            return new ArticleSpeechContent(
                    article.number,
                    article.title,
                    blockText.substring(start, end)
            );
        }
        return null;
    }
}
