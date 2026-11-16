package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Escala del sistema 200 %, sin eliminar controles ni reemplazarlos por solo color. */
@RunWith(RobolectricTestRunner.class)
@Config(qualifiers = "w393dp-h852dp")
public class ReaderAccessibilitySizingTest {
    private Context scaledContext(float fontScale) {
        Context context = ApplicationProvider.getApplicationContext();
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.fontScale = fontScale;
        return new ContextThemeWrapper(context.createConfigurationContext(configuration),
                R.style.Theme_codigoprocesalcivilpe);
    }

    private View inflate(Context c, int id) {
        return LayoutInflater.from(c).inflate(id, new FrameLayout(c), false);
    }

    @Test public void dashboardStacksActionsForTwoHundredPercentFontScale() {
        Context c = scaledContext(2f);
        View root = inflate(c, R.layout.fragment_home);
        assertTrue(ReaderAccessibilitySizing.isLarge(root.getResources()));
        ReaderAccessibilitySizing.adaptHomeQuickAccess(root);
        LinearLayout row = root.findViewById(R.id.homeGridRowOne);
        assertEquals(LinearLayout.VERTICAL, row.getOrientation());
        MaterialButton code = root.findViewById(R.id.buttonHomeCode);
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, code.getLayoutParams().width);
        assertEquals(0f, ((LinearLayout.LayoutParams) code.getLayoutParams()).weight, 0f);
        assertNotNull(root.findViewById(R.id.buttonHomeNotes));
    }

    @Test public void normalFontScalePreservesTwoByTwoGrid() {
        Context c = scaledContext(1f);
        View root = inflate(c,R.layout.fragment_home);
        ReaderAccessibilitySizing.adaptHomeQuickAccess(root);
        LinearLayout row = root.findViewById(R.id.homeGridRowOne);
        assertEquals(LinearLayout.HORIZONTAL,row.getOrientation());
        assertEquals(2,row.getChildCount());
    }

    @Test public void readerActionsStayAccessibleInHorizontalScrollAtTwoHundredPercent() {
        Context c = scaledContext(2f);
        View root = inflate(c,R.layout.activity_section_content);
        ReaderAccessibilitySizing.adaptReaderBottomActions(root);
        HorizontalScrollView scroll = root.findViewById(R.id.readerBottomActionsScroll);
        assertNotNull(scroll);
        MaterialButton next = root.findViewById(R.id.btnNextArticle);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT,next.getLayoutParams().width);
        assertTrue(next.getMinWidth() >= c.getResources().getDimensionPixelSize(
                R.dimen.reader_large_text_next_min_width));
        assertNotNull(next.getContentDescription());
        assertNotNull(root.findViewById(R.id.btnReaderMoreArticle));
    }

    @Test public void appearanceAndConfigurationSegmentsCanBeScrolledWithoutTextCutoff() {
        Context c = scaledContext(2f);
        View appearance = inflate(c,R.layout.bottom_sheet_reader_appearance);
        for(int groupId : new int[]{R.id.readerThemeGroup,R.id.readerFontGroup,
                R.id.readerLineGroup,R.id.readerMarginGroup}) {
            ReaderAccessibilitySizing.adaptSegmentedGroup(appearance,groupId);
            MaterialButtonToggleGroup group = appearance.findViewById(groupId);
            assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT,group.getLayoutParams().width);
            for(int i=0;i<group.getChildCount();i++) {
                MaterialButton button = (MaterialButton)group.getChildAt(i);
                assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT,
                        button.getLayoutParams().width);
                assertEquals(0f,((LinearLayout.LayoutParams)button.getLayoutParams()).weight,0f);
            }
        }
        View config = inflate(c,R.layout.fragment_configuracion);
        ReaderAccessibilitySizing.adaptSegmentedGroup(config,R.id.appThemeToggle);
        MaterialButtonToggleGroup appThemes = config.findViewById(R.id.appThemeToggle);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT,appThemes.getLayoutParams().width);
        assertNotNull(config.findViewById(R.id.appThemeToggleScroll));
    }
}
