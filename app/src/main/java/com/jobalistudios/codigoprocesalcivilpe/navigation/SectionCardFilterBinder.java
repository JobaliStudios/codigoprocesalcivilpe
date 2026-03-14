package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.view.View;
import android.widget.SearchView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public final class SectionCardFilterBinder {

    public static class Entry {
        final View view;
        final String title;
        final String subtitle;
        final String range;
        final SectionFilterType type;

        public Entry(View view, String title, String subtitle, String range, SectionFilterType type) {
            this.view = view;
            this.title = title;
            this.subtitle = subtitle;
            this.range = range;
            this.type = type;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void addEntry(View view, String title, String subtitle, String range, SectionFilterType type) {
        entries.add(new Entry(view, title, subtitle, range, type));
    }

    public void bind(String key, SearchView searchView, ChipGroup chipGroup, View emptyState) {
        new SectionFilterController(key, searchView, chipGroup, emptyState, (query, type) -> {
            int visible = 0;
            for (Entry entry : entries) {
                boolean matchesType = type == SectionFilterType.ALL || entry.type == type;
                boolean matchesText = SectionFilterController.matchesText(query, entry.title, entry.subtitle, entry.range);
                boolean show = matchesType && matchesText;
                entry.view.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    visible++;
                }
            }
            return visible;
        });
    }
}
