package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.IdRes;

import com.google.android.material.chip.ChipGroup;
import com.jobalistudios.codigoprocesalcivilpe.R;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SectionFilterController {

    public interface FilterApplier {
        int apply(String query, SectionFilterType type);
    }

    private static class State {
        final String query;
        final SectionFilterType type;

        State(String query, SectionFilterType type) {
            this.query = query;
            this.type = type;
        }
    }

    private static final Map<String, State> LAST_STATE = new HashMap<>();

    private final String screenKey;
    private final SearchView searchView;
    private final ChipGroup chipGroup;
    private final View emptyStateView;
    private final FilterApplier filterApplier;

    public SectionFilterController(
            String screenKey,
            SearchView searchView,
            ChipGroup chipGroup,
            View emptyStateView,
            FilterApplier filterApplier
    ) {
        this.screenKey = screenKey;
        this.searchView = searchView;
        this.chipGroup = chipGroup;
        this.emptyStateView = emptyStateView;
        this.filterApplier = filterApplier;
        bind();
    }

    private void bind() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyCurrentFilter();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyCurrentFilter();
                return true;
            }
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> applyCurrentFilter());

        State previous = LAST_STATE.get(screenKey);
        if (previous != null) {
            selectChip(previous.type);
            searchView.setQuery(previous.query, false);
        } else {
            selectChip(SectionFilterType.ALL);
        }
        applyCurrentFilter();
    }

    private void applyCurrentFilter() {
        String query = normalize(searchView.getQuery() == null ? "" : searchView.getQuery().toString());
        SectionFilterType type = getCurrentType();
        int visibleCount = filterApplier.apply(query, type);
        LAST_STATE.put(screenKey, new State(query, type));

        if (emptyStateView != null) {
            emptyStateView.setVisibility(visibleCount == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private SectionFilterType getCurrentType() {
        @IdRes int checked = chipGroup.getCheckedChipId();
        if (checked == R.id.chipTitulo) {
            return SectionFilterType.TITULO;
        }
        if (checked == R.id.chipCapitulo) {
            return SectionFilterType.CAPITULO;
        }
        if (checked == R.id.chipSubcapitulo) {
            return SectionFilterType.SUBCAPITULO;
        }
        return SectionFilterType.ALL;
    }

    private void selectChip(SectionFilterType type) {
        if (type == SectionFilterType.TITULO) {
            chipGroup.check(R.id.chipTitulo);
            return;
        }
        if (type == SectionFilterType.CAPITULO) {
            chipGroup.check(R.id.chipCapitulo);
            return;
        }
        if (type == SectionFilterType.SUBCAPITULO) {
            chipGroup.check(R.id.chipSubcapitulo);
            return;
        }
        chipGroup.check(R.id.chipAll);
    }

    public static boolean matchesText(String rawQuery, String... sources) {
        if (TextUtils.isEmpty(rawQuery)) {
            return true;
        }
        String query = normalize(rawQuery);
        for (String source : sources) {
            if (normalize(source).contains(query)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim();
    }
}
