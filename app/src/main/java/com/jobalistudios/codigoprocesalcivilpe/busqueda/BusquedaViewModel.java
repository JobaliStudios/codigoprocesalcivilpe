package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BusquedaViewModel extends AndroidViewModel {

    private static final String PREFS_NAME = "busqueda_prefs";
    private static final String PREF_RECENT = "recent_queries";
    private static final int MAX_RECENT = 8;

    private final SharedPreferences sharedPreferences;
    private final List<LegalSearchItem> legalIndex;
    private final Set<String> selectedSections = new HashSet<>();
    private final MutableLiveData<BusquedaUiState> uiState = new MutableLiveData<>();

    private String currentQuery = "";

    public BusquedaViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, 0);
        legalIndex = LegalSearchItemFactory.create(application);
        refreshState(false);
    }

    public LiveData<BusquedaUiState> getUiState() {
        return uiState;
    }

    public void updateQuery(String query) {
        currentQuery = query == null ? "" : query.trim();
        refreshState(false);
    }

    public void toggleSection(String sectionName) {
        if (selectedSections.contains(sectionName)) {
            selectedSections.remove(sectionName);
        } else {
            selectedSections.add(sectionName);
        }
        refreshState(false);
    }

    public void submitCurrentQuery() {
        if (!currentQuery.isEmpty()) {
            persistRecentQuery(currentQuery);
            refreshState(false);
        }
    }

    public void useRecentQuery(String query) {
        currentQuery = query;
        refreshState(false);
    }

    private void refreshState(boolean loading) {
        List<String> recents = getRecentQueries();
        List<LegalSearchResult> results = search(currentQuery);
        uiState.setValue(new BusquedaUiState(currentQuery, loading, results, recents, selectedSections));
    }

    private List<LegalSearchResult> search(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedQuery = normalize(query);
        List<LegalSearchResult> results = new ArrayList<>();

        for (LegalSearchItem item : legalIndex) {
            if (!selectedSections.isEmpty() && !selectedSections.contains(item.sectionName)) {
                continue;
            }
            int score = calculateScore(item, normalizedQuery);
            if (score > 0) {
                results.add(new LegalSearchResult(item, score));
            }
        }

        results.sort(Comparator.comparingInt((LegalSearchResult r) -> r.relevance).reversed());
        return results;
    }

    private int calculateScore(LegalSearchItem item, String normalizedQuery) {
        String title = normalize(item.title);
        String snippet = normalize(item.snippet);
        int score = 0;
        if (title.contains(normalizedQuery)) score += 5;
        if (snippet.contains(normalizedQuery)) score += 3;
        int first = normalize(item.fullText).indexOf(normalizedQuery);
        if (first >= 0) score += 1;
        return score;
    }

    private List<String> getRecentQueries() {
        String value = sharedPreferences.getString(PREF_RECENT, "");
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(value.split("\\n")));
    }

    private void persistRecentQuery(String query) {
        List<String> queries = getRecentQueries();
        queries.remove(query);
        queries.add(0, query);
        if (queries.size() > MAX_RECENT) {
            queries = queries.subList(0, MAX_RECENT);
        }
        sharedPreferences.edit().putString(PREF_RECENT, TextUtils.join("\n", queries)).apply();
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    public static class BusquedaUiState {
        public final String query;
        public final boolean loading;
        public final List<LegalSearchResult> results;
        public final List<String> recentQueries;
        public final Set<String> selectedSections;

        BusquedaUiState(String query, boolean loading, List<LegalSearchResult> results,
                        List<String> recentQueries, Set<String> selectedSections) {
            this.query = query;
            this.loading = loading;
            this.results = results;
            this.recentQueries = recentQueries;
            this.selectedSections = new HashSet<>(selectedSections);
        }
    }

    static class LegalSearchItemFactory {
        static List<LegalSearchItem> create(Application app) {
            return Arrays.asList(
                    item(app, R.string.seccion_primeratit, R.string.rangartisec1tit1, R.string.seccionprimeratit1txt, R.string.titulo1),
                    item(app, R.string.seccion_primeratit, R.string.rangartisec1tit2cap1, R.string.seccionprimeratit2cap1txt, R.string.capitulo1),
                    item(app, R.string.seccion_primeratit, R.string.rangartisec1tit2cap2, R.string.seccionprimeratit2cap2txt, R.string.capitulo2),
                    item(app, R.string.seccion_segundatit, R.string.rangartisec2tit1cap1, R.string.seccionsegundatit1cap1, R.string.sec2tit1cap1sub),
                    item(app, R.string.seccion_terceratit, R.string.rangartisec3tit1cap1, R.string.seccionterceratit1cap1, R.string.sec3tit1cap1sub),
                    item(app, R.string.seccion_cuartatit, R.string.rangartisec4tit1, R.string.seccioncuartatit1, R.string.sec4titulo1sub),
                    item(app, R.string.seccion_quintatit, R.string.rangartisec5tit2cap1, R.string.seccionquintatit2cap1, R.string.sec5titulo2sub),
                    item(app, R.string.seccion_sextatit, R.string.rangartisec6tit1, R.string.seccionsextatit1, R.string.sec6titulo1sub)
            );
        }

        private static LegalSearchItem item(Application app, int sectionRes, int rangeRes, int textRes, int titleRes) {
            String fullText = app.getString(textRes);
            String snippet = fullText.length() > 180 ? fullText.substring(0, 180) + "…" : fullText;
            return new LegalSearchItem(
                    app.getString(titleRes),
                    app.getString(sectionRes),
                    app.getString(rangeRes),
                    snippet,
                    fullText,
                    textRes
            );
        }
    }

    public static class LegalSearchItem {
        public final String title;
        public final String sectionName;
        public final String articleRange;
        public final String snippet;
        public final String fullText;
        public final int textResId;

        LegalSearchItem(String title, String sectionName, String articleRange, String snippet, String fullText, int textResId) {
            this.title = title;
            this.sectionName = sectionName;
            this.articleRange = articleRange;
            this.snippet = snippet;
            this.fullText = fullText;
            this.textResId = textResId;
        }
    }

    public static class LegalSearchResult {
        public final LegalSearchItem item;
        public final int relevance;

        LegalSearchResult(LegalSearchItem item, int relevance) {
            this.item = item;
            this.relevance = relevance;
        }
    }
}
