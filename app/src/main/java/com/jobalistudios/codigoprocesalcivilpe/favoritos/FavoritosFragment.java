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

import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentFavoritosBinding;

import java.util.List;

public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    private FavoritesManager favoritesManager;
    private FavoritosAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false);
        favoritesManager = new FavoritesManager(requireContext());

        setupRecycler();
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
                favoritesManager.remove(item.getId());
                loadFavorites();
            }
        });
        binding.recyclerFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFavoritos.setAdapter(adapter);
    }

    private void loadFavorites() {
        List<FavoriteItem> items = favoritesManager.getAll();
        adapter.submitList(items);
        binding.textFavoritos.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openFavorite(FavoriteItem item) {
        try {
            Class<?> activityClass = Class.forName(item.getActivityClassName());
            startActivity(new Intent(requireContext(), activityClass));
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), "No se pudo abrir este favorito", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
