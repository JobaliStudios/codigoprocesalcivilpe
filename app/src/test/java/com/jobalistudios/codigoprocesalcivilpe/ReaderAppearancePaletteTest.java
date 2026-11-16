package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.core.graphics.ColorUtils;
import androidx.test.core.app.ApplicationProvider;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ReaderAppearancePaletteTest {
    private final Context context = ApplicationProvider.getApplicationContext();

    @After public void clear() {
        ReadingPreferenceManager.resetReadingPreferences(context);
    }

    @Test public void autoThemeFollowsEffectiveAppMode() {
        assertEquals(ReaderAppearancePalette.ResolvedTheme.LIGHT,
                ReaderAppearancePalette.resolve(ReadingPreferenceManager.ReaderTheme.AUTO, false));
        assertEquals(ReaderAppearancePalette.ResolvedTheme.DARK,
                ReaderAppearancePalette.resolve(ReadingPreferenceManager.ReaderTheme.AUTO, true));
    }

    @Test public void explicitThemesIgnoreGlobalNightMode() {
        for (boolean appNight : new boolean[] {false, true}) {
            assertEquals(ReaderAppearancePalette.ResolvedTheme.LIGHT,
                    ReaderAppearancePalette.resolve(
                            ReadingPreferenceManager.ReaderTheme.LIGHT, appNight));
            assertEquals(ReaderAppearancePalette.ResolvedTheme.SEPIA,
                    ReaderAppearancePalette.resolve(
                            ReadingPreferenceManager.ReaderTheme.SEPIA, appNight));
            assertEquals(ReaderAppearancePalette.ResolvedTheme.DARK,
                    ReaderAppearancePalette.resolve(
                            ReadingPreferenceManager.ReaderTheme.DARK, appNight));
        }
    }

    @Test public void allReadingPalettesHaveAtLeastWcagTextContrast() {
        for (ReadingPreferenceManager.ReaderTheme theme :
                new ReadingPreferenceManager.ReaderTheme[] {
                        ReadingPreferenceManager.ReaderTheme.LIGHT,
                        ReadingPreferenceManager.ReaderTheme.SEPIA,
                        ReadingPreferenceManager.ReaderTheme.DARK}) {
            ReadingPreferenceManager.setReaderTheme(context, theme);
            ReaderAppearancePalette colors = ReaderAppearancePalette.from(context);
            assertTrue(theme + " body/background",
                    ColorUtils.calculateContrast(colors.text, colors.background) >= 4.5d);
            assertTrue(theme + " muted/background",
                    ColorUtils.calculateContrast(colors.muted, colors.background) >= 4.5d);
            assertTrue(theme + " body/surface",
                    ColorUtils.calculateContrast(colors.text, colors.surface) >= 4.5d);
        }
    }
    @Test public void fourSavedHighlightColorsRemainReadableWithDarkReader() {
        ReadingPreferenceManager.setReaderTheme(context,
                ReadingPreferenceManager.ReaderTheme.DARK);
        ReaderAppearancePalette colors = ReaderAppearancePalette.from(context);
        for (int id : new int[] {
                R.color.reader_dark_user_highlight_yellow,
                R.color.reader_dark_user_highlight_orange,
                R.color.reader_dark_user_highlight_green,
                R.color.reader_dark_user_highlight_blue}) {
            int overlay = androidx.core.content.ContextCompat.getColor(context, id);
            int composite = ColorUtils.compositeColors(overlay, colors.background);
            assertTrue("Highlight " + id,
                    ColorUtils.calculateContrast(colors.text, composite) >= 4.5d);
        }
    }
}
