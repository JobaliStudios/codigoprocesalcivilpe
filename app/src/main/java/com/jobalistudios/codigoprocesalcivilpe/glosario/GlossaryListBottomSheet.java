package com.jobalistudios.codigoprocesalcivilpe.glosario;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.ReaderAppearancePalette;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

/** Lista editorial sencilla, con búsqueda local, independiente del índice jurídico. */
public final class GlossaryListBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "GlossaryListBottomSheet";
    private static final String STATE_QUERY = "glossaryQuery";
    private TextInputEditText query;

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle state) {
        return new BottomSheetDialog(requireContext(), R.style.App_Reader_Sheet);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.bottom_sheet_glossary_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle state) {
        super.onViewCreated(root, state);
        if (!ReadingPreferenceManager.isGlossaryEnabled(requireContext())
                || GlossaryRepository.getTerms(requireContext()).isEmpty()) {
            dismissAllowingStateLoss();
            return;
        }
        ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
        root.setBackgroundColor(palette.surface);
        TextView title = root.findViewById(R.id.glossaryListTitle);
        title.setTextColor(palette.text);
        ViewCompat.setAccessibilityHeading(title, true);
        root.findViewById(R.id.glossaryListClose).setOnClickListener(v -> dismiss());
        query = root.findViewById(R.id.glossaryListSearch);
        query.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int st, int before, int count) {
                filter(root, s.toString());
            }
            @Override public void afterTextChanged(Editable value) {}
        });
        if (state != null) query.setText(state.getString(STATE_QUERY, ""));
        filter(root, query.getText() == null ? "" : query.getText().toString());
    }

    private void filter(View root, String text) {
        String wanted = SearchTextNormalizer.normalizePlain(text);
        LinearLayout items = root.findViewById(R.id.glossaryListItems);
        items.removeAllViews();
        ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
        for (GlossaryRepository.Term term : GlossaryRepository.getTerms(requireContext())) {
            boolean match = SearchTextNormalizer.normalizePlain(
                    term.getTerm()).contains(wanted);
            if (!match) {
                for (String alias : term.getAliases()) {
                    if (SearchTextNormalizer.normalizePlain(alias).contains(wanted)) {
                        match = true;
                        break;
                    }
                }
            }
            if (!match) continue;
            MaterialButton button = new MaterialButton(requireContext());
            button.setText(term.getTerm());
            button.setAllCaps(false);
            button.setTextColor(palette.primary);
            button.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(palette.container));
            button.setMinHeight(getResources().getDimensionPixelSize(R.dimen.touch_target_min));
            button.setContentDescription(getString(R.string.glossary_term_accessibility,
                    term.getTerm()));
            button.setOnClickListener(v -> {
                dismiss();
                GlossaryBottomSheet.newInstance(term.getId())
                        .show(getParentFragmentManager(), GlossaryBottomSheet.TAG);
            });
            items.addView(button, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        boolean empty = items.getChildCount() == 0;
        root.findViewById(R.id.glossaryListEmpty).setVisibility(
                empty ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.glossaryListScroll).setVisibility(
                empty ? View.GONE : View.VISIBLE);
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        if (query != null && query.getText() != null)
            outState.putString(STATE_QUERY, query.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroyView() {
        query = null;
        super.onDestroyView();
    }

    @Override public void onStart() {
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
