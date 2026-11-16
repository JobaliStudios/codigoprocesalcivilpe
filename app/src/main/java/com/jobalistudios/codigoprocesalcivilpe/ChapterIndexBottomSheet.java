package com.jobalistudios.codigoprocesalcivilpe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ReaderChapterProgress;

import java.util.ArrayList;
import java.util.List;

/** Índice de un único bloque jurídico, consultable sin conexión y sin modificar marcadores. */
public final class ChapterIndexBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ChapterIndexBottomSheet";
    public static final String RESULT_KEY = "chapterIndexAction";
    public static final String RESULT_ACTION = "action";
    public static final String RESULT_ARTICLE = "article";
    public static final String ACTION_OPEN = "open";
    public static final String ACTION_OTHER_CHAPTER = "other";
    public static final String ACTION_GO_TO_ARTICLE = "go";

    private static final String ARG_BLOCK = "block";
    private static final String ARG_ARTICLE = "current";
    private static final String ARG_TITLE = "title";
    private static final String ARG_READING_PERCENT = "readingPercent";
    private static final String STATE_QUERY = "query";
    private static final String STATE_FILTER = "filter";

    private String blockKey;
    private String currentArticle;
    private String blockTitle;
    private int readingPercent;
    private ChapterIndexStateBuilder.Filter filter = ChapterIndexStateBuilder.Filter.ALL;
    private TextInputEditText search;
    private RecyclerView list;
    private TextView empty;
    private ArticleAdapter adapter;

    @NonNull
    public static ChapterIndexBottomSheet newInstance(
            @NonNull String blockKey, @Nullable String currentArticle,
            @NonNull String title, int readingPercent) {
        ChapterIndexBottomSheet sheet = new ChapterIndexBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_BLOCK, blockKey);
        args.putString(ARG_ARTICLE, currentArticle);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_READING_PERCENT, Math.max(0, Math.min(100, readingPercent)));
        sheet.setArguments(args);
        return sheet;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedState) {
        return new BottomSheetDialog(requireContext(), R.style.App_Reader_Sheet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.bottom_sheet_chapter_index, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedState) {
        super.onViewCreated(root, savedState);
        Bundle args = requireArguments();
        blockKey = args.getString(ARG_BLOCK, "");
        currentArticle = args.getString(ARG_ARTICLE, "");
        blockTitle = args.getString(ARG_TITLE, "");
        readingPercent = args.getInt(ARG_READING_PERCENT, 0);
        if (savedState != null) {
            String stored = savedState.getString(STATE_FILTER, "ALL");
            try {
                filter = ChapterIndexStateBuilder.Filter.valueOf(stored);
            } catch (IllegalArgumentException invalid) {
                filter = ChapterIndexStateBuilder.Filter.ALL;
            }
        }

        TextView titleView = root.findViewById(R.id.chapterIndexTitle);
        TextView subtitle = root.findViewById(R.id.chapterIndexSubtitle);
        titleView.setText(blockTitle.isEmpty() ? getString(R.string.reader_index_title) : blockTitle);
        ViewCompat.setAccessibilityHeading(titleView, true);
        ArticleBlock block = ArticleRepository.getBlock(requireContext(), blockKey);
        ReaderChapterProgress progress = ReaderChapterProgress.of(block, currentArticle);
        subtitle.setText(progress.position > 0
                ? getString(R.string.reader_index_subtitle, progress.total, currentArticle)
                : getString(R.string.reader_index_subtitle_empty, progress.total));

        list = root.findViewById(R.id.chapterIndexList);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ArticleAdapter();
        list.setAdapter(adapter);
        empty = root.findViewById(R.id.chapterIndexEmpty);
        search = root.findViewById(R.id.chapterIndexSearch);

        ChipGroup chips = root.findViewById(R.id.chapterIndexFilters);
        int selected = selectedFilterId(filter);
        chips.check(selected);
        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            filter = filterForId(checkedIds.get(0));
            refresh();
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                refresh();
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        if (savedState != null) {
            search.setText(savedState.getString(STATE_QUERY, ""));
            search.setSelection(search.length());
        }
        root.findViewById(R.id.chapterIndexClose).setOnClickListener(v -> dismiss());
        root.findViewById(R.id.chapterIndexOtherChapter).setOnClickListener(
                v -> emit(ACTION_OTHER_CHAPTER, null));
        root.findViewById(R.id.chapterIndexGoToArticle).setOnClickListener(
                v -> emit(ACTION_GO_TO_ARTICLE, null));
        ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
        root.setBackgroundColor(palette.surface);
        titleView.setTextColor(palette.text);
        subtitle.setTextColor(palette.muted);
        refresh();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(getDialog() instanceof BottomSheetDialog)) return;
        View sheet = ((BottomSheetDialog) requireDialog())
                .findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet == null) return;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxWidth = getResources().getDimensionPixelSize(R.dimen.reader_sheet_max_width);
        ViewGroup.LayoutParams params = sheet.getLayoutParams();
        params.width = Math.min(screenWidth, maxWidth);
        sheet.setLayoutParams(params);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_FILTER, filter.name());
        if (search != null && search.getText() != null) {
            outState.putString(STATE_QUERY, search.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        search = null;
        list = null;
        empty = null;
        adapter = null;
        super.onDestroyView();
    }

    private void refresh() {
        if (adapter == null || search == null) return;
        List<ChapterIndexStateBuilder.Entry> entries = new ChapterIndexStateBuilder(requireContext())
                .build(blockKey, currentArticle, search.getText() == null
                        ? "" : search.getText().toString(), filter);
        adapter.setEntries(entries);
        if (empty != null) empty.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
        if (list != null) list.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void emit(@NonNull String action, @Nullable String article) {
        Bundle result = new Bundle();
        result.putString(RESULT_ACTION, action);
        if (article != null) result.putString(RESULT_ARTICLE, article);
        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
        dismiss();
    }

    private int selectedFilterId(ChapterIndexStateBuilder.Filter value) {
        switch (value) {
            case FAVORITES: return R.id.chapterIndexFavorites;
            case NOTES: return R.id.chapterIndexNotes;
            case HIGHLIGHTS: return R.id.chapterIndexHighlights;
            case ALL:
            default: return R.id.chapterIndexAll;
        }
    }

    private ChapterIndexStateBuilder.Filter filterForId(int id) {
        if (id == R.id.chapterIndexFavorites) return ChapterIndexStateBuilder.Filter.FAVORITES;
        if (id == R.id.chapterIndexNotes) return ChapterIndexStateBuilder.Filter.NOTES;
        if (id == R.id.chapterIndexHighlights) return ChapterIndexStateBuilder.Filter.HIGHLIGHTS;
        return ChapterIndexStateBuilder.Filter.ALL;
    }

    private final class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.Holder> {
        private List<ChapterIndexStateBuilder.Entry> entries = new ArrayList<>();
        void setEntries(List<ChapterIndexStateBuilder.Entry> next) {
            entries = new ArrayList<>(next);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter_index_article, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int position) {
            ChapterIndexStateBuilder.Entry item = entries.get(position);
            ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
            h.number.setText(item.number);
            h.summary.setText(item.summary);
            h.number.setTextColor(item.current ? palette.onPrimary : palette.primary);
            h.number.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    item.current ? palette.primary : palette.container));
            h.card.setCardBackgroundColor(item.current ? palette.container : palette.surface);
            h.card.setStrokeColor(item.current ? palette.primary : palette.divider);
            h.summary.setTextColor(palette.text);
            h.current.setTextColor(palette.primary);
            h.current.setVisibility(item.current ? View.VISIBLE : View.GONE);
            if (item.current) {
                h.current.setText(getString(R.string.reader_index_reading, readingPercent));
            }
            StringBuilder markers = new StringBuilder();
            StringBuilder details = new StringBuilder();
            if (item.favorite) {
                markers.append("★");
                details.append(getString(R.string.reader_index_status_favorite));
            }
            if (item.note) {
                if (markers.length() != 0) markers.append("  ");
                markers.append("✎");
                if (details.length() != 0) details.append(", ");
                details.append(getString(R.string.reader_index_status_note));
            }
            if (item.highlight) {
                if (markers.length() != 0) markers.append("  ");
                markers.append("▰");
                if (details.length() != 0) details.append(", ");
                details.append(getString(R.string.reader_index_status_highlight));
            }
            h.indicators.setText(markers.toString());
            h.indicators.setTextColor(palette.primary);
            h.indicators.setVisibility(markers.length() == 0 ? View.GONE : View.VISIBLE);
            if (item.current) {
                if (details.length() != 0) details.append(", ");
                details.append(getString(R.string.reader_index_status_current));
            }
            h.itemView.setContentDescription(getString(R.string.reader_index_row_accessibility,
                    item.number, item.summary,
                    details.length() == 0 ? getString(R.string.reader_index_status_none) : details));
            h.itemView.setOnClickListener(v -> emit(ACTION_OPEN, item.number));
        }

        @Override public int getItemCount() { return entries.size(); }

        final class Holder extends RecyclerView.ViewHolder {
            final MaterialCardView card;
            final TextView number, summary, current, indicators;
            Holder(@NonNull View view) {
                super(view);
                card = (MaterialCardView) view;
                number = view.findViewById(R.id.chapterIndexNumber);
                summary = view.findViewById(R.id.chapterIndexSummary);
                current = view.findViewById(R.id.chapterIndexCurrent);
                indicators = view.findViewById(R.id.chapterIndexIndicators);
            }
        }
    }
}
