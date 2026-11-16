package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.text.Layout;
import android.util.TypedValue;
import android.widget.TextView;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

/** Métricas del cuerpo jurídico y opciones tipográficas; no cambia el texto ni sus spans. */
public final class ReaderTypography {
    public static final float HEADING_SCALE = 1.15f;
    public static final float ANNOTATION_TITLE_SCALE = 0.92f;
    public static final float ANNOTATION_SUMMARY_SCALE = 0.86f;
    public static final float LINE_SPACING_SCALE = 0.20f;

    private final float bodySizePx;

    private ReaderTypography(float bodySizePx) {
        this.bodySizePx = bodySizePx;
    }

    public static ReaderTypography create(float baseBodySizePx, float appScale) {
        return new ReaderTypography(baseBodySizePx * appScale);
    }

    public float getBodySizePx() { return bodySizePx; }
    public float getHeadingSizePx() { return bodySizePx * HEADING_SCALE; }
    public float getAnnotationTitleSizePx() { return bodySizePx * ANNOTATION_TITLE_SCALE; }
    public float getAnnotationSummarySizePx() { return bodySizePx * ANNOTATION_SUMMARY_SCALE; }
    public float getLineSpacingExtraPx() { return bodySizePx * LINE_SPACING_SCALE; }

    /** Solo el TextView jurídico adopta la fuente; la interfaz queda en Sans del sistema. */
    public static void applyReadingOptions(Context context, TextView articleText) {
        articleText.setTypeface(ReaderAppearancePalette.getBodyTypeface(context));
        articleText.setLineSpacing(0f,
                ReadingPreferenceManager.getLineSpacing(context).multiplier);
        articleText.setBreakStrategy(android.graphics.text.LineBreaker.BREAK_STRATEGY_HIGH_QUALITY);
        articleText.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
        articleText.setJustificationMode(ReadingPreferenceManager.isJustifyEnabled(context)
                ? android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD : android.graphics.text.LineBreaker.JUSTIFICATION_MODE_NONE);

        int marginResource;
        switch (ReadingPreferenceManager.getMargins(context)) {
            case NARROW: marginResource = R.dimen.reader_margin_narrow; break;
            case WIDE: marginResource = R.dimen.reader_margin_wide; break;
            case NORMAL:
            default: marginResource = R.dimen.reader_margin_normal;
        }
        int margin = context.getResources().getDimensionPixelSize(marginResource);
        articleText.setPadding(margin, articleText.getPaddingTop(),
                margin, articleText.getPaddingBottom());
    }
}
