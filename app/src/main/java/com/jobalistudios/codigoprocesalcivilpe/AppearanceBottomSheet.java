package com.jobalistudios.codigoprocesalcivilpe;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

/** Edición local y en vivo de los ajustes de lectura, sin alterar el contenido legal. */
public final class AppearanceBottomSheet extends BottomSheetDialogFragment {
    public static final String RESULT_KEY = "reader_appearance_changed";
    private boolean updating;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle state) {
        return new BottomSheetDialog(requireContext(), R.style.App_Reader_Sheet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.bottom_sheet_reader_appearance, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(getDialog() instanceof BottomSheetDialog)) return;
        View bottom = ((BottomSheetDialog) getDialog())
                .findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottom != null) {
            ViewGroup.LayoutParams params = bottom.getLayoutParams();
            params.width = Math.min(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDimensionPixelSize(R.dimen.reader_sheet_max_width));
            if (params instanceof CoordinatorLayout.LayoutParams) {
                ((CoordinatorLayout.LayoutParams) params).gravity =
                        android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
            }
            bottom.setLayoutParams(params);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        refreshFromPreferences(view);

        onSelected(view, R.id.readerThemeGroup, (selected) -> {
            ReadingPreferenceManager.ReaderTheme value = selected == R.id.readerLight
                    ? ReadingPreferenceManager.ReaderTheme.LIGHT
                    : selected == R.id.readerSepia
                    ? ReadingPreferenceManager.ReaderTheme.SEPIA
                    : selected == R.id.readerDark
                    ? ReadingPreferenceManager.ReaderTheme.DARK
                    : ReadingPreferenceManager.ReaderTheme.AUTO;
            ReadingPreferenceManager.setReaderTheme(requireContext(), value);
            changed(view);
        });

        onSelected(view, R.id.readerFontGroup, (selected) -> {
            ReadingPreferenceManager.ReaderFont value = selected == R.id.fontSerif
                    ? ReadingPreferenceManager.ReaderFont.SERIF
                    : selected == R.id.fontAccessible
                    ? ReadingPreferenceManager.ReaderFont.ACCESSIBLE
                    : ReadingPreferenceManager.ReaderFont.SANS;
            ReadingPreferenceManager.setReaderFont(requireContext(), value);
            changed(view);
        });

        onSelected(view, R.id.readerLineGroup, (selected) -> {
            ReadingPreferenceManager.LineSpacing value = selected == R.id.lineCompact
                    ? ReadingPreferenceManager.LineSpacing.COMPACT
                    : selected == R.id.lineWide
                    ? ReadingPreferenceManager.LineSpacing.WIDE
                    : ReadingPreferenceManager.LineSpacing.NORMAL;
            ReadingPreferenceManager.setLineSpacing(requireContext(), value);
            changed(view);
        });

        onSelected(view, R.id.readerMarginGroup, (selected) -> {
            ReadingPreferenceManager.Margins value = selected == R.id.marginNarrow
                    ? ReadingPreferenceManager.Margins.NARROW
                    : selected == R.id.marginWide
                    ? ReadingPreferenceManager.Margins.WIDE
                    : ReadingPreferenceManager.Margins.NORMAL;
            ReadingPreferenceManager.setMargins(requireContext(), value);
            changed(view);
        });

        Slider scale = view.findViewById(R.id.fontScaleSlider);
        scale.addOnChangeListener((slider, value, fromUser) -> {
            if (!fromUser || updating) return;
            ReadingPreferenceManager.setContentFontScale(requireContext(), value / 100f);
            changed(view);
        });

        SwitchMaterial justify = view.findViewById(R.id.readerJustifySwitch);
        justify.setOnCheckedChangeListener((button, enabled) -> {
            if (updating) return;
            ReadingPreferenceManager.setJustifyEnabled(requireContext(), enabled);
            changed(view);
        });
        SwitchMaterial focus = view.findViewById(R.id.readerFocusSwitch);
        focus.setOnCheckedChangeListener((button, enabled) -> {
            if (updating) return;
            ReadingPreferenceManager.setFocusDefaultEnabled(requireContext(), enabled);
            changed(view);
        });
        SwitchMaterial awake = view.findViewById(R.id.readerKeepAwakeSwitch);
        awake.setOnCheckedChangeListener((button, enabled) -> {
            if (updating) return;
            ReadingPreferenceManager.setKeepScreenOnEnabled(requireContext(), enabled);
            changed(view);
        });
        view.findViewById(R.id.appearanceReset).setOnClickListener(button -> {
            ReadingPreferenceManager.resetReadingPreferences(requireContext());
            refreshFromPreferences(view);
            changed(view);
        });
        view.findViewById(R.id.appearanceDone).setOnClickListener(button -> dismiss());
    }

    private void refreshFromPreferences(@NonNull View view) {
        updating = true;
        try {
            switch (ReadingPreferenceManager.getReaderTheme(requireContext())) {
                case LIGHT: check(view, R.id.readerThemeGroup, R.id.readerLight); break;
                case SEPIA: check(view, R.id.readerThemeGroup, R.id.readerSepia); break;
                case DARK: check(view, R.id.readerThemeGroup, R.id.readerDark); break;
                case AUTO:
                default: check(view, R.id.readerThemeGroup, R.id.readerAuto);
            }
            switch (ReadingPreferenceManager.getReaderFont(requireContext())) {
                case SERIF: check(view, R.id.readerFontGroup, R.id.fontSerif); break;
                case ACCESSIBLE: check(view, R.id.readerFontGroup, R.id.fontAccessible); break;
                case SANS:
                default: check(view, R.id.readerFontGroup, R.id.fontSans);
            }
            switch (ReadingPreferenceManager.getLineSpacing(requireContext())) {
                case COMPACT: check(view, R.id.readerLineGroup, R.id.lineCompact); break;
                case WIDE: check(view, R.id.readerLineGroup, R.id.lineWide); break;
                case NORMAL:
                default: check(view, R.id.readerLineGroup, R.id.lineNormal);
            }
            switch (ReadingPreferenceManager.getMargins(requireContext())) {
                case NARROW: check(view, R.id.readerMarginGroup, R.id.marginNarrow); break;
                case WIDE: check(view, R.id.readerMarginGroup, R.id.marginWide); break;
                case NORMAL:
                default: check(view, R.id.readerMarginGroup, R.id.marginNormal);
            }
            int percentage = Math.round(ReadingPreferenceManager
                    .getContentFontScale(requireContext()) * 100);
            ((Slider) view.findViewById(R.id.fontScaleSlider)).setValue(percentage);
            ((SwitchMaterial) view.findViewById(R.id.readerJustifySwitch))
                    .setChecked(ReadingPreferenceManager.isJustifyEnabled(requireContext()));
            ((SwitchMaterial) view.findViewById(R.id.readerFocusSwitch))
                    .setChecked(ReadingPreferenceManager.isFocusDefaultEnabled(requireContext()));
            ((SwitchMaterial) view.findViewById(R.id.readerKeepAwakeSwitch))
                    .setChecked(ReadingPreferenceManager.isKeepScreenOnEnabled(requireContext()));
        } finally {
            updating = false;
        }
        updatePreview(view);
    }

    private void check(View root, int groupId, int buttonId) {
        ((MaterialButtonToggleGroup) root.findViewById(groupId)).check(buttonId);
    }

    private void changed(View view) {
        updatePreview(view);
        getParentFragmentManager().setFragmentResult(RESULT_KEY, new Bundle());
    }

    private void updatePreview(View view) {
        ReaderAppearancePalette palette = ReaderAppearancePalette.from(requireContext());
        MaterialCardView card = view.findViewById(R.id.previewCard);
        TextView body = view.findViewById(R.id.previewText);
        card.setCardBackgroundColor(palette.container);
        body.setTextColor(palette.text);
        body.setTypeface(ReaderAppearancePalette.getBodyTypeface(requireContext()));
        body.setLineSpacing(0f,
                ReadingPreferenceManager.getLineSpacing(requireContext()).multiplier);
        body.setJustificationMode(ReadingPreferenceManager.isJustifyEnabled(requireContext())
                ? android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
                : android.text.Layout.JUSTIFICATION_MODE_NONE);
        body.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimension(R.dimen.reader_body_text_size)
                * ReadingPreferenceManager.getContentFontScale(requireContext()));
        TextView percentageLabel = view.findViewById(R.id.fontScaleLabel);
        percentageLabel.setText(getString(R.string.reader_appearance_size_value,
                Math.round(ReadingPreferenceManager.getContentFontScale(requireContext()) * 100)));
    }

    private interface SelectionHandler {
        void selected(int buttonId);
    }

    private void onSelected(View view, int groupId, SelectionHandler handler) {
        MaterialButtonToggleGroup group = view.findViewById(groupId);
        group.addOnButtonCheckedListener((buttons, checkedId, checked) -> {
            if (checked && !updating) handler.selected(checkedId);
        });
    }
}
