package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteItem item);
    }

    private final List<FavoriteItem> items = new ArrayList<>();
    private final OnFavoriteClickListener onFavoriteClickListener;

    public FavoritesAdapter(OnFavoriteClickListener onFavoriteClickListener) {
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    public void submitList(List<FavoriteItem> favorites) {
        items.clear();
        items.addAll(favorites);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(items.get(position), onFavoriteClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView typeText;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.favoriteTitle);
            typeText = itemView.findViewById(R.id.favoriteType);
        }

        void bind(FavoriteItem item, OnFavoriteClickListener listener) {
            titleText.setText(item.getTitle());
            typeText.setText(item.getType());
            itemView.setOnClickListener(view -> listener.onFavoriteClick(item));
        }
    }
}
