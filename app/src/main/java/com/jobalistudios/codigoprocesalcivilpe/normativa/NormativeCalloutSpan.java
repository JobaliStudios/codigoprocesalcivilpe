package com.jobalistudios.codigoprocesalcivilpe.normativa;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Dibuja un callout alrededor de un rango existente. No reemplaza ni agrega caracteres,
 * por lo que el texto legal conserva selección, búsqueda, copia y offsets originales.
 */
public final class NormativeCalloutSpan implements
        LeadingMarginSpan, LineBackgroundSpan, LineHeightSpan.WithDensity {

    private final int annotationStart;
    private final int annotationEnd;
    private final int backgroundColor;
    private final int borderColor;
    private final int titleColor;
    private final int summaryColor;
    private final float borderWidth;
    private final int horizontalPadding;
    private final int verticalPadding;
    private final float titleTextSize;
    private final float summaryTextSize;
    @NonNull private final String title;
    @Nullable private final String summary;

    public NormativeCalloutSpan(
            int annotationStart,
            int annotationEnd,
            int backgroundColor,
            int borderColor,
            int titleColor,
            int summaryColor,
            float borderWidth,
            int horizontalPadding,
            int verticalPadding,
            float titleTextSize,
            float summaryTextSize,
            @NonNull String title,
            @Nullable String summary
    ) {
        this.annotationStart = annotationStart;
        this.annotationEnd = annotationEnd;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.titleColor = titleColor;
        this.summaryColor = summaryColor;
        this.borderWidth = borderWidth;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        this.titleTextSize = titleTextSize;
        this.summaryTextSize = summaryTextSize;
        this.title = title;
        this.summary = summary;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getSummary() {
        return summary;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return Math.round(borderWidth) + horizontalPadding;
    }

    @Override
    public void drawLeadingMargin(
            Canvas canvas,
            Paint paint,
            int x,
            int dir,
            int top,
            int baseline,
            int bottom,
            CharSequence text,
            int start,
            int end,
            boolean first,
            Layout layout
    ) {
        // El fondo y el indicador se dibujan juntos en drawBackground().
    }

    @Override
    public void drawBackground(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            int left,
            int right,
            int top,
            int baseline,
            int bottom,
            @NonNull CharSequence text,
            int start,
            int end,
            int lineNumber
    ) {
        int oldColor = paint.getColor();
        Paint.Style oldStyle = paint.getStyle();
        float oldTextSize = paint.getTextSize();
        Typeface oldTypeface = paint.getTypeface();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawRect(left, top, right, bottom, paint);
        paint.setColor(borderColor);
        canvas.drawRect(left, top, left + borderWidth, bottom, paint);

        if (isFirstLine(start, end)) {
            float textLeft = left + borderWidth + horizontalPadding;
            paint.setColor(titleColor);
            paint.setTextSize(titleTextSize);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            Paint.FontMetrics titleMetrics = paint.getFontMetrics();
            float titleBaseline = top + verticalPadding - titleMetrics.ascent;
            canvas.drawText(title, textLeft, titleBaseline, paint);

            if (summary != null) {
                paint.setColor(summaryColor);
                paint.setTextSize(summaryTextSize);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                Paint.FontMetrics summaryMetrics = paint.getFontMetrics();
                float summaryBaseline = titleBaseline + titleMetrics.descent
                        + Math.max(1f, verticalPadding / 2f) - summaryMetrics.ascent;
                canvas.drawText(summary, textLeft, summaryBaseline, paint);
            }
        }

        paint.setColor(oldColor);
        paint.setStyle(oldStyle);
        paint.setTextSize(oldTextSize);
        paint.setTypeface(oldTypeface);
    }

    @Override
    public void chooseHeight(
            CharSequence text,
            int start,
            int end,
            int spanStartVertical,
            int vertical,
            Paint.FontMetricsInt metrics
    ) {
        chooseHeight(text, start, end, spanStartVertical, vertical, metrics, null);
    }

    @Override
    public void chooseHeight(
            CharSequence text,
            int start,
            int end,
            int spanStartVertical,
            int vertical,
            Paint.FontMetricsInt metrics,
            TextPaint textPaint
    ) {
        if (isFirstLine(start, end)) {
            int headerHeight = Math.round(titleTextSize + verticalPadding * 2f);
            if (summary != null) {
                headerHeight += Math.round(summaryTextSize + verticalPadding / 2f);
            }
            metrics.ascent -= headerHeight;
            metrics.top -= headerHeight;
        }
        if (isLastLine(start, end)) {
            metrics.descent += verticalPadding;
            metrics.bottom += verticalPadding;
        }
    }

    private boolean isFirstLine(int lineStart, int lineEnd) {
        return lineStart <= annotationStart && annotationStart < lineEnd;
    }

    private boolean isLastLine(int lineStart, int lineEnd) {
        return lineStart < annotationEnd && annotationEnd <= lineEnd;
    }
}
