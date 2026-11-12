package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
public class AdaptiveLayoutsTest {

    private View inflate(int layout) {
        Context themed = new ContextThemeWrapper(
                ApplicationProvider.getApplicationContext(),
                R.style.Theme_codigoprocesalcivilpe);
        return LayoutInflater.from(themed).inflate(layout, new FrameLayout(themed), false);
    }

    @Test
    @Config(qualifiers = "w393dp-h852dp")
    public void compactWindowUsesSingleColumnLayouts() {
        View home = inflate(R.layout.fragment_home);
        View search = inflate(R.layout.fragment_busqueda);

        assertNull(home.findViewById(R.id.home_left_pane));
        assertNull(search.findViewById(R.id.search_filters_pane));
        assertNotNull(home.findViewById(R.id.button_consultar_codigo));
        assertNotNull(search.findViewById(R.id.recyclerResultados));
    }

    @Test
    @Config(qualifiers = "w960dp-h720dp")
    public void expandedWindowSeparatesHomeAndSearchIntoTwoPanes() {
        View home = inflate(R.layout.fragment_home);
        View left = home.findViewById(R.id.home_left_pane);
        View right = home.findViewById(R.id.home_right_pane);

        assertNotNull(left);
        assertNotNull(right);
        assertSame(left.getParent(), right.getParent());
        assertNotNull(home.findViewById(R.id.button_consultar_codigo));
        assertNotNull(home.findViewById(R.id.favorite_articles_container));
        assertNotNull(home.findViewById(R.id.button_practice_quiz));

        View search = inflate(R.layout.fragment_busqueda);
        View filters = search.findViewById(R.id.search_filters_pane);
        View results = search.findViewById(R.id.search_results_pane);
        assertNotNull(filters);
        assertNotNull(results);
        assertSame(filters.getParent(), results.getParent());
        assertNotNull(search.findViewById(R.id.searchView));
        assertNotNull(search.findViewById(R.id.recyclerResultados));
    }

    @Test
    @Config(qualifiers = "w960dp-h720dp")
    public void legalReaderUsesReadableMaximumWidth() {
        View reader = inflate(R.layout.activity_section_content);
        TextView legalBody = reader.findViewById(R.id.textView2);
        int maximum = reader.getResources().getDimensionPixelSize(
                R.dimen.max_reading_line_width);

        assertEquals(maximum, legalBody.getMaxWidth());
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT,
                legalBody.getLayoutParams().width);
    }

    @Test
    public void searchResultCountIsAnnouncedToScreenReaders() {
        View search = inflate(R.layout.fragment_busqueda);
        View count = search.findViewById(R.id.textResultCount);

        assertEquals(View.ACCESSIBILITY_LIVE_REGION_POLITE,
                count.getAccessibilityLiveRegion());
    }
}
