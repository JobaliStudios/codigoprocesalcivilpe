package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.os.Bundle;


import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialAdCoordinator;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;

public class CodigoProcesalCivilMain extends AppBaseActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codigo_procesal_civil_main);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer), "ca-app-pub-6018202881039088/4642452541");

        findViewById(R.id.section1).setOnClickListener(v -> openHierarchy("sec_1"));
        findViewById(R.id.section2).setOnClickListener(v -> openHierarchy("sec_2"));
        findViewById(R.id.section3).setOnClickListener(v -> openHierarchy("sec_3"));
        findViewById(R.id.section4).setOnClickListener(v -> openHierarchy("sec_4"));
        findViewById(R.id.section5).setOnClickListener(v -> openHierarchy("sec_5"));
        findViewById(R.id.section6).setOnClickListener(v -> openHierarchy("sec_6"));

    }

    private void openHierarchy(String nodeId) {
        LegalHierarchyRepository.Node node = LegalHierarchyRepository.findNodeById(nodeId);
        if (node == null) {
            return;
        }
        InterstitialAdCoordinator.getInstance().navigate(
                this,
                getString(R.string.admob_interstitial_ad_unit_id),
                () -> startActivity(LegalHierarchyRepository.buildIntentForNode(this, node))
        );
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
