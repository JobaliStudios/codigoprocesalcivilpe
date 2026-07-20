package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FavoritosViewModel extends ViewModel {

    public enum SortMode {
        RECIENTES,
        SECCION,
        TIPO
    }

    private static final String FILTER_ALL = "Todas";

    private final MutableLiveData<List<FavoriteItem>> favorites = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> sectionFilters = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedSection = new MutableLiveData<>(FILTER_ALL);
    private final MutableLiveData<SortMode> sortMode = new MutableLiveData<>(SortMode.RECIENTES);

    public LiveData<List<FavoriteItem>> getFavorites() {
        return favorites;
    }

    public LiveData<List<String>> getSectionFilters() {
        return sectionFilters;
    }

    public LiveData<String> getSelectedSection() {
        return selectedSection;
    }

    public LiveData<SortMode> getSortMode() {
        return sortMode;
    }

    public void setFavorites(List<FavoriteItem> allFavorites) {
        Set<String> sections = new LinkedHashSet<>();
        sections.add(FILTER_ALL);
        for (FavoriteItem item : allFavorites) {
            sections.add(resolveSection(item));
        }

        String activeFilter = selectedSection.getValue() == null ? FILTER_ALL : selectedSection.getValue();
        if (!sections.contains(activeFilter)) {
            activeFilter = FILTER_ALL;
            selectedSection.setValue(FILTER_ALL);
        }

        sectionFilters.setValue(new ArrayList<>(sections));

        List<FavoriteItem> filtered = new ArrayList<>();
        for (FavoriteItem item : allFavorites) {
            if (FILTER_ALL.equals(activeFilter) || resolveSection(item).equals(activeFilter)) {
                filtered.add(item);
            }
        }
        favorites.setValue(filtered);
    }

    public void setSectionFilter(String section) {
        selectedSection.setValue(section == null || section.isEmpty() ? FILTER_ALL : section);
    }

    public void setSortMode(SortMode mode) {
        sortMode.setValue(mode == null ? SortMode.RECIENTES : mode);
    }

    public static String resolveSection(FavoriteItem item) {
        if (item == null || item.getId() == null) {
            return "Sin sección";
        }
        String id = item.getId().toLowerCase();
        if (id.contains("primera")) return "Sección Primera";
        if (id.contains("segunda")) return "Sección Segunda";
        if (id.contains("tercera")) return "Sección Tercera";
        if (id.contains("cuarta")) return "Sección Cuarta";
        if (id.contains("quinta")) return "Sección Quinta";
        if (id.contains("sexta")) return "Sección Sexta";
        // Formato nuevo de favoritos: "node:sec_N_..."
        int marker = id.indexOf("sec_");
        if (marker >= 0 && marker + 4 < id.length()) {
            switch (id.charAt(marker + 4)) {
                case '1': return "Sección Primera";
                case '2': return "Sección Segunda";
                case '3': return "Sección Tercera";
                case '4': return "Sección Cuarta";
                case '5': return "Sección Quinta";
                case '6': return "Sección Sexta";
            }
        }
        return "Sin sección";
    }
}
