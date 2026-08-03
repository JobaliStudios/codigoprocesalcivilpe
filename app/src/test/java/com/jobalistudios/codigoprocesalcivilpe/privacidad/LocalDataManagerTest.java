package com.jobalistudios.codigoprocesalcivilpe.privacidad;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LocalDataManagerTest {

    private Context context;
    private LocalDataManager manager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        clear("busqueda_prefs");
        clear("codigoprocesalcivil_favorites");
        clear("codigoprocesalcivil_highlights");
        clear("app_settings");
        manager = new LocalDataManager(context);
    }

    @Test
    public void categoryActions_onlyDeleteSelectedData() {
        put("busqueda_prefs", "recent_queries", "demanda");
        put("codigoprocesalcivil_favorites", "items", "[]");

        manager.clearRecentSearches();

        assertFalse(has("busqueda_prefs", "recent_queries"));
        assertTrue(has("codigoprocesalcivil_favorites", "items"));
    }

    @Test
    public void clearAllLocalData_deletesEveryManagedCategory() {
        put("busqueda_prefs", "recent_queries", "demanda");
        put("codigoprocesalcivil_favorites", "items", "[]");
        put("codigoprocesalcivil_highlights", "block", "[]");
        put("app_settings", "dark_mode_enabled", "true");

        manager.clearAllLocalData();

        assertTrue(context.getSharedPreferences("busqueda_prefs", 0).getAll().isEmpty());
        assertTrue(context.getSharedPreferences("codigoprocesalcivil_favorites", 0).getAll().isEmpty());
        assertTrue(context.getSharedPreferences("codigoprocesalcivil_highlights", 0).getAll().isEmpty());
        assertTrue(context.getSharedPreferences("app_settings", 0).getAll().isEmpty());
    }

    private void put(String preferences, String key, String value) {
        context.getSharedPreferences(preferences, Context.MODE_PRIVATE)
                .edit().putString(key, value).commit();
    }

    private boolean has(String preferences, String key) {
        return context.getSharedPreferences(preferences, Context.MODE_PRIVATE).contains(key);
    }

    private void clear(String preferences) {
        context.getSharedPreferences(preferences, Context.MODE_PRIVATE)
                .edit().clear().commit();
    }
}
