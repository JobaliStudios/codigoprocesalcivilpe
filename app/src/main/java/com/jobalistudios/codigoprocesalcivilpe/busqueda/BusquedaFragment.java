package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentBusquedaBinding;

import java.util.LinkedHashMap;
import java.util.Map;

public class BusquedaFragment extends Fragment {

    private static final String[] SECCIONES = {
            "Sección Primera", "Sección Segunda", "Sección Tercera",
            "Sección Cuarta", "Sección Quinta", "Sección Sexta"
    };

    private final Map<String, Chip> sectionChips = new LinkedHashMap<>();
    private FragmentBusquedaBinding binding;
    private BusquedaViewModel viewModel;
    private BusquedaResultsAdapter adapter;
    private boolean renderingFilterState;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentBusquedaBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BusquedaViewModel.class);

        adapter = new BusquedaResultsAdapter(this::openResult);
        binding.recyclerResultados.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerResultados.setAdapter(adapter);

        configureSearchView();
        configureTypeFilters();
        configureSectionChips();
        configureActions();

        viewModel.getUiState().observe(getViewLifecycleOwner(), this::render);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshFavorites();
        }
    }

    private void configureSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.updateQuery(query);
                viewModel.submitCurrentQuery();
                binding.searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.updateQuery(newText);
                return true;
            }
        });
    }

    private void configureTypeFilters() {
        binding.chipGroupSearchType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (renderingFilterState || checkedIds.isEmpty()) {
                return;
            }
            viewModel.setFilter(filterForChip(checkedIds.get(0)));
        });
    }

    private SearchFilter filterForChip(int chipId) {
        if (chipId == R.id.chipSearchNumber) {
            return SearchFilter.NUMBER;
        }
        if (chipId == R.id.chipSearchText) {
            return SearchFilter.TEXT;
        }
        if (chipId == R.id.chipSearchFavorites) {
            return SearchFilter.FAVORITES;
        }
        return SearchFilter.ALL;
    }

    private int chipForFilter(SearchFilter filter) {
        switch (filter) {
            case NUMBER:
                return R.id.chipSearchNumber;
            case TEXT:
                return R.id.chipSearchText;
            case FAVORITES:
                return R.id.chipSearchFavorites;
            default:
                return R.id.chipSearchAll;
        }
    }

    private void configureSectionChips() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (String section : SECCIONES) {
            Chip chip = (Chip) inflater.inflate(
                    R.layout.item_search_filter_chip,
                    binding.chipGroupSections,
                    false
            );
            chip.setText(section);
            chip.setOnClickListener(view -> viewModel.toggleSection(section));
            binding.chipGroupSections.addView(chip);
            sectionChips.put(section, chip);
        }
    }

    private void configureActions() {
        binding.buttonGoToArticle.setOnClickListener(view -> openGoToArticle());
        binding.buttonEmptyBusquedaCta.setOnClickListener(view -> {
            binding.searchView.setIconified(false);
            binding.searchView.requestFocus();
        });
        binding.buttonClearSearchFilters.setOnClickListener(view -> viewModel.clearFilters());
        binding.buttonClearRecent.setOnClickListener(view -> viewModel.clearAllRecentQueries());
    }

    private void openGoToArticle() {
        if (getChildFragmentManager().isStateSaved()
                || getChildFragmentManager().findFragmentByTag(
                GoToArticleBottomSheet.TAG) != null) {
            return;
        }
        GoToArticleBottomSheet.newInstance().show(
                getChildFragmentManager(),
                GoToArticleBottomSheet.TAG
        );
    }

    private void render(BusquedaViewModel.BusquedaUiState state) {
        adapter.submitList(state.results);
        binding.progressLoading.setVisibility(View.GONE);

        renderingFilterState = true;
        binding.chipGroupSearchType.check(chipForFilter(state.filter));
        renderingFilterState = false;
        for (Map.Entry<String, Chip> entry : sectionChips.entrySet()) {
            entry.getValue().setChecked(state.selectedSections.contains(entry.getKey()));
        }

        renderRecentQueries(state);
        renderResultCount(state);
        renderEmptyState(state);

        if (!state.query.equals(binding.searchView.getQuery().toString())) {
            binding.searchView.setQuery(state.query, false);
        }
    }

    private void renderRecentQueries(BusquedaViewModel.BusquedaUiState state) {
        boolean visible = state.query.trim().isEmpty()
                && state.filter != SearchFilter.FAVORITES
                && !state.recentQueries.isEmpty();
        binding.recentHeader.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.recentScroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.chipGroupRecent.removeAllViews();
        if (!visible) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (String query : state.recentQueries) {
            Chip chip = (Chip) inflater.inflate(
                    R.layout.item_search_filter_chip,
                    binding.chipGroupRecent,
                    false
            );
            chip.setCheckable(false);
            chip.setText(query);
            chip.setCloseIconVisible(true);
            chip.setCloseIconContentDescription(getString(
                    R.string.busqueda_remove_recent_description, query));
            chip.setOnClickListener(view -> {
                binding.searchView.setQuery(query, false);
                viewModel.useRecentQuery(query);
            });
            chip.setOnCloseIconClickListener(view -> viewModel.removeRecentQuery(query));
            binding.chipGroupRecent.addView(chip);
        }
    }

    private void renderResultCount(BusquedaViewModel.BusquedaUiState state) {
        boolean visible = !state.query.trim().isEmpty()
                || state.filter == SearchFilter.FAVORITES;
        binding.textResultCount.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            int count = state.results.size();
            binding.textResultCount.setText(getResources().getQuantityString(
                    R.plurals.busqueda_result_count, count, count));
        }
    }

    private void renderEmptyState(BusquedaViewModel.BusquedaUiState state) {
        boolean empty = state.emptyState != BusquedaViewModel.EmptyState.NONE;
        binding.emptyStateBusqueda.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerResultados.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (!empty) {
            return;
        }

        int title = R.string.busqueda_no_results_title;
        String body;
        boolean prompt = false;
        switch (state.emptyState) {
            case PROMPT:
                title = R.string.busqueda_empty_title;
                body = getString(R.string.busqueda_empty_state);
                prompt = true;
                break;
            case INVALID_NUMBER:
                body = getString(R.string.busqueda_invalid_number);
                break;
            case NO_FAVORITES:
                title = R.string.busqueda_no_article_favorites_title;
                body = getString(R.string.busqueda_no_article_favorites_body);
                break;
            case FILTERED_NO_RESULTS:
                body = getString(R.string.busqueda_filtered_no_results);
                break;
            default:
                body = getString(R.string.busqueda_no_results, state.query);
                break;
        }
        binding.textEmptyBusquedaTitle.setText(title);
        binding.textEmptyBusquedaBody.setText(body);
        binding.buttonEmptyBusquedaCta.setVisibility(prompt ? View.VISIBLE : View.GONE);
        binding.buttonClearSearchFilters.setVisibility(
                !prompt && state.hasActiveFilters() ? View.VISIBLE : View.GONE);
    }

    private void openResult(ArticleSearchResult result) {
        viewModel.recordResultOpened();
        Intent intent = ArticleSearchNavigation.createIntent(
                requireContext(), result, binding.searchView.getQuery().toString());
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        sectionChips.clear();
    }
}
