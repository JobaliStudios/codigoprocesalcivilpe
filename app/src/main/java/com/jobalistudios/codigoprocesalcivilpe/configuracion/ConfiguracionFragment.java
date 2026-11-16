package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.AppearanceBottomSheet;
import com.jobalistudios.codigoprocesalcivilpe.ReaderAccessibilitySizing;
import androidx.core.view.ViewCompat;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentConfiguracionBinding;
import com.jobalistudios.codigoprocesalcivilpe.privacidad.PrivacyCenterActivity;

public class ConfiguracionFragment extends Fragment {

    private FragmentConfiguracionBinding binding;
    private ConfiguracionViewModel configuracionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        configuracionViewModel = new ViewModelProvider(this).get(ConfiguracionViewModel.class);

        binding = FragmentConfiguracionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.configSubtitle.setText(getString(R.string.config_subtitle));
        binding.configTitle.setText(getString(R.string.config_title));
        binding.textVersion.setText(getString(R.string.config_version_value, obtenerVersion()));

        setupSwitchObserver(binding.switchModoLectura, configuracionViewModel.getModoLectura(),
                configuracionViewModel::setModoLectura);

        ReaderAccessibilitySizing.adaptSegmentedGroup(root, R.id.appThemeToggle);
        ViewCompat.setAccessibilityHeading(binding.configTitle, true);
        setupAppThemeSelector();
        setupSwipeNavigationSwitch();
        setupReadingControls();

        binding.cardSoporte.setOnClickListener(v -> openEmailSupport());

