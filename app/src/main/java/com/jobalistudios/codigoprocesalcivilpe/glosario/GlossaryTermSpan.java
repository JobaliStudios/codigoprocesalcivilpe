package com.jobalistudios.codigoprocesalcivilpe.glosario;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/** Un término editorial, distinguible por subrayado además del color de lectura. */
public final class GlossaryTermSpan extends ClickableSpan {
    public interface Listener {
        void onGlossaryTermSelected(@NonNull String termId);
    }

    @NonNull private final String termId;
    @ColorInt private final int primaryColor;
    @NonNull private final Listener listener;

    public GlossaryTermSpan(@NonNull String termId, @ColorInt int primaryColor,
                            @NonNull Listener listener) {
        this.termId = termId;
        this.primaryColor = primaryColor;
        this.listener = listener;
    }

    @NonNull public String getTermId() { return termId; }

    @Override
    public void onClick(@NonNull View widget) {
        listener.onGlossaryTermSelected(termId);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint paint) {
        paint.setColor(primaryColor);
        paint.setUnderlineText(true);
        paint.underlineColor = primaryColor;
        paint.underlineThickness = 1.0f;
    }
}
