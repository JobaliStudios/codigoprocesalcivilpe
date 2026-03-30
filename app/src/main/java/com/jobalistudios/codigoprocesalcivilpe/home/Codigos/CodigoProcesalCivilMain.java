package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionListActivity;


public class CodigoProcesalCivilMain extends AppCompatActivity {

    private AdView adView;

    // Get the ad size with screen width.
    public AdSize getAdSize () {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels;

        WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
        adWidthPixels = windowMetrics.getBounds().width();

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codigo_procesal_civil_main);

        // Inicializacion de Ads en background
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Configuración del AdView
        FrameLayout adContainer = findViewById(R.id.adContainer);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/2334541370");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        findViewById(R.id.section1).setOnClickListener(v -> openHierarchy("sec_1"));
        findViewById(R.id.section2).setOnClickListener(v -> openHierarchy("sec_2"));
        findViewById(R.id.section3).setOnClickListener(v -> openHierarchy("sec_3"));
        findViewById(R.id.section4).setOnClickListener(v -> openHierarchy("sec_4"));
        findViewById(R.id.section5).setOnClickListener(v -> openHierarchy("sec_5"));
        findViewById(R.id.section6).setOnClickListener(v -> openHierarchy("sec_6"));

    }

    private void openHierarchy(String nodeId) {
        startActivity(SectionListActivity.createIntent(this, nodeId));
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
