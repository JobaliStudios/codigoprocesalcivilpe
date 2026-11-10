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
import java.lang.reflect.Field;
import java.util.regex.Pattern;

@RunWith(RobolectricTestRunner.class)
public class LegalUpdateDisclosureTest {

    @Test
    public void allVisibleStrings_doNotPromiseAutomaticLegalUpdates() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        Pattern unsupportedPromise = Pattern.compile(
                "actualización\\s+automática|se\\s+actualiza\\s+automáticamente|"
                        + "siempre\\s+actualizad[oa]s?",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        for (Field resource : R.string.class.getFields()) {
            if (resource.getType() != int.class) continue;
            String value = context.getString(resource.getInt(null));
            assertFalse("Promesa no respaldada en " + resource.getName(),
                    unsupportedPromise.matcher(value).find());
        }
    }

    @Test
    public void publicBranding_doesNotIncludeAYear() {
        Context context = ApplicationProvider.getApplicationContext();
        for (int resource : new int[]{R.string.app_name,
                R.string.codigos_codigo_procesal_civil_title}) {
            assertFalse(Pattern.compile("\\b20\\d{2}\\b")
                    .matcher(context.getString(resource)).find());
        }
    }

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
