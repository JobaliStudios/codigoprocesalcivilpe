package com.jobalistudios.codigoprocesalcivilpe.normativa;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

/** Acción superpuesta a una anotación existente; no cambia texto, color ni offsets. */
public final class NormativeHistorySpan extends ClickableSpan {

    public interface Listener {
        void onOpenNormativeHistory(@NonNull String articleNumber);
    }

    @NonNull private final String articleNumber;
    @NonNull private final Listener listener;

    public NormativeHistorySpan(
            @NonNull String articleNumber,
            @NonNull Listener listener
    ) {
        this.articleNumber = articleNumber;
        this.listener = listener;
    }

    @NonNull
    public String getArticleNumber() {
        return articleNumber;
    }

    @Override
    public void onClick(@NonNull View widget) {
        listener.onOpenNormativeHistory(articleNumber);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint drawState) {
        // NormativeCalloutSpan conserva toda la presentación visual.
    }
}
