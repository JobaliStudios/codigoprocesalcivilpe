package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReaderTypographyTest {

    @Test
    public void appReaderScale_changesEveryLegalTextMetricProportionally() {
        ReaderTypography normal = ReaderTypography.create(20f, 1f);
        ReaderTypography enlarged = ReaderTypography.create(20f, 1.5f);

        assertEquals(20f, normal.getBodySizePx(), 0.001f);
        assertEquals(30f, enlarged.getBodySizePx(), 0.001f);
        assertEquals(normal.getHeadingSizePx() * 1.5f,
                enlarged.getHeadingSizePx(), 0.001f);
        assertEquals(normal.getAnnotationTitleSizePx() * 1.5f,
                enlarged.getAnnotationTitleSizePx(), 0.001f);
        assertEquals(normal.getAnnotationSummarySizePx() * 1.5f,
                enlarged.getAnnotationSummarySizePx(), 0.001f);
        assertEquals(normal.getLineSpacingExtraPx() * 1.5f,
                enlarged.getLineSpacingExtraPx(), 0.001f);
    }
}
