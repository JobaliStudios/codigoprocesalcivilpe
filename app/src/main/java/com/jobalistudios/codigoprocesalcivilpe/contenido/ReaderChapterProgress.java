package com.jobalistudios.codigoprocesalcivilpe.contenido;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Progreso por ordinal del artículo dentro del bloque, no por página ni longitud. */
public final class ReaderChapterProgress {
    public final int position;
    public final int total;

    private ReaderChapterProgress(int position, int total) {
        this.position = position;
        this.total = total;
    }

    @NonNull
    public static ReaderChapterProgress of(@Nullable ArticleBlock block,
                                            @Nullable String articleNumber) {
        if (block == null || block.articles.isEmpty()) return new ReaderChapterProgress(0, 0);
        int position = 0;
        if (articleNumber != null) {
            for (int i = 0; i < block.articles.size(); i++) {
                if (articleNumber.equals(block.articles.get(i).number)) {
                    position = i + 1;
                    break;
                }
            }
        }
        return new ReaderChapterProgress(position, block.articles.size());
    }

    public int percent() {
        return total <= 0 ? 0 : Math.round(100f * position / total);
    }

    public int progressOnScale(int max) {
        return total == 0 ? 0 : Math.round(max * position / (float) total);
    }
}
