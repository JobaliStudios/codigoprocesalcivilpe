package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowLooper;

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
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void fontDialogChangesAndResetsPersistedScaleWithoutChangingReadingBehavior() {
        SectionContentActivity activity = launchArticle();
        activity.findViewById(R.id.btnTamanoLetra).performClick();

        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertNotNull(dialog);
        assertTrue(dialog.isShowing());

        SeekBar seekBar = dialog.findViewById(R.id.fontSizeSeekBar);
        TextView value = dialog.findViewById(R.id.fontSizeValue);
        assertNotNull(seekBar);
        assertNotNull(value);
        assertEquals(3, seekBar.getProgress()); // escala 100% sobre mínimo 85%

        seekBar.setProgress(8); // 85% + 8 * 5% = 125%
        assertEquals(1.25f,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
        assertEquals(activity.getString(R.string.font_size_value, 125),
                value.getText().toString());

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).performClick();
        ShadowLooper.idleMainLooper(); // El callback de los botones se despacha por Handler.
        assertEquals(ReadingPreferenceManager.DEFAULT_FONT_SCALE,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
    }

    @Test
    public void fontDialogStartsFromSavedPreferenceAndOkKeepsSelection() {
        ReadingPreferenceManager.setContentFontScale(application, 1.5f);
        SectionContentActivity activity = launchArticle();
        activity.findViewById(R.id.btnTamanoLetra).performClick();

        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertNotNull(dialog);
        SeekBar seekBar = dialog.findViewById(R.id.fontSizeSeekBar);
        assertNotNull(seekBar);
        assertEquals(13, seekBar.getProgress());

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        assertEquals(1.5f,
                ReadingPreferenceManager.getContentFontScale(activity), 0.001f);
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
