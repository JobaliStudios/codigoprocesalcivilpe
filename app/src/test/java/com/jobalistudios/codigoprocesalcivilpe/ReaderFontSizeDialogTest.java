package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.slider.Slider;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

/** Contrato previo de Aa migrado al nuevo AppearanceBottomSheet; ninguna prueba se elimina. */
@RunWith(RobolectricTestRunner.class)
public class ReaderFontSizeDialogTest {
    private Application application;
    private ActivityController<SectionContentActivity> controller;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @After
    public void tearDown() {
        if (controller != null) controller.pause().stop().destroy();
    }

    @Test
    public void fontDialogChangesAndResetsPersistedScaleWithoutChangingReadingBehavior() {
        SectionContentActivity activity = launchArticle();
        AppearanceBottomSheet sheet = openAppearance(activity);
        Slider slider = sheet.requireView().findViewById(R.id.fontScaleSlider);
        TextView value = sheet.requireView().findViewById(R.id.fontScaleLabel);
        assertNotNull(slider);
        assertNotNull(value);
        assertEquals(100f, slider.getValue(), 0.001f);

        slider.setValue(125f);
        assertEquals(1.25f,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
        assertEquals(activity.getString(R.string.reader_appearance_size_value, 125),
                value.getText().toString());

        sheet.requireView().findViewById(R.id.appearanceReset).performClick();
        assertEquals(ReadingPreferenceManager.DEFAULT_FONT_SCALE,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
        assertEquals(100f, slider.getValue(), 0.001f);
    }

    @Test
    public void fontDialogStartsFromSavedPreferenceAndOkKeepsSelection() {
        ReadingPreferenceManager.setContentFontScale(application, 1.5f);
        SectionContentActivity activity = launchArticle();
        AppearanceBottomSheet sheet = openAppearance(activity);
        Slider slider = sheet.requireView().findViewById(R.id.fontScaleSlider);
        assertNotNull(slider);
        assertEquals(150f, slider.getValue(), 0.001f);

        sheet.requireView().findViewById(R.id.appearanceDone).performClick();
        assertEquals(1.5f,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
    }

    @Test
    public void recreationPreservesThemeFontAndScaleThroughPreferences() {
        SectionContentActivity activity = launchArticle();
        AppearanceBottomSheet sheet = openAppearance(activity);
        sheet.requireView().findViewById(R.id.readerSepia).performClick();
        sheet.requireView().findViewById(R.id.fontSerif).performClick();
        ((Slider) sheet.requireView().findViewById(R.id.fontScaleSlider)).setValue(110f);
        sheet.dismiss();
        activity.getSupportFragmentManager().executePendingTransactions();

        AppearanceBottomSheet reopened = openAppearance(activity);
        assertEquals(R.id.readerSepia, ((com.google.android.material.button.MaterialButtonToggleGroup)
                reopened.requireView().findViewById(R.id.readerThemeGroup)).getCheckedButtonId());
        assertEquals(R.id.fontSerif, ((com.google.android.material.button.MaterialButtonToggleGroup)
                reopened.requireView().findViewById(R.id.readerFontGroup)).getCheckedButtonId());
        assertEquals(110f, ((Slider) reopened.requireView()
                .findViewById(R.id.fontScaleSlider)).getValue(), 0.001f);
    }

    private AppearanceBottomSheet openAppearance(SectionContentActivity activity) {
        activity.findViewById(R.id.btnTamanoLetra).performClick();
        activity.getSupportFragmentManager().executePendingTransactions();
        AppearanceBottomSheet sheet = (AppearanceBottomSheet) activity
                .getSupportFragmentManager().findFragmentByTag("readerAppearance");
        assertNotNull(sheet);
        assertNotNull(sheet.getDialog());
        assertTrue(sheet.requireDialog().isShowing());
        return sheet;
    }

    private SectionContentActivity launchArticle() {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(application, "564");
        assertNotNull(target);
        controller = Robolectric.buildActivity(
                SectionContentActivity.class, target.createIntent(application))
                .create().start().resume().visible();
        return controller.get();
    }
}
