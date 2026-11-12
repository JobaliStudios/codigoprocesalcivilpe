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

        setupDarkModeSwitch();

        binding.cardSoporte.setOnClickListener(v -> openEmailSupport());

        binding.cardAcercaDe.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), AboutActivity.class)));

        binding.cardPrivacidad.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), PrivacyCenterActivity.class)));

        return root;
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


    private void setupDarkModeSwitch() {
        refreshDarkModeSwitch();
    }

    private void refreshDarkModeSwitch() {
        if (binding == null) return;
        // Los interruptores no guardan su estado de vista: la fuente persistente
        // es app_settings. Evita que Android restaure el valor oscuro previo durante
        // la recreación de Activity y dispare el listener que lo vuelve a escribir.
        // Actualizar la vista sin disparar el listener ni guardar de nuevo el valor antiguo.
        binding.switchModoOscuro.setOnCheckedChangeListener(null);
        binding.switchModoOscuro.setChecked(ThemePreferenceManager.isDarkModeEnabled(requireContext()));
        binding.switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePreferenceManager.setDarkModeEnabled(requireContext(), isChecked);
            ThemePreferenceManager.applySavedTheme(requireContext());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;
        configuracionViewModel.refreshModoLectura();
        refreshDarkModeSwitch();
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
