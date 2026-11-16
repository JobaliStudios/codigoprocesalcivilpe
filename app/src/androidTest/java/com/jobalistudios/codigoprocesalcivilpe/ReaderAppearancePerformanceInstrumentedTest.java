package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Medición REAL (emulador/dispositivo instrumentado) sobre el mayor bloque
 * jurídico. No guarda tiempos falsos ni modifica el corpus para la prueba.
 *
 * Los límites detectan bloqueos graves en cada actualización síncrona;
 * no sustituyen un perfilado manual de frames con Android Studio.
 */
@RunWith(AndroidJUnit4.class)
public final class ReaderAppearancePerformanceInstrumentedTest {
    private static final String TAG = "ReaderPremiumPerf";
    private static final String BLOCK_KEY = "seccionquintatit4cap2subcap1";
    private static final long TYPOGRAPHY_BUDGET_MS = 700L;
    private static final long THEME_BUDGET_MS = 1500L;

    @After
    public void after() {
        ReadingPreferenceManager.resetReadingPreferences(
                ApplicationProvider.getApplicationContext());
    }

    @Test
    public void realChapterAppearanceChangeDoesNotBlockMainThread() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleBlock actual = ArticleRepository.getBlock(context, BLOCK_KEY);
        assertNotNull("Es obligatorio medir con el corpus jurídico real", actual);
        int length = actual.fullText().length();
        assertTrue("El bloque de prueba debe tener más de 15 000 caracteres: " + length,
                length > 15000);

        int textId = context.getResources().getIdentifier(
                BLOCK_KEY, "string", context.getPackageName());
        assertTrue("Recurso legal no encontrado", textId != 0);
        Intent target = SectionContentActivity.createIntent(context,
                R.layout.activity_section_content, textId,
                R.string.home_dashboard_title, 0);
        List<String> measurements = new ArrayList<>();

        ReadingPreferenceManager.resetReadingPreferences(context);
        try (ActivityScenario<SectionContentActivity> scenario = ActivityScenario.launch(target)) {
            long lineTime = change(scenario, () -> ReadingPreferenceManager.setLineSpacing(
                    context, ReadingPreferenceManager.LineSpacing.WIDE));
            measurements.add("lineSpacing=" + lineTime + "ms");
            assertTrue("Interlineado bloqueó el hilo principal: " + lineTime,
                    lineTime < TYPOGRAPHY_BUDGET_MS);

            long marginTime = change(scenario, () -> ReadingPreferenceManager.setMargins(
                    context, ReadingPreferenceManager.Margins.WIDE));
            measurements.add("margins=" + marginTime + "ms");
            assertTrue("Márgenes bloquearon el hilo principal: " + marginTime,
                    marginTime < TYPOGRAPHY_BUDGET_MS);

            long themeTime = change(scenario, () -> ReadingPreferenceManager.setReaderTheme(
                    context, ReadingPreferenceManager.ReaderTheme.SEPIA));
            measurements.add("themeAndSpans=" + themeTime + "ms");
            assertTrue("Tema y spans bloquearon el hilo principal: " + themeTime,
                    themeTime < THEME_BUDGET_MS);
        }
        Log.i(TAG,"Bloque real " + BLOCK_KEY + " (" + length
                + " caracteres). Tiempos observados: " + measurements);
    }

    private long change(ActivityScenario<SectionContentActivity> scenario, Runnable settings) {
        final long[] result = new long[1];
        scenario.onActivity(activity -> {
            settings.run();
            long start = SystemClock.elapsedRealtimeNanos();
            activity.getSupportFragmentManager().setFragmentResult(
                    AppearanceBottomSheet.RESULT_KEY, new Bundle());
            result[0] = (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000L;
            Log.i(TAG,"Cambio observado en hilo principal: " + result[0] + "ms");
        });
        return result[0];
    }
}
