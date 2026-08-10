package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class FavoritosAdapter extends
        ListAdapter<FavoriteListItem, FavoritosAdapter.FavoritoViewHolder> {

    public interface OnFavoriteClickListener {
        void onOpen(FavoriteItem item);
        void onRemove(FavoriteItem item);
    }

    private static final DiffUtil.ItemCallback<FavoriteListItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FavoriteListItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull FavoriteListItem oldItem,
                        @NonNull FavoriteListItem newItem
                ) {
                    return oldItem.getFavorite().getId()
                            .equals(newItem.getFavorite().getId());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull FavoriteListItem oldItem,
                        @NonNull FavoriteListItem newItem
                ) {
                    return oldItem.hasSameContent(newItem);
                }
            };

    private final OnFavoriteClickListener listener;

    public FavoritosAdapter(OnFavoriteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
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
        holder.bind(getItem(position), listener);
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {

        private final TextView textTitle;
        private final TextView textSubtitle;
        private final TextView textType;
        private final View indicators;
        private final Chip noteChip;
        private final Chip highlightChip;
        private final ImageButton buttonRemove;

        FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.favoriteTitle);
            textSubtitle = itemView.findViewById(R.id.favoriteSubtitle);
            textType = itemView.findViewById(R.id.favoriteType);
            indicators = itemView.findViewById(R.id.favoriteIndicators);
            noteChip = itemView.findViewById(R.id.favoriteNoteIndicator);
            highlightChip = itemView.findViewById(R.id.favoriteHighlightIndicator);
            buttonRemove = itemView.findViewById(R.id.buttonRemoveFavorite);
        }

        void bind(FavoriteListItem listItem, OnFavoriteClickListener listener) {
            FavoriteItem item = listItem.getFavorite();
            textTitle.setText(item.getTitle());
            textSubtitle.setText(item.getSubtitle());
            textSubtitle.setVisibility(TextUtils.isEmpty(item.getSubtitle())
                    ? View.GONE
                    : View.VISIBLE);
            textType.setText(item.getType());
            textType.setVisibility(listItem.isArticle() ? View.GONE : View.VISIBLE);

            noteChip.setVisibility(listItem.hasNote() ? View.VISIBLE : View.GONE);
            highlightChip.setVisibility(listItem.hasHighlight() ? View.VISIBLE : View.GONE);
            indicators.setVisibility(listItem.hasNote() || listItem.hasHighlight()
                    ? View.VISIBLE
                    : View.GONE);

            itemView.setContentDescription(buildContentDescription(listItem));
            itemView.setOnClickListener(view -> listener.onOpen(item));
            buttonRemove.setContentDescription(itemView.getContext().getString(
                    R.string.favorite_remove_dynamic,
                    item.getTitle()
            ));
            buttonRemove.setOnClickListener(view -> listener.onRemove(item));
        }

        private String buildContentDescription(FavoriteListItem item) {
            FavoriteItem favorite = item.getFavorite();
            StringBuilder description = new StringBuilder(favorite.getTitle());
            if (!TextUtils.isEmpty(favorite.getSubtitle())) {
                description.append(". ").append(favorite.getSubtitle());
            }
            if (item.hasNote() && item.hasHighlight()) {
                description.append(". ").append(itemView.getContext().getString(
                        R.string.favorite_has_note_and_highlight));
            } else if (item.hasNote()) {
                description.append(". ").append(itemView.getContext().getString(
                        R.string.favorite_has_note));
            } else if (item.hasHighlight()) {
                description.append(". ").append(itemView.getContext().getString(
                        R.string.favorite_has_highlight));
            }
            return description.toString();
        }
    }
}
