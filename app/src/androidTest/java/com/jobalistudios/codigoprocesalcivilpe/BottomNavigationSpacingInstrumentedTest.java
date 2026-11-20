package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.view.ViewGroup;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Checks actual rendered spacing on an Android device, not only XML dimensions. */
@RunWith(AndroidJUnit4.class)
public class BottomNavigationSpacingInstrumentedTest {

    @Test
    public void everyTabKeepsIconCloseToLabelAndNavigatesWithTheSameFourIds() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                BottomNavigationView bar = activity.findViewById(R.id.nav_view);
                assertNotNull(bar);

                int[] ids = { R.id.navigation_home, R.id.navigation_busqueda,
                        R.id.navigation_favoritos, R.id.navigation_configuracion };
                assertEquals(ids.length, bar.getMenu().size());

                ViewGroup menuView = (ViewGroup) bar.getChildAt(0);
                assertNotNull(menuView);
                assertEquals(4, menuView.getChildCount());

                int maxSpacing = (int) (12 * activity.getResources()
                        .getDisplayMetrics().density + 0.5f);

                for (int i = 0; i < ids.length; i++) {
                    assertEquals(ids[i], bar.getMenu().getItem(i).getItemId());
                    View item = menuView.getChildAt(i);
                    View icon = item.findViewById(
                            com.google.android.material.R.id.navigation_bar_item_icon_container);
                    View labels = item.findViewById(
                            com.google.android.material.R.id.navigation_bar_item_labels_group);
                    assertNotNull(icon);
                    assertNotNull(labels);

                    // Use visual Y, including the prototype's translationY centering.
                    float spacing = labels.getY() - (icon.getY() + icon.getHeight());
                    assertTrue("Icon/label overlap in tab " + i + ": " + spacing + " px",
                            spacing >= 0);
                    assertTrue("Excessive icon/label gap in tab " + i + ": "
                            + spacing + " px", spacing <= maxSpacing);
                }
            });
        }
    }
}
