package com.jobalistudios.codigoprocesalcivilpe.glosario;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.ReaderAppearancePalette;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.List;

/** Hoja editorial; nunca sustituye el texto legal ni muestra contenido no aprobado. */
public final class GlossaryBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "GlossaryBottomSheet";
    public static final String RESULT_KEY = "glossaryArticleAction";
    public static final String RESULT_ARTICLE = "article";
    private static final String ARG_TERM = "termId";
    private static final String STATE_TERM = "activeTermId";
    private String activeTermId;

    @NonNull
    public static GlossaryBottomSheet newInstance(@NonNull String termId) {
        GlossaryBottomSheet sheet = new GlossaryBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_TERM, termId);
        sheet.setArguments(args);
        return sheet;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedState) {
        return new BottomSheetDialog(requireContext(), R.style.App_Reader_Sheet);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.bottom_sheet_glossary, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedState) {
        super.onViewCreated(root, savedState);
        activeTermId = savedState == null
                ? requireArguments().getString(ARG_TERM, "")
                : savedState.getString(STATE_TERM,
                requireArguments().getString(ARG_TERM, ""));
        if (!ReadingPreferenceManager.isGlossaryEnabled(requireContext())
                || GlossaryRepository.find(requireContext(), activeTermId) == null) {
            dismissAllowingStateLoss();
            return;
        }
        displayTerm(root, activeTermId);
    }

    private void displayTerm(@NonNull View root, @NonNull String id) {
        GlossaryRepository.Term term = GlossaryRepository.find(requireContext(), id);
        if (term == null) return;
        activeTermId = id;
        ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
        root.setBackgroundColor(palette.surface);
        TextView heading = root.findViewById(R.id.glossaryTerm);
        heading.setText(term.getTerm());
        heading.setTextColor(palette.text);
        ViewCompat.setAccessibilityHeading(heading, true);
        TextView definition = root.findViewById(R.id.glossaryDefinition);
        definition.setText(term.getDefinition());
        definition.setTextColor(palette.text);
        TextView label = root.findViewById(R.id.glossaryLabel);
        label.setTextColor(palette.primary);
        TextView warning = root.findViewById(R.id.glossaryDisclaimer);
        warning.setText(getString(R.string.glossary_editorial_warning,
                term.getSource(), term.getReviewedAt()));
        warning.setTextColor(palette.muted);
        TextView reviewer = root.findViewById(R.id.glossaryReviewer);
        reviewer.setText(getString(R.string.glossary_reviewed_by, term.getReviewedBy()));
        reviewer.setTextColor(palette.muted);

        ChipGroup articles = root.findViewById(R.id.glossaryArticleChips);
        articles.removeAllViews();
        for (String number : term.getArticles()) {
            if (ArticleNavigationResolver.resolve(requireContext(), number) == null) continue;
            Chip chip = createChip(getString(R.string.glossary_article_label, number), palette);
            chip.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(RESULT_ARTICLE, number);
                getParentFragmentManager().setFragmentResult(RESULT_KEY, bundle);
                dismiss();
            });
            articles.addView(chip);
        }
        root.findViewById(R.id.glossaryArticlesContainer)
                .setVisibility(articles.getChildCount() == 0 ? View.GONE : View.VISIBLE);

        ChipGroup related = root.findViewById(R.id.glossaryRelatedChips);
        related.removeAllViews();
        for (String relatedId : term.getRelated()) {
            GlossaryRepository.Term linked = GlossaryRepository.find(requireContext(), relatedId);
            if (linked == null) continue;
            Chip chip = createChip(linked.getTerm(), palette);
            chip.setContentDescription(getString(R.string.glossary_term_accessibility,
                    linked.getTerm()));
            chip.setOnClickListener(v -> displayTerm(root, relatedId));
            related.addView(chip);
        }
        root.findViewById(R.id.glossaryRelatedContainer)
                .setVisibility(related.getChildCount() == 0 ? View.GONE : View.VISIBLE);

        root.findViewById(R.id.glossaryCopy).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText(term.getTerm(),
                        term.getTerm() + "\n" + term.getDefinition() + "\n"
                                + getString(R.string.glossary_editorial_warning,
                                term.getSource(), term.getReviewedAt())));
                Toast.makeText(requireContext(), R.string.glossary_copied,
                        Toast.LENGTH_SHORT).show();
            }
        });
        root.findViewById(R.id.glossaryBrowse).setOnClickListener(v -> {
            dismiss();
            GlossaryListBottomSheet list = new GlossaryListBottomSheet();
            list.show(getParentFragmentManager(), GlossaryListBottomSheet.TAG);
        });
        ScrollView scroll = root.findViewById(R.id.glossaryScroll);
        scroll.post(() -> scroll.scrollTo(0, 0));
    }

    private Chip createChip(String text, ReaderAppearancePalette palette) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(false);
        chip.setTextColor(palette.primary);
        chip.setChipBackgroundColor(ColorStateList.valueOf(palette.container));
        chip.setMinHeight(getResources().getDimensionPixelSize(R.dimen.touch_target_min));
        return chip;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_TERM, activeTermId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(getDialog() instanceof BottomSheetDialog)) return;
        View sheet = ((BottomSheetDialog) requireDialog())
                .findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet == null) return;
        ViewGroup.LayoutParams params = sheet.getLayoutParams();
        params.width = Math.min(getResources().getDisplayMetrics().widthPixels,
                getResources().getDimensionPixelSize(R.dimen.reader_sheet_max_width));
        sheet.setLayoutParams(params);
        BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
