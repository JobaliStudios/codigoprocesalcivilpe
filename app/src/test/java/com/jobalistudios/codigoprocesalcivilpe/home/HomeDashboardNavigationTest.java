package com.jobalistudios.codigoprocesalcivilpe.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.jobalistudios.codigoprocesalcivilpe.MainActivity;
import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class HomeDashboardNavigationTest {
    private ActivityController<MainActivity> controller;

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void viewAllFavorites_navigatesToExistingBottomNavigationDestination() {
        controller = Robolectric.buildActivity(MainActivity.class)
                .create().start().resume().visible();
        MainActivity activity = controller.get();
        NavHostFragment host = (NavHostFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assertNotNull(host);
        NavController navController = host.getNavController();
        assertNotNull(navController.getCurrentDestination());
        assertEquals(R.id.navigation_home, navController.getCurrentDestination().getId());

        activity.findViewById(R.id.button_view_all_favorites).performClick();

        assertNotNull(navController.getCurrentDestination());
        assertEquals(R.id.navigation_favoritos, navController.getCurrentDestination().getId());
    }
}
