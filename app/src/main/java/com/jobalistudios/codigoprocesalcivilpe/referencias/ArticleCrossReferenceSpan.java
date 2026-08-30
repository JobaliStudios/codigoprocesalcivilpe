package com.jobalistudios.codigoprocesalcivilpe.referencias;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/** ClickableSpan identificable: color semántico y subrayado para no depender solo del color. */
public final class ArticleCrossReferenceSpan extends ClickableSpan {
    public interface Listener {
        void onArticleReferenceClicked(@NonNull String articleNumber);
    }

    @NonNull private final String articleNumber;
    @ColorInt private final int color;
    @NonNull private final Listener listener;

    public ArticleCrossReferenceSpan(
            @NonNull String articleNumber,
            @ColorInt int color,
            @NonNull Listener listener
    ) {
        this.articleNumber = articleNumber;
        this.color = color;
        this.listener = listener;
    }

    @NonNull
    public String getArticleNumber() {
        return articleNumber;
    }

    @Override
    public void onClick(@NonNull View widget) {
        listener.onArticleReferenceClicked(articleNumber);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint drawState) {
        drawState.setColor(color);
        drawState.setUnderlineText(true);
    }
}
