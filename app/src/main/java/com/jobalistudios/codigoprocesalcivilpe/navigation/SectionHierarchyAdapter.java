package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SectionHierarchyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_GROUP = 0;
    private static final int TYPE_ITEM = 1;

    public interface OnSectionItemClickListener {
        void onSectionItemClick(SectionItem item);
    }

    private static class Row {
        final SectionGroup group;
        final SectionItem item;

        Row(SectionGroup group) {
            this.group = group;
            this.item = null;
        }

        Row(SectionGroup group, SectionItem item) {
            this.group = group;
            this.item = item;
        }

        boolean isGroup() {
            return item == null;
        }
    }

    private final List<SectionGroup> groups;
    private final List<Row> rows = new ArrayList<>();
    private final Set<Integer> expandedGroups = new HashSet<>();
    private final OnSectionItemClickListener listener;
    private final Context context;

    public SectionHierarchyAdapter(Context context, List<SectionGroup> groups, OnSectionItemClickListener listener) {
        this.context = context;
        this.groups = groups;
        this.listener = listener;
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();
        for (int i = 0; i < groups.size(); i++) {
            SectionGroup group = groups.get(i);
            rows.add(new Row(group));
            if (expandedGroups.contains(i)) {
                for (SectionItem item : group.getItems()) {
                    rows.add(new Row(group, item));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).isGroup() ? TYPE_GROUP : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_GROUP) {
            return new GroupViewHolder(inflater.inflate(R.layout.item_section_group, parent, false));
        }
        return new ItemViewHolder(inflater.inflate(R.layout.item_section_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof GroupViewHolder) {
            bindGroup((GroupViewHolder) holder, row.group, position);
        } else if (holder instanceof ItemViewHolder) {
            bindItem((ItemViewHolder) holder, row.item);
        }
    }

    private void bindGroup(GroupViewHolder holder, SectionGroup group, int position) {
        int groupIndex = getGroupIndexForRow(position);
        boolean expanded = expandedGroups.contains(groupIndex);
        holder.title.setText(group.getTitle());
        String detail = String.format(
                Locale.getDefault(),
                "%s · %d %s",
                context.getString(R.string.group_articles_label),
                group.getArticleCount(),
                context.getResources().getQuantityString(R.plurals.group_articles_count, group.getArticleCount()));
        holder.detail.setText(detail);
        holder.chevron.setRotation(expanded ? 90f : 0f);
        holder.itemView.setOnClickListener(v -> {
            if (expanded) {
                expandedGroups.remove(groupIndex);
            } else {
                expandedGroups.add(groupIndex);
            }
            rebuildRows();
            notifyDataSetChanged();
        });
    }

    private int getGroupIndexForRow(int rowPosition) {
        int count = -1;
        for (int i = 0; i <= rowPosition; i++) {
            if (rows.get(i).isGroup()) {
                count++;
            }
        }
        return count;
    }

    private void bindItem(ItemViewHolder holder, SectionItem item) {
        holder.title.setText(item.getTitleRes());
        holder.subtitle.setText(item.getSubtitleRes());
        holder.range.setText(item.getRangeRes());
        holder.itemView.setOnClickListener(v -> listener.onSectionItemClick(item));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView detail;
        final ImageView chevron;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.groupTitle);
            detail = itemView.findViewById(R.id.groupDetail);
            chevron = itemView.findViewById(R.id.groupChevron);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView range;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sectionTitle);
            subtitle = itemView.findViewById(R.id.sectionSubtitle);
            range = itemView.findViewById(R.id.sectionRange);
        }
    }
}
