package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
public class LegalUpdateDisclosureTest {

    @Test
    public void visibleDescriptions_matchBundledOfflineUpdateModel() {
        Context context = ApplicationProvider.getApplicationContext();
        String homeDescription = (context.getString(R.string.home_dashboard_subtitle) + " "
                + context.getString(R.string.home_offline_available))
                .toLowerCase(Locale.ROOT);
        String contentSubtitle = context.getString(R.string.content_verified_date)
                .toLowerCase(Locale.ROOT);

        assertTrue(homeDescription.contains("sin conexión"));
        assertTrue(contentSubtitle.contains(
                "contenido normativo verificado al 8 de agosto de 2026"));

        String combined = homeDescription + " " + contentSubtitle + " "
                + context.getString(R.string.codigos_codigo_procesal_civil_title) + " "
                + context.getString(R.string.quiz_cpc_ready_subtitle);
        assertFalse(combined.contains("2025"));
        assertFalse(combined.toLowerCase(Locale.ROOT).contains("actualización automática"));
    }
}
