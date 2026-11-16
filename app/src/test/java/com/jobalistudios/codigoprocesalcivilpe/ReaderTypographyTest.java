package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.TextView;
import android.text.Layout;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
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

    @Test
    public void readerTypefaceSpacingAndJustificationDoNotAlterText() {
        Context context = ApplicationProvider.getApplicationContext();
        TextView article = new TextView(context);
        article.setText("Artículo 1.- Derecho a la tutela jurisdiccional efectiva.");
        ReadingPreferenceManager.setLineSpacing(context,
                ReadingPreferenceManager.LineSpacing.WIDE);
        ReadingPreferenceManager.setJustifyEnabled(context, true);
        ReadingPreferenceManager.setMargins(context,
                ReadingPreferenceManager.Margins.WIDE);
        try {
            ReaderTypography.applyReadingOptions(context, article);
            assertEquals("Artículo 1.- Derecho a la tutela jurisdiccional efectiva.",
                    article.getText().toString());
            assertEquals(1.9f, article.getLineSpacingMultiplier(), 0.001f);
            assertEquals(Layout.JUSTIFICATION_MODE_INTER_WORD, article.getJustificationMode());
            assertEquals(context.getResources().getDimensionPixelSize(
                    R.dimen.reader_margin_wide), article.getPaddingLeft());
        } finally {
            ReadingPreferenceManager.resetReadingPreferences(context);
        }
    }
}
