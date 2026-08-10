package com.jobalistudios.codigoprocesalcivilpe.contenido;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Resuelve anterior/actual/siguiente según el orden jurídico real del repositorio. */
public final class ArticleSequenceResolver {
    private ArticleSequenceResolver() {
    }

    @Nullable
    public static Result resolve(@NonNull Context context, @Nullable String articleNumber) {
        if (articleNumber == null) {
            return null;
        }
        String wanted = articleNumber.trim().toUpperCase(Locale.ROOT);
        List<ArticleRepository.Location> sequence = buildSequence(context);
        for (int index = 0; index < sequence.size(); index++) {
            ArticleRepository.Location location = sequence.get(index);
            if (location.article.number.equals(wanted)) {
                return new Result(
                        index > 0 ? sequence.get(index - 1) : null,
                        location,
                        index + 1 < sequence.size() ? sequence.get(index + 1) : null
                );
            }
        }
        return null;
    }

    @NonNull
    static List<ArticleRepository.Location> buildSequence(@NonNull Context context) {
        List<ArticleRepository.Location> sequence = new ArrayList<>();
        Set<String> numbers = new HashSet<>();
        for (ArticleBlock block : ArticleRepository.getBlocks(context).values()) {
            for (Article article : block.articles) {
                if (!numbers.add(article.number)) {
                    throw new IllegalStateException(
                            "Número de artículo duplicado en la secuencia: " + article.number
                    );
                }
                sequence.add(new ArticleRepository.Location(block, article));
            }
        }
        return sequence;
    }

    public static final class Result {
        @Nullable
        public final ArticleRepository.Location previous;
        @NonNull
        public final ArticleRepository.Location current;
        @Nullable
        public final ArticleRepository.Location next;

        private Result(
                @Nullable ArticleRepository.Location previous,
                @NonNull ArticleRepository.Location current,
                @Nullable ArticleRepository.Location next
        ) {
            this.previous = previous;
            this.current = current;
            this.next = next;
        }
    }
}
