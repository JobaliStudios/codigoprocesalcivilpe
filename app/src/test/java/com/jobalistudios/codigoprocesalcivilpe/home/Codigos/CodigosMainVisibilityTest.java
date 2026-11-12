package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import static org.junit.Assert.assertEquals;

import android.view.View;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CodigosMainVisibilityTest {

    @Test
    public void keepsCivilProcedureAvailableButHidesUnreleasedCodes() {
        CodigosMain activity = Robolectric.buildActivity(CodigosMain.class)
                .setup().get();

        assertEquals(View.VISIBLE,
                activity.findViewById(R.id.card_codigo_procesal).getVisibility());
        assertEquals(View.GONE,
                activity.findViewById(R.id.card_proximamente_header).getVisibility());
        assertEquals(View.GONE,
                activity.findViewById(R.id.layout_proximamente_content).getVisibility());
    }
}
