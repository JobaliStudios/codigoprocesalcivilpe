package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ActivityAboutBinding;

/** Información verificable del producto y enlaces externos a fuentes oficiales. */
public class AboutActivity extends AppBaseActivity {

    private static final String OFFICIAL_CPC_URL =
            "https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007";
    private static final String SPIJ_URL = "https://spij.minjus.gob.pe/";
    private static final String PRIVACY_URL = "https://jobalistudios.github.io/privacy-policy/";

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.aboutToolbar.setNavigationOnClickListener(v -> finish());
        binding.aboutVersion.setText(getString(R.string.config_version_value,
                installedVersion()));
        binding.buttonElPeruano.setOnClickListener(v -> openUrl(OFFICIAL_CPC_URL));
        binding.buttonSpij.setOnClickListener(v -> openUrl(SPIJ_URL));
        binding.buttonPrivacyPolicy.setOnClickListener(v -> openUrl(PRIVACY_URL));
    }

    @NonNull
    private String installedVersion() {
        try {
            String version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            return version == null ? getString(R.string.config_version_default) : version;
        } catch (Exception e) {
            return getString(R.string.config_version_default);
        }
    }

    private void openUrl(@NonNull String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.about_link_unavailable, Toast.LENGTH_SHORT).show();
        }
    }
}
