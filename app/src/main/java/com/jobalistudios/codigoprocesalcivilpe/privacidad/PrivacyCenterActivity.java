package com.jobalistudios.codigoprocesalcivilpe.privacidad;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.GoogleMobileAdsConsentManager;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ActivityPrivacyCenterBinding;

public class PrivacyCenterActivity extends AppCompatActivity {

    private ActivityPrivacyCenterBinding binding;
    private LocalDataManager localDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        localDataManager = new LocalDataManager(this);

        binding.privacyToolbar.setNavigationOnClickListener(v -> finish());
        setupConsentOptions();
        setupDeletionActions();
    }

    private void setupConsentOptions() {
        GoogleMobileAdsConsentManager consentManager =
                GoogleMobileAdsConsentManager.getInstance(this);
        boolean optionsRequired = consentManager.isPrivacyOptionsRequired();
        binding.buttonManageAdsConsent.setVisibility(optionsRequired ? View.VISIBLE : View.GONE);
        binding.textConsentAvailability.setText(optionsRequired
                ? R.string.privacy_consent_available
                : R.string.privacy_consent_not_required);
        binding.buttonManageAdsConsent.setOnClickListener(v ->
                consentManager.showPrivacyOptionsForm(this, formError -> {
                    if (formError != null) {
                        Toast.makeText(this, R.string.config_privacy_error,
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void setupDeletionActions() {
        binding.buttonClearSearches.setOnClickListener(v -> confirmDeletion(
                R.string.privacy_clear_searches,
                R.string.privacy_clear_searches_confirm,
                R.string.privacy_clear_searches_done,
                localDataManager::clearRecentSearches));

        binding.buttonClearFavorites.setOnClickListener(v -> confirmDeletion(
                R.string.privacy_clear_favorites,
                R.string.privacy_clear_favorites_confirm,
                R.string.privacy_clear_favorites_done,
                localDataManager::clearFavorites));

        binding.buttonClearHighlights.setOnClickListener(v -> confirmDeletion(
                R.string.config_clear_highlights_title,
                R.string.config_clear_highlights_confirm_message,
                R.string.config_clear_highlights_done,
                localDataManager::clearHighlightsAndNotes));

        binding.buttonClearAll.setOnClickListener(v -> confirmDeletion(
                R.string.privacy_clear_all,
                R.string.privacy_clear_all_confirm,
                R.string.privacy_clear_all_done,
                localDataManager::clearAllLocalData));
    }

    private void confirmDeletion(@StringRes int title, @StringRes int message,
                                 @StringRes int successMessage, Runnable action) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.highlight_cancel, null)
                .setPositiveButton(R.string.privacy_delete_action, (dialog, which) -> {
                    action.run();
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
