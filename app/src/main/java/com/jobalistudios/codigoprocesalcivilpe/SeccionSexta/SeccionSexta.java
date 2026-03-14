package com.jobalistudios.codigoprocesalcivilpe.SeccionSexta;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionCardFilterBinder;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionFilterType;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class SeccionSexta extends AppCompatActivity {

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
        setContentView(R.layout.activity_seccionsexta);

        MobileAds.initialize(this, initializationStatus -> {});

        // Inicializacion de Ads en background
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Configuración del AdView
        FrameLayout adContainer = findViewById(R.id.adContainer26);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/5661861533");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionSexta.this, SeccionSextaTit1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionSexta.this, SeccionSextaTit2.class)));


        SectionCardFilterBinder filterBinder = new SectionCardFilterBinder();
        filterBinder.addEntry(section1, getString(R.string.titulo1), getString(R.string.sec6titulo1sub), getString(R.string.rangartisec6tit1), SectionFilterType.TITULO);
        filterBinder.addEntry(section2, getString(R.string.titulo2), getString(R.string.sec6titulo2sub), getString(R.string.rangartisec6tit2), SectionFilterType.TITULO);
        filterBinder.bind(
                SeccionSexta.class.getName(),
                findViewById(R.id.sectionSearchView),
                findViewById(R.id.sectionTypeChipGroup),
                findViewById(R.id.sectionFilterEmptyState)
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