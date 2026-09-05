package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ItemMyNotesArticleBinding;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ItemMyNotesTextBlockBinding;

/** Una card por artículo; los bloques internos conservan el orden de lectura. */
final class MyNotesAdapter extends ListAdapter<ArticleNotesEntry, MyNotesAdapter.ViewHolder> {

    interface OnArticleClickListener {
        void onOpenArticle(@NonNull String articleNumber);
    }

    private static final DiffUtil.ItemCallback<ArticleNotesEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ArticleNotesEntry>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ArticleNotesEntry oldItem,
                        @NonNull ArticleNotesEntry newItem
                ) {
                    return oldItem.getArticleNumber().equals(newItem.getArticleNumber());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ArticleNotesEntry oldItem,
                        @NonNull ArticleNotesEntry newItem
                ) {
                    return oldItem.equals(newItem);
                }
            };

    private final OnArticleClickListener listener;

    MyNotesAdapter(@NonNull OnArticleClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMyNotesArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyNotesArticleBinding binding;

        ViewHolder(ItemMyNotesArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ArticleNotesEntry entry, OnArticleClickListener listener) {
            String heading = binding.getRoot().getContext().getString(
                    R.string.my_notes_article_number,
                    entry.getArticleNumber()
            );
            binding.myNotesArticleNumber.setText(heading);
            binding.myNotesArticleTitle.setText(entry.getArticleTitle());
            binding.myNotesArticleTitle.setVisibility(
                    TextUtils.isEmpty(entry.getArticleTitle()) ? View.GONE : View.VISIBLE
            );
            binding.myNotesFavorite.setVisibility(entry.isFavorite() ? View.VISIBLE : View.GONE);
            binding.myNotesArticleHeader.setContentDescription(TextUtils.isEmpty(
                    entry.getArticleTitle())
                    ? heading
                    : binding.getRoot().getContext().getString(
                    R.string.my_notes_article_accessibility,
                    entry.getArticleNumber(),
                    entry.getArticleTitle()
            ));
            binding.myNotesArticleHeader.setOnClickListener(
                    view -> listener.onOpenArticle(entry.getArticleNumber())
            );

            binding.myNotesContent.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(binding.getRoot().getContext());
            for (ArticleNotesEntry.HighlightEntry highlight : entry.getHighlights()) {
                addTextBlock(inflater, R.string.my_notes_highlight_label, highlight.getText());
                if (highlight.hasNote()) {
                    addTextBlock(inflater, R.string.my_notes_personal_note_label,
                            highlight.getNote());
                }
            }
            for (ArticleNotesEntry.NoteEntry note : entry.getArticleNotes()) {
                addTextBlock(inflater, R.string.my_notes_article_note_label, note.getText());
            }
        }

        private void addTextBlock(LayoutInflater inflater, int labelRes, String text) {
            ItemMyNotesTextBlockBinding block = ItemMyNotesTextBlockBinding.inflate(
                    inflater,
                    binding.myNotesContent,
                    false
            );
            block.myNotesBlockLabel.setText(labelRes);
            block.myNotesBlockText.setText(text);
            binding.myNotesContent.addView(block.getRoot());
        }
    }
}
