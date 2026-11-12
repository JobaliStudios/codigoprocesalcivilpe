package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class AboutActivityTest {

    @Test
    public void showsVersionDeveloperAndIndependenceWithoutClaimingAutoUpdates() {
        AboutActivity activity = Robolectric.buildActivity(AboutActivity.class)
                .setup().get();

        TextView version = activity.findViewById(R.id.aboutVersion);
        TextView developer = activity.findViewById(R.id.aboutDeveloper);
        TextView independence = activity.findViewById(R.id.aboutIndependence);

        assertTrue(version.getText().toString().contains("Código Procesal Civil PE"));
        assertEquals("Jobali Studios", developer.getText().toString());
        assertTrue(independence.getText().toString().contains("No es una fuente oficial"));
    }

    @Test
    public void exposesSourceCutoffAndExplicitlyUncertifiedCurrency() {
        AboutActivity activity = Robolectric.buildActivity(AboutActivity.class)
                .setup().get();
        TextView scope = activity.findViewById(R.id.aboutLegalScope);
        String notice = scope.getText().toString();
        assertTrue(notice.contains("7 de junio de 2025"));
        assertTrue(notice.contains("no certifica la vigencia íntegra"));
        assertTrue(notice.contains("fuentes oficiales"));
    }

    @Test
    public void officialSourceAndPrivacyButtonsOpenCorrectWebsites() {
        AboutActivity activity = Robolectric.buildActivity(AboutActivity.class)
                .setup().get();

        activity.findViewById(R.id.buttonElPeruano).performClick();
        Intent first = shadowOf(activity).getNextStartedActivity();
        assertEquals(Intent.ACTION_VIEW, first.getAction());
        assertTrue(first.getDataString().contains("diariooficial.elperuano.pe"));

        activity.findViewById(R.id.buttonSpij).performClick();
        Intent second = shadowOf(activity).getNextStartedActivity();
        assertTrue(second.getDataString().startsWith("https://spij.minjus.gob.pe/"));

        activity.findViewById(R.id.buttonUpdatedNorms).performClick();
        Intent catalog = shadowOf(activity).getNextStartedActivity();
        assertEquals(Intent.ACTION_VIEW, catalog.getAction());
        assertEquals("https://diariooficial.elperuano.pe/normas/normasactualizadas",
                catalog.getDataString());

        activity.findViewById(R.id.buttonPrivacyPolicy).performClick();
        Intent third = shadowOf(activity).getNextStartedActivity();
        assertTrue(third.getDataString().contains("jobalistudios.github.io/privacy-policy"));
    }
}