        binding.cardAcercaDe.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), AboutActivity.class)));

        binding.cardPrivacidad.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), PrivacyCenterActivity.class)));

        return root;
    }


    private void setupReadingControls() {
        binding.configReaderThemeRow.setOnClickListener(v -> openReadingAppearance());
        binding.configReaderFontRow.setOnClickListener(v -> openReadingAppearance());
        binding.configReaderSizeRow.setOnClickListener(v -> openReadingAppearance());

        // No guardamos estado de switches en la jerarquía: las preferencias son
        // la fuente de verdad tras reinicios, cambios del tema o restauraciones.
        refreshReadingControls();
        binding.switchJustify.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setJustifyEnabled(requireContext(), checked));
        binding.switchGlossary.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setGlossaryEnabled(requireContext(), checked));
        binding.switchFocus.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setFocusDefaultEnabled(requireContext(), checked));

        binding.buttonReadingReset.setOnClickListener(v -> {
            ReadingPreferenceManager.resetReadingPreferences(requireContext());
            configuracionViewModel.refreshModoLectura();
            refreshReadingControls();
            refreshSwipeNavigationSwitch();
        });
        getParentFragmentManager().setFragmentResultListener(
                AppearanceBottomSheet.RESULT_KEY, this, (key, result) -> {
                    if (binding != null) {
                        configuracionViewModel.refreshModoLectura();
                        refreshReadingControls();
                    }
                });
    }

    private void openReadingAppearance() {
        if (getParentFragmentManager().isStateSaved()
                || getParentFragmentManager().findFragmentByTag("readerAppearance") != null) {
            return;
        }
        new AppearanceBottomSheet().show(getParentFragmentManager(), "readerAppearance");
    }

    private void refreshReadingControls() {
        if (binding == null) return;
        binding.switchJustify.setOnCheckedChangeListener(null);
        binding.switchGlossary.setOnCheckedChangeListener(null);
        binding.switchFocus.setOnCheckedChangeListener(null);
        binding.switchJustify.setChecked(
                ReadingPreferenceManager.isJustifyEnabled(requireContext()));
        binding.switchGlossary.setChecked(
                ReadingPreferenceManager.isGlossaryEnabled(requireContext()));
        binding.switchFocus.setChecked(
                ReadingPreferenceManager.isFocusDefaultEnabled(requireContext()));
        binding.switchJustify.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setJustifyEnabled(requireContext(), checked));
        binding.switchGlossary.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setGlossaryEnabled(requireContext(), checked));
        binding.switchFocus.setOnCheckedChangeListener((button, checked) ->
                ReadingPreferenceManager.setFocusDefaultEnabled(requireContext(), checked));

        int theme;
        switch (ReadingPreferenceManager.getReaderTheme(requireContext())) {
            case LIGHT: theme = R.string.reader_theme_light; break;
            case SEPIA: theme = R.string.reader_theme_sepia; break;
            case DARK: theme = R.string.reader_theme_dark; break;
            case AUTO:
            default: theme = R.string.reader_theme_auto;
        }
        binding.readerThemeValue.setText(theme);

        int font;
        switch (ReadingPreferenceManager.getReaderFont(requireContext())) {
            case SERIF: font = R.string.reader_font_serif; break;
            case ACCESSIBLE: font = R.string.reader_font_accessible; break;
            case SANS:
            default: font = R.string.reader_font_sans;
        }
        binding.readerFontValue.setText(font);
        binding.readerSizeValue.setText(getString(R.string.reader_appearance_size_value,
                Math.round(ReadingPreferenceManager.getContentFontScale(
                        requireContext()) * 100f)));
        binding.configReaderThemeRow.setContentDescription(getString(
                R.string.reader_a11y_reader_theme_row, binding.readerThemeValue.getText()));
        binding.configReaderFontRow.setContentDescription(getString(
                R.string.reader_a11y_reader_font_row, binding.readerFontValue.getText()));
        binding.configReaderSizeRow.setContentDescription(getString(
                R.string.reader_a11y_reader_size_row, binding.readerSizeValue.getText()));
    }

    private void openEmailSupport() {
        Intent emailIntent = SupportEmailIntentFactory.create(requireContext());
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException noEmailClient) {
            // No requiere instalar una aplicación ni enviar el mensaje automáticamente.
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.support_no_email_title)
                    .setMessage(getString(R.string.support_no_email_message,
                            getString(R.string.support_email_address)))
                    .setNegativeButton(R.string.highlight_cancel, null)
                    .setPositiveButton(R.string.support_copy_email, (dialog, which) -> {
                        ClipboardManager clipboard = (ClipboardManager) requireContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(ClipData.newPlainText(
                                    getString(R.string.support_clipboard_label),
                                    getString(R.string.support_email_address)));
                            Toast.makeText(requireContext(), R.string.support_email_copied,
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    private void setupSwitchObserver(SwitchMaterial switchMaterial,
                                     LiveData<Boolean> liveData,
                                     SwitchUpdater updater) {
        liveData.observe(getViewLifecycleOwner(), enabled -> {
            switchMaterial.setOnCheckedChangeListener(null);
            switchMaterial.setChecked(Boolean.TRUE.equals(enabled));
            switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> updater.update(isChecked));
        });
    }


    private void setupSwipeNavigationSwitch() {
        refreshSwipeNavigationSwitch();
        binding.switchSwipeNavigation.setOnCheckedChangeListener((button, enabled) ->
                ReadingPreferenceManager.setSwipeNavigationEnabled(requireContext(), enabled));
    }

    private void refreshSwipeNavigationSwitch() {
        if (binding == null) return;
        binding.switchSwipeNavigation.setOnCheckedChangeListener(null);
        binding.switchSwipeNavigation.setChecked(
                ReadingPreferenceManager.isSwipeNavigationEnabled(requireContext()));
        binding.switchSwipeNavigation.setOnCheckedChangeListener((button, enabled) ->
                ReadingPreferenceManager.setSwipeNavigationEnabled(requireContext(), enabled));
    }

    private void setupAppThemeSelector() {
        refreshAppThemeSelector();
    }

    private void refreshAppThemeSelector() {
        if (binding == null) return;

        // La preferencia persistida es la única fuente de verdad después de recreaciones
        // por DayNight. No se permite restaurar un estado anterior de las vistas.
        binding.appThemeToggle.clearOnButtonCheckedListeners();
        AppThemeMode mode = ThemePreferenceManager.getAppThemeMode(requireContext());
        int checkedId;
        switch (mode) {
            case DARK:
                checkedId = R.id.themeDark;
                break;
            case LIGHT:
                checkedId = R.id.themeLight;
                break;
            case SYSTEM:
            default:
                checkedId = R.id.themeSystem;
        }
        binding.appThemeToggle.check(checkedId);
        binding.appThemeToggle.addOnButtonCheckedListener((group, buttonId, isChecked) -> {
            if (!isChecked) return;
            AppThemeMode selected = buttonId == R.id.themeDark ? AppThemeMode.DARK
                    : buttonId == R.id.themeLight ? AppThemeMode.LIGHT
                    : AppThemeMode.SYSTEM;
            if (selected != ThemePreferenceManager.getAppThemeMode(requireContext())) {
                ThemePreferenceManager.setAppThemeMode(requireContext(), selected);
                ThemePreferenceManager.applySavedTheme(requireContext());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;
        configuracionViewModel.refreshModoLectura();
        refreshAppThemeSelector();
        refreshSwipeNavigationSwitch();
        refreshReadingControls();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private interface SwitchUpdater {
        void update(boolean isChecked);
    }

    private String obtenerVersion() {
        try {
            return requireContext()
                    .getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            return getString(R.string.config_version_default);
        }
    }
}
