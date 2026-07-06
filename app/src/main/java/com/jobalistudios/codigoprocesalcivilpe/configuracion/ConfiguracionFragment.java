package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.GoogleMobileAdsConsentManager;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentConfiguracionBinding;

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

        setupPrivacyOptions();

        return root;
    }

    /** Punto de entrada a las opciones de privacidad de anuncios (exigido por Google donde aplica). */
    private void setupPrivacyOptions() {
        GoogleMobileAdsConsentManager consentManager =
                GoogleMobileAdsConsentManager.getInstance(requireContext());
        binding.cardPrivacidad.setVisibility(
                consentManager.isPrivacyOptionsRequired() ? View.VISIBLE : View.GONE);
        binding.cardPrivacidad.setOnClickListener(v ->
                consentManager.showPrivacyOptionsForm(requireActivity(), formError -> {
                    if (formError != null) {
                        Toast.makeText(requireContext(),
                                R.string.config_privacy_error, Toast.LENGTH_SHORT).show();
                    }
                }));
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
        boolean darkModeEnabled = ThemePreferenceManager.isDarkModeEnabled(requireContext());
        binding.switchModoOscuro.setChecked(darkModeEnabled);

        binding.switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePreferenceManager.setDarkModeEnabled(requireContext(), isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
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
