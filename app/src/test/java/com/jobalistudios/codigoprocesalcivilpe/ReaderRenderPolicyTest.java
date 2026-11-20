package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/** El cambio puramente tipográfico evita reparsear spans sobre contenido legal grande. */
public class ReaderRenderPolicyTest {
    @Test public void sameSpanStyleIsReused() {
        String first = ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.LIGHT, 40.0f, true);
        assertFalse(ReaderRenderPolicy.needsRebuild(first, first));
        assertTrue(ReaderRenderPolicy.needsRebuild(null, first));
    }

    @Test public void paletteSizeAndGlossaryTriggerReformat() {
        String base = ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.LIGHT, 40.0f, true);
        assertNotEquals(base, ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.SEPIA, 40.0f, true));
        assertNotEquals(base, ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.LIGHT, 44.0f, true));
        assertNotEquals(base, ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.LIGHT, 40.0f, false));
        assertTrue(ReaderRenderPolicy.needsRebuild(base, ReaderRenderPolicy.spanSignature(
                ReaderAppearancePalette.ResolvedTheme.DARK, 40.0f, true)));
    }
}
