package com.jobalistudios.codigoprocesalcivilpe;

/** Métricas del cuerpo jurídico derivadas de la preferencia Aa de la aplicación. */
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
}
