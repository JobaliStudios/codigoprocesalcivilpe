package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.FavoritoViewHolder> {

    public interface OnFavoriteClickListener {
        void onOpen(FavoriteItem item);
        void onRemove(FavoriteItem item);
    }

    private final List<FavoriteItem> items = new ArrayList<>();
    private final OnFavoriteClickListener listener;
    private FavoritosViewModel.SortMode sortMode = FavoritosViewModel.SortMode.RECIENTES;

    public FavoritosAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteItem> list) {
        items.clear();
        items.addAll(list);
        sortInternal();
        notifyDataSetChanged();
    }

    public void setSortMode(FavoritosViewModel.SortMode mode) {
        sortMode = mode == null ? FavoritosViewModel.SortMode.RECIENTES : mode;
        sortInternal();
        notifyDataSetChanged();
    }

    private void sortInternal() {
        if (sortMode == FavoritosViewModel.SortMode.RECIENTES) {
            Collections.reverse(items);
            return;
        }

        Comparator<FavoriteItem> comparator;
        if (sortMode == FavoritosViewModel.SortMode.SECCION) {
            comparator = Comparator
                    .comparing(FavoritosViewModel::resolveSection)
                    .thenComparing(FavoriteItem::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator
                    .comparing(FavoriteItem::getType, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(FavoriteItem::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
        items.sort(comparator);
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorito, parent, false);
        return new FavoritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {

        private final TextView textTitle;
        private final TextView textSubtitle;
        private final TextView textType;
        private final ImageButton buttonRemove;

        FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.favoriteTitle);
            textSubtitle = itemView.findViewById(R.id.favoriteSubtitle);
            textType = itemView.findViewById(R.id.favoriteType);
            buttonRemove = itemView.findViewById(R.id.buttonRemoveFavorite);
        }

        void bind(FavoriteItem item, OnFavoriteClickListener listener) {
            textTitle.setText(item.getTitle());
            textSubtitle.setText(item.getSubtitle());
            textType.setText(item.getType());
            itemView.setOnClickListener(v -> listener.onOpen(item));
            buttonRemove.setOnClickListener(v -> listener.onRemove(item));
        }
    }
}
