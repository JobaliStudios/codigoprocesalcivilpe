package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.ArrayList;
import java.util.List;

public class BusquedaResultsAdapter extends RecyclerView.Adapter<BusquedaResultsAdapter.ResultViewHolder> {

    public interface OnResultClickListener {
        void onResultClicked(BusquedaViewModel.LegalSearchResult result);
    }

    private final List<BusquedaViewModel.LegalSearchResult> items = new ArrayList<>();
    private final OnResultClickListener listener;

    public BusquedaResultsAdapter(OnResultClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BusquedaViewModel.LegalSearchResult> results) {
        items.clear();
        items.addAll(results);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado_busqueda, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        BusquedaViewModel.LegalSearchResult result = items.get(position);
        holder.title.setText(result.item.title);
        holder.meta.setText(result.item.sectionName + " • " + result.item.articleRange);
        holder.snippet.setText(result.item.snippet);
        holder.itemView.setOnClickListener(v -> listener.onResultClicked(result));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView meta;
        TextView snippet;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitulo);
            meta = itemView.findViewById(R.id.textMeta);
            snippet = itemView.findViewById(R.id.textSnippet);
        }
    }
}
