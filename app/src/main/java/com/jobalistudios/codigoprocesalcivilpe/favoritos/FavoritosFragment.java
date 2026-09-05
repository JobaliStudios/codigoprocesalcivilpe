package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.apuntes.MyNotesActivity;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentFavoritosBinding;

import java.util.List;

public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    private FavoritesManager favoritesManager;
    private FavoritosAdapter adapter;
    private FavoritosViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        favoritesManager = new FavoritesManager(requireContext());
        viewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        setupRecycler();
        setupControls();
        setupObservers();
        setupCta();
        loadFavorites();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            loadFavorites();
        }
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

    private void setupControls() {
        FavoritosUiState currentState = viewModel.getUiState().getValue();
        if (currentState != null) {
            binding.favoriteSearchView.setQuery(currentState.query, false);
            binding.favoriteFilterGroup.check(filterId(currentState.filterMode));
        }
        binding.favoriteSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setQuery(newText);
                return true;
            }
        });

        binding.favoriteFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = checkedIds.isEmpty()
                    ? R.id.favoriteFilterAll
                    : checkedIds.get(0);
            if (checkedId == R.id.favoriteFilterNotes) {
                viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_NOTES);
            } else if (checkedId == R.id.favoriteFilterHighlights) {
                viewModel.setFilterMode(FavoritosViewModel.FilterMode.WITH_HIGHLIGHTS);
            } else {
                viewModel.setFilterMode(FavoritosViewModel.FilterMode.ALL);
            }
        });

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.favorite_sort_options,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(sortAdapter);
        if (currentState != null) {
            binding.spinnerSort.setSelection(
                    currentState.sortMode == FavoritosViewModel.SortMode.ARTICLE_NUMBER ? 1 : 0,
                    false
            );
        }
        binding.spinnerSort.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                viewModel.setSortMode(position == 1
                        ? FavoritosViewModel.SortMode.ARTICLE_NUMBER
                        : FavoritosViewModel.SortMode.RECENT);
            }
        });
    }

    private void setupObservers() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            adapter.submitList(state.visibleItems);
            renderControls(state);
            renderEmptyState(state);
        });
    }

    private void renderControls(FavoritosUiState state) {
        if (!binding.favoriteSearchView.getQuery().toString().equals(state.query)) {
            binding.favoriteSearchView.setQuery(state.query, false);
        }
        int filterId = filterId(state.filterMode);
        if (binding.favoriteFilterGroup.getCheckedChipId() != filterId) {
            binding.favoriteFilterGroup.check(filterId);
        }
        int sortPosition = state.sortMode == FavoritosViewModel.SortMode.ARTICLE_NUMBER ? 1 : 0;
        if (binding.spinnerSort.getSelectedItemPosition() != sortPosition) {
            binding.spinnerSort.setSelection(sortPosition, false);
        }
    }

    private int filterId(FavoritosViewModel.FilterMode mode) {
        if (mode == FavoritosViewModel.FilterMode.WITH_NOTES) {
            return R.id.favoriteFilterNotes;
        }
        if (mode == FavoritosViewModel.FilterMode.WITH_HIGHLIGHTS) {
            return R.id.favoriteFilterHighlights;
        }
        return R.id.favoriteFilterAll;
    }

    private void renderEmptyState(FavoritosUiState state) {
        boolean empty = state.emptyState != FavoritosUiState.EmptyState.NONE;
        binding.emptyStateFavoritos.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerFavoritos.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (!empty) {
            return;
        }

        int titleRes;
        int bodyRes;
        if (state.emptyState == FavoritosUiState.EmptyState.NO_NOTES) {
            titleRes = R.string.favorites_empty_notes_title;
            bodyRes = R.string.favorites_empty_notes_body;
        } else if (state.emptyState == FavoritosUiState.EmptyState.NO_HIGHLIGHTS) {
            titleRes = R.string.favorites_empty_highlights_title;
            bodyRes = R.string.favorites_empty_highlights_body;
        } else if (state.emptyState == FavoritosUiState.EmptyState.NO_QUERY_RESULTS) {
            binding.textEmptyFavoritosTitle.setText(getString(
                    R.string.favorites_empty_query_title,
                    state.query.trim()
            ));
            binding.textFavoritos.setText(R.string.favorites_empty_query_body);
            binding.buttonExplorarCodigos.setVisibility(View.GONE);
            return;
        } else {
            titleRes = R.string.favorites_empty_title;
            bodyRes = R.string.no_favorites;
        }
        binding.textEmptyFavoritosTitle.setText(titleRes);
        binding.textFavoritos.setText(bodyRes);
        binding.buttonExplorarCodigos.setVisibility(
                state.emptyState == FavoritosUiState.EmptyState.NO_FAVORITES
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void setupCta() {
        binding.buttonMyNotes.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), MyNotesActivity.class))
        );
        binding.buttonExplorarCodigos.setOnClickListener(view ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.navigation_home)
        );
    }

    private void loadFavorites() {
        boolean removedCorrupted = favoritesManager.removeInvalidFavorites();
        if (removedCorrupted) {
            Toast.makeText(
                    requireContext(),
                    R.string.favorites_invalid_removed,
                    Toast.LENGTH_SHORT
            ).show();
        }
        List<FavoriteItem> items = favoritesManager.getAll();
        viewModel.setFavorites(items);
    }

    private void removeWithUndo(FavoriteItem item) {
        favoritesManager.remove(item.getId());
        loadFavorites();

        Snackbar.make(binding.getRoot(), R.string.favorite_removed_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_action, view -> {
                    favoritesManager.restore(item);
                    loadFavorites();
                })
                .show();
    }

    private void openFavorite(FavoriteItem item) {
        android.content.Intent intent = FavoriteDestinationMapper.toIntent(
                requireContext(),
                item.getDestinationId()
        );
        if (intent == null) {
            favoritesManager.remove(item.getId());
            loadFavorites();
            Toast.makeText(
                    requireContext(),
                    R.string.favorite_invalid_removed,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private abstract static class SimpleItemSelectedListener implements
            android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
            // no-op
        }

        @Override
        public void onItemSelected(
                android.widget.AdapterView<?> parent,
                View view,
                int position,
                long id
        ) {
            onItemSelected(position);
        }

        public abstract void onItemSelected(int position);
    }
}
