package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryBottomSheet;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class NormativeHistoryBottomSheetTest {

    private ActivityController<SectionContentActivity> controller;

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void article835_rendersStructuredDetailAndOfficialSource() {
        Intent intent = SectionContentActivity.createIntent(
                androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                R.layout.activity_section_content,
                R.string.seccionsextatit2subcap10,
                R.string.app_name,
                R.string.app_name
        );
        controller = Robolectric.buildActivity(SectionContentActivity.class, intent).setup();
        SectionContentActivity activity = controller.get();

        activity.openNormativeHistory("835");
        activity.getSupportFragmentManager().executePendingTransactions();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(
                NormativeHistoryBottomSheet.TAG
        );

        assertNotNull(fragment);
        View root = fragment.requireView();
        assertEquals("Artículo 835", text(root, R.id.normativeHistoryArticle));
        assertEquals(View.VISIBLE,
                root.findViewById(R.id.normativeHistoryRepealedStatus).getVisibility());
        assertEquals("Ley N.º 32377", text(root, R.id.normativeInstrumentValue));
        assertEquals("7 de junio de 2025", text(root, R.id.normativeDateValue));
        assertEquals("Derogación", text(root, R.id.normativeTypeValue));
        MaterialButton source = root.findViewById(R.id.normativeOfficialSource);
        assertEquals(View.VISIBLE, source.getVisibility());
        assertTrue(source.getContentDescription().toString().contains("Ley N.º 32377"));
    }

    private String text(View root, int viewId) {
        return ((TextView) root.findViewById(viewId)).getText().toString();
    }
}
