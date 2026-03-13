package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentFavoritosBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    private FavoritesManager favoritesManager;
    private FavoritosAdapter adapter;
    private FavoritosViewModel viewModel;
    private ArrayAdapter<String> sectionFilterAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        favoritesManager = new FavoritesManager(requireContext());
        viewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        setupRecycler();
        setupSortAndFilters();
        setupObservers();
        setupCta();
        loadFavorites();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupRecycler() {
        adapter = new FavoritosAdapter(new FavoritosAdapter.OnFavoriteClickListener() {
            @Override
            public void onOpen(FavoriteItem item) {
                openFavorite(item);
            }

            @Override
            public void onRemove(FavoriteItem item) {
                removeWithUndo(item);
            }
        });
        binding.recyclerFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFavoritos.setAdapter(adapter);
    }

    private void setupSortAndFilters() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.favorite_sort_options,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(sortAdapter);
        binding.spinnerSort.setSelection(0, false);
        binding.spinnerSort.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (position == 1) {
                    viewModel.setSortMode(FavoritosViewModel.SortMode.SECCION);
                } else if (position == 2) {
                    viewModel.setSortMode(FavoritosViewModel.SortMode.TIPO);
                } else {
                    viewModel.setSortMode(FavoritosViewModel.SortMode.RECIENTES);
                }
            }
        });

        sectionFilterAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );
        sectionFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSectionFilter.setAdapter(sectionFilterAdapter);
        binding.spinnerSectionFilter.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (position >= 0 && position < sectionFilterAdapter.getCount()) {
                    viewModel.setSectionFilter(sectionFilterAdapter.getItem(position));
                    loadFavorites();
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            updateEmptyState(items.isEmpty());
        });

        viewModel.getSortMode().observe(getViewLifecycleOwner(), adapter::setSortMode);

        viewModel.getSectionFilters().observe(getViewLifecycleOwner(), sections -> {
            sectionFilterAdapter.clear();
            sectionFilterAdapter.addAll(sections);
            sectionFilterAdapter.notifyDataSetChanged();
        });

        viewModel.getSelectedSection().observe(getViewLifecycleOwner(), selected -> {
            int index = sectionFilterAdapter.getPosition(selected);
            if (index >= 0 && binding.spinnerSectionFilter.getSelectedItemPosition() != index) {
                binding.spinnerSectionFilter.setSelection(index, false);
            }
        });
    }

    private void setupCta() {
        binding.buttonExplorarCodigos.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.navigation_home)
        );
    }

    private void loadFavorites() {
        boolean removedCorrupted = favoritesManager.removeInvalidFavorites();
        if (removedCorrupted) {
            Toast.makeText(requireContext(), "Se eliminaron favoritos inválidos", Toast.LENGTH_SHORT).show();
        }

        List<FavoriteItem> items = favoritesManager.getAll();
        viewModel.setFavorites(items);
    }

    private void removeWithUndo(FavoriteItem item) {
        favoritesManager.remove(item.getId());
        loadFavorites();

        Snackbar.make(binding.getRoot(), R.string.favorite_removed_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_action, view -> {
                    favoritesManager.add(item);
                    loadFavorites();
                })
                .show();
    }

    private void updateEmptyState(boolean isEmpty) {
        int visibility = isEmpty ? View.VISIBLE : View.GONE;
        binding.emptyStateFavoritos.setVisibility(visibility);
        binding.textFavoritos.setVisibility(visibility);
        binding.buttonExplorarCodigos.setVisibility(visibility);
        binding.recyclerFavoritos.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openFavorite(FavoriteItem item) {
        android.content.Intent intent = FavoriteDestinationMapper.toIntent(requireContext(), item.getDestinationId());
        if (intent == null) {
            favoritesManager.remove(item.getId());
            loadFavorites();
            Toast.makeText(requireContext(), "Este favorito ya no es válido y fue eliminado", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private abstract static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
            // no-op
        }

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            onItemSelected(position);
        }

        public abstract void onItemSelected(int position);
    }
}
