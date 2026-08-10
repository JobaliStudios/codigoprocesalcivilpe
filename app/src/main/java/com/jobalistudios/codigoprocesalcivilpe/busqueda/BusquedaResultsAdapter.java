package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;

/** Presenta resultados ya calculados y aplica únicamente spans visuales al snippet. */
public class BusquedaResultsAdapter
        extends ListAdapter<ArticleSearchResult, BusquedaResultsAdapter.ResultViewHolder> {

    public interface OnResultClickListener {
        void onResultClicked(ArticleSearchResult result);
    }

    private static final DiffUtil.ItemCallback<ArticleSearchResult> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ArticleSearchResult>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ArticleSearchResult oldItem,
                        @NonNull ArticleSearchResult newItem
                ) {
                    return oldItem.item.article.number.equals(newItem.item.article.number);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ArticleSearchResult oldItem,
                        @NonNull ArticleSearchResult newItem
                ) {
                    return oldItem.snippet.equals(newItem.snippet)
                            && oldItem.relevance == newItem.relevance
                            && oldItem.isFavorite == newItem.isFavorite
                            && rangesEqual(oldItem, newItem);
                }
            };

    private final OnResultClickListener listener;

    public BusquedaResultsAdapter(OnResultClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado_busqueda, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ArticleSearchResult result = getItem(position);
        String displayTitle = result.item.displayTitle();
        String metadata = result.item.sectionName + " · " + result.item.articleRange;
        holder.title.setText(displayTitle);
        holder.meta.setText(metadata);
        holder.snippet.setText(highlightedSnippet(holder, result));
        holder.favoriteIndicator.setVisibility(result.isFavorite ? View.VISIBLE : View.GONE);
        holder.itemView.setContentDescription(displayTitle + ", " + metadata + ". " + result.snippet);
        holder.itemView.setOnClickListener(view -> listener.onResultClicked(result));
    }

    private SpannableString highlightedSnippet(ResultViewHolder holder, ArticleSearchResult result) {
        SpannableString text = new SpannableString(result.snippet);
        int primary = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary);
        for (SearchTextNormalizer.Range range : result.highlightRanges) {
            if (range.start < 0 || range.end > text.length() || range.start >= range.end) {
                continue;
            }
            text.setSpan(new StyleSpan(Typeface.BOLD), range.start, range.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new ForegroundColorSpan(primary), range.start, range.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return text;
    }

    private static boolean rangesEqual(ArticleSearchResult first, ArticleSearchResult second) {
        if (first.highlightRanges.size() != second.highlightRanges.size()) {
            return false;
        }
        for (int index = 0; index < first.highlightRanges.size(); index++) {
            SearchTextNormalizer.Range left = first.highlightRanges.get(index);
            SearchTextNormalizer.Range right = second.highlightRanges.get(index);
            if (left.start != right.start || left.end != right.end) {
                return false;
            }
        }
        return true;
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView meta;
        final TextView snippet;
        final TextView favoriteIndicator;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitulo);
            meta = itemView.findViewById(R.id.textMeta);
            snippet = itemView.findViewById(R.id.textSnippet);
            favoriteIndicator = itemView.findViewById(R.id.textFavoriteIndicator);
        }
    }
}
