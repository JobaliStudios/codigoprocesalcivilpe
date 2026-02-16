package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentFavoritosBinding;

import java.util.List;

public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    private FavoritesStorage favoritesStorage;
    private FavoritesAdapter favoritesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        favoritesStorage = new FavoritesStorage(requireContext());

        favoritesAdapter = new FavoritesAdapter(this::openFavorite);
        binding.favoritesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesRecycler.setAdapter(favoritesAdapter);

        renderFavorites();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderFavorites();
    }

    private void renderFavorites() {
        List<FavoriteItem> favorites = favoritesStorage.getFavorites();
        favoritesAdapter.submitList(favorites);
        binding.textFavoritos.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openFavorite(FavoriteItem item) {
        try {
            Class<?> targetClass = Class.forName(item.getActivityClassName());
            startActivity(new Intent(requireContext(), targetClass));
        } catch (ClassNotFoundException exception) {
            Toast.makeText(requireContext(), getString(R.string.favorites_open_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
