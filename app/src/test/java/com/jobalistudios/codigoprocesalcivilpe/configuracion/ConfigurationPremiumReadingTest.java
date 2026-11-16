package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class ConfigurationPremiumReadingTest {
    private ActivityController<FragmentActivity> controller;
    private FragmentActivity activity;
    private FrameLayout host;
    private Context app;

    @Before public void before() {
        app = ApplicationProvider.getApplicationContext();
        app.getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().clear().commit();
        controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.get().setTheme(R.style.Theme_codigoprocesalcivilpe);
        controller.create().start().resume().visible();
        activity = controller.get();
        host = new FrameLayout(activity);
        host.setId(View.generateViewId());
        activity.setContentView(host);
    }

    @After public void after() {
        if(controller != null) controller.pause().stop().destroy();
        app.getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().clear().commit();
    }

    private ConfiguracionFragment open() {
        ConfiguracionFragment fragment = new ConfiguracionFragment();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(host.getId(), fragment).commitNow();
        return fragment;
    }

    @Test public void cardsAndReadingSwitchesExposePersistedValues() {
        ConfiguracionFragment fragment = open();
        View root = fragment.requireView();
        assertNotNull(root.findViewById(R.id.cardAppAppearance));
        assertNotNull(root.findViewById(R.id.card_preferencias));
        assertNotNull(root.findViewById(R.id.card_soporte));
        assertNotNull(root.findViewById(R.id.card_acerca_de));
        assertNotNull(root.findViewById(R.id.card_privacidad));
        assertTrue(((SwitchMaterial)root.findViewById(R.id.switchGlossary)).isChecked());
        assertFalse(((SwitchMaterial)root.findViewById(R.id.switchFocus)).isChecked());
        assertEquals("100 %", ((TextView)root.findViewById(R.id.readerSizeValue))
                .getText().toString());
    }

    @Test public void switchesSurviveFragmentRecreationWithoutSavedViewState() {
        View root = open().requireView();
        ((SwitchMaterial)root.findViewById(R.id.switchJustify)).setChecked(true);
        ((SwitchMaterial)root.findViewById(R.id.switchGlossary)).setChecked(false);
        ((SwitchMaterial)root.findViewById(R.id.switchFocus)).setChecked(true);
        assertTrue(ReadingPreferenceManager.isJustifyEnabled(app));
        assertFalse(ReadingPreferenceManager.isGlossaryEnabled(app));
        assertTrue(ReadingPreferenceManager.isFocusDefaultEnabled(app));

        root = open().requireView();
        assertTrue(((SwitchMaterial)root.findViewById(R.id.switchJustify)).isChecked());
        assertFalse(((SwitchMaterial)root.findViewById(R.id.switchGlossary)).isChecked());
        assertTrue(((SwitchMaterial)root.findViewById(R.id.switchFocus)).isChecked());
        assertFalse(root.findViewById(R.id.switchJustify).isSaveEnabled());
        assertFalse(root.findViewById(R.id.switchGlossary).isSaveEnabled());
    }

    @Test public void resetPreservesAppThemeAndOtherData() {
        ThemePreferenceManager.setAppThemeMode(app,AppThemeMode.DARK);
        ReadingPreferenceManager.setReaderFont(app,ReadingPreferenceManager.ReaderFont.SERIF);
        ReadingPreferenceManager.setContentFontScale(app,1.35f);
        ReadingPreferenceManager.setJustifyEnabled(app,true);
        ReadingPreferenceManager.setGlossaryEnabled(app,false);
        ReadingPreferenceManager.setSwipeNavigationEnabled(app,true);
        View root = open().requireView();
        root.findViewById(R.id.buttonReadingReset).performClick();
        assertEquals(AppThemeMode.DARK,ThemePreferenceManager.getAppThemeMode(app));
        assertEquals(ReadingPreferenceManager.ReaderFont.SANS,
                ReadingPreferenceManager.getReaderFont(app));
        assertEquals(1f,ReadingPreferenceManager.getContentFontScale(app),0.001f);
        assertFalse(ReadingPreferenceManager.isJustifyEnabled(app));
        assertTrue(ReadingPreferenceManager.isGlossaryEnabled(app));
        assertFalse(ReadingPreferenceManager.isSwipeNavigationEnabled(app));
        assertEquals("100 %",((TextView)root.findViewById(R.id.readerSizeValue))
                .getText().toString());
    }
}
