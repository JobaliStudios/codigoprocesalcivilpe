package com.jobalistudios.codigoprocesalcivilpe.busqueda;

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
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentBusquedaBinding;

public class BusquedaFragment extends Fragment {

    private static final String[] SECCIONES = {
            "Sección Primera", "Sección Segunda", "Sección Tercera",
            "Sección Cuarta", "Sección Quinta", "Sección Sexta"
    };

    private FragmentBusquedaBinding binding;
    private BusquedaViewModel viewModel;
    private BusquedaResultsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBusquedaBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BusquedaViewModel.class);

        adapter = new BusquedaResultsAdapter(this::openResult);
        binding.recyclerResultados.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerResultados.setAdapter(adapter);

        configureSearchView();
        configureSectionChips();
        setupEmptyStateCta();

        viewModel.getUiState().observe(getViewLifecycleOwner(), this::render);
        return binding.getRoot();
    }

    private void configureSearchView() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.updateQuery(query);
                viewModel.submitCurrentQuery();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.updateQuery(newText);
                return true;
            }
        });
    }


    private void setupEmptyStateCta() {
        binding.buttonEmptyBusquedaCta.setOnClickListener(v -> {
            binding.searchView.setIconified(false);
            if (binding.searchView.requestFocus()) {
                binding.searchView.clearFocus();
                binding.searchView.requestFocus();
            }
        });
    }

    private void configureSectionChips() {
        for (String section : SECCIONES) {
            Chip chip = new Chip(requireContext());
            chip.setText(section);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> viewModel.toggleSection(section));
            binding.chipGroupSections.addView(chip);
        }
    }

    private void render(BusquedaViewModel.BusquedaUiState state) {
        adapter.submitList(state.results);

        binding.progressLoading.setVisibility(state.loading ? View.VISIBLE : View.GONE);
        boolean emptyQuery = state.query.isEmpty();
        boolean hasNoResults = !emptyQuery && state.results.isEmpty();
        boolean showEmptyState = emptyQuery || hasNoResults;
        binding.emptyStateBusqueda.setVisibility(showEmptyState ? View.VISIBLE : View.GONE);
        binding.recyclerResultados.setVisibility(showEmptyState ? View.GONE : View.VISIBLE);
        binding.textEmptyBusquedaBody.setText(emptyQuery
                ? R.string.busqueda_empty_state
                : R.string.busqueda_no_results);
        binding.buttonEmptyBusquedaCta.setText(R.string.busqueda_empty_cta);

        binding.chipGroupRecent.removeAllViews();
        binding.textRecentTitle.setVisibility(state.recentQueries.isEmpty() ? View.GONE : View.VISIBLE);
        for (String query : state.recentQueries) {
            Chip chip = new Chip(requireContext());
            chip.setText(query);
            chip.setOnClickListener(v -> {
                binding.searchView.setQuery(query, true);
                viewModel.useRecentQuery(query);
            });
            binding.chipGroupRecent.addView(chip);
        }

        if (!state.query.equals(binding.searchView.getQuery().toString())) {
            binding.searchView.setQuery(state.query, false);
        }
    }

    private void openResult(BusquedaViewModel.LegalSearchResult result) {
        startActivity(SectionContentActivity.createIntent(
                requireContext(),
                R.layout.activity_section_content,
                result.item.textResId,
                result.item.titleResId,
                result.item.subtitleResId
        ).putExtra(SectionContentActivity.EXTRA_INITIAL_QUERY, binding.searchView.getQuery().toString()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
