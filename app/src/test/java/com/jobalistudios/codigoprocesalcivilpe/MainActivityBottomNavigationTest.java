package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.res.ColorStateList;
import android.graphics.Color;

import org.robolectric.annotation.Config;

import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

/** UI-only changes: the original NavigationUI graph and destination IDs must remain intact. */
@RunWith(RobolectricTestRunner.class)
public class MainActivityBottomNavigationTest {
    private ActivityController<MainActivity> controller;

    private MainActivity launch() {
        controller = Robolectric.buildActivity(MainActivity.class)
                .create().start().resume().visible();
        return controller.get();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void fourDestinationsKeepShortVisibleLabelsAndEqualWidths() {
        MainActivity activity = launch();
        BottomNavigationView bar = activity.findViewById(R.id.nav_view);
        assertNotNull(bar);

        assertEquals(NavigationBarView.LABEL_VISIBILITY_LABELED, bar.getLabelVisibilityMode());
        assertFalse(bar.isItemHorizontalTranslationEnabled());
        assertEquals(4, bar.getMenu().size());
        int[] ids = {R.id.navigation_home, R.id.navigation_busqueda,
                R.id.navigation_favoritos, R.id.navigation_configuracion};
        String[] labels = {"Inicio", "Buscar", "Favoritos", "Ajustes"};
        for (int i = 0; i < ids.length; i++) {
            assertEquals(ids[i], bar.getMenu().getItem(i).getItemId());
            assertEquals(labels[i], bar.getMenu().getItem(i).getTitle().toString());
        }

        assertEquals("Buscar", activity.getString(R.string.title_busqueda));
        assertEquals("Ajustes", activity.getString(R.string.title_configuracion));
        assertTrue(bar.isItemActiveIndicatorEnabled());
        assertEquals(activity.getResources().getDimensionPixelSize(
                R.dimen.bottom_nav_indicator_width), bar.getItemActiveIndicatorWidth());
        assertEquals(activity.getResources().getDimensionPixelSize(
                R.dimen.bottom_nav_indicator_height), bar.getItemActiveIndicatorHeight());
    }

    @Test
    public void iconLabelAndPillUseBrandPaletteRatherThanMaterialDefault() {
        MainActivity activity = launch();
        BottomNavigationView bar = activity.findViewById(R.id.nav_view);
        ColorStateList icons = bar.getItemIconTintList();
        ColorStateList labels = bar.getItemTextColor();
        ColorStateList pill = bar.getItemActiveIndicatorColor();
        assertNotNull(icons);
        assertNotNull(labels);
        assertNotNull(pill);

        int checked = android.R.attr.state_checked;
        int guinda = ContextCompat.getColor(activity, R.color.bottom_nav_active_tint);
        int neutral = ContextCompat.getColor(activity, R.color.bottom_nav_inactive_tint);
        int soft = ContextCompat.getColor(activity, R.color.bottom_nav_indicator_fill);
        assertEquals(guinda, icons.getColorForState(new int[]{checked}, 0));
        assertEquals(neutral, icons.getDefaultColor());
        assertEquals(guinda, labels.getColorForState(new int[]{checked}, 0));
        assertEquals(neutral, labels.getDefaultColor());
        assertEquals(soft, pill.getColorForState(new int[]{checked}, 0));
        assertEquals(Color.WHITE, ContextCompat.getColor(activity, R.color.bottom_nav_surface));
        assertEquals(Color.rgb(107, 8, 29), guinda);
        assertEquals(Color.rgb(237, 225, 228), soft);
        assertEquals(Color.rgb(225, 206, 210),
                ContextCompat.getColor(activity, R.color.bottom_nav_top_divider));
    }

    @Test
    @Config(qualifiers = "night")
    public void darkPrototypePaletteKeepsReadableLabelsAndSoftPill() {
        MainActivity activity = launch();
        BottomNavigationView bar = activity.findViewById(R.id.nav_view);
        int checked = android.R.attr.state_checked;
        assertNotNull(bar.getItemIconTintList());
        assertNotNull(bar.getItemActiveIndicatorColor());
        assertEquals(ContextCompat.getColor(activity, R.color.bottom_nav_active_tint),
                bar.getItemIconTintList().getColorForState(new int[]{checked}, 0));
        assertEquals(ContextCompat.getColor(activity, R.color.bottom_nav_indicator_fill),
                bar.getItemActiveIndicatorColor().getColorForState(new int[]{checked}, 0));
        assertEquals(Color.rgb(24, 20, 22),
                ContextCompat.getColor(activity, R.color.bottom_nav_surface));
    }

    @Test
    public void navigationStillReachesTheFourOriginalFragments() {
        MainActivity activity = launch();
        BottomNavigationView bar = activity.findViewById(R.id.nav_view);
        NavHostFragment host = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assertNotNull(host);

        int[] destinations = {R.id.navigation_home, R.id.navigation_busqueda,
                R.id.navigation_favoritos, R.id.navigation_configuracion};
        for (int id : destinations) {
            bar.setSelectedItemId(id);
            assertNotNull(host.getNavController().getCurrentDestination());
            assertEquals(id, host.getNavController().getCurrentDestination().getId());
        }
    }
}
