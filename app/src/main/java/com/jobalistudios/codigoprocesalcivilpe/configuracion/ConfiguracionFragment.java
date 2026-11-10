package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

        binding.cardSoporte.setOnClickListener(v -> Toast.makeText(requireContext(),
                R.string.config_contact_message, Toast.LENGTH_SHORT).show());

        binding.cardAcercaDe.setOnClickListener(v -> Toast.makeText(requireContext(),
                R.string.config_about_message, Toast.LENGTH_SHORT).show());

        binding.cardPrivacidad.setOnClickListener(v -> startActivity(
                new Intent(requireContext(), PrivacyCenterActivity.class)));

        return root;
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
