package com.jobalistudios.codigoprocesalcivilpe.SeccionCuarta;

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

public class SeccionCuarta extends AppCompatActivity {

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
        setContentView(R.layout.activity_seccioncuarta);

        MobileAds.initialize(this, initializationStatus -> {});

        // Inicializacion de Ads en background
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Configuración del AdView
        FrameLayout adContainer = findViewById(R.id.adContainer12);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/4510160454");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);
        CardView section4 = findViewById(R.id.section4);
        CardView section5 = findViewById(R.id.section5);
        CardView section6 = findViewById(R.id.section6);
        CardView section7 = findViewById(R.id.section7);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit2.class)));

        section3.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit3.class)));

        section4.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit4.class)));

        section5.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit5.class)));

        section6.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit6.class)));

        section7.setOnClickListener(v -> startActivity(new Intent(SeccionCuarta.this, SeccionCuartaTit7.class)));


        SectionCardFilterBinder filterBinder = new SectionCardFilterBinder();
        filterBinder.addEntry(section1, getString(R.string.titulo1), getString(R.string.sec4titulo1sub), getString(R.string.rangartisec4tit1), SectionFilterType.TITULO);
        filterBinder.addEntry(section2, getString(R.string.titulo2), getString(R.string.sec4titulo2sub), getString(R.string.rangartisec4tit2), SectionFilterType.TITULO);
        filterBinder.addEntry(section3, getString(R.string.titulo3), getString(R.string.sec4titulo3sub), getString(R.string.rangartisec4tit3), SectionFilterType.TITULO);
        filterBinder.addEntry(section4, getString(R.string.titulo4), getString(R.string.sec4titulo4sub), getString(R.string.rangartisec4tit4), SectionFilterType.TITULO);
        filterBinder.addEntry(section5, getString(R.string.titulo5), getString(R.string.sec4titulo5sub), getString(R.string.rangartisec4tit5), SectionFilterType.TITULO);
        filterBinder.addEntry(section6, getString(R.string.titulo6), getString(R.string.sec4titulo6sub), getString(R.string.rangartisec4tit6), SectionFilterType.TITULO);
        filterBinder.addEntry(section7, getString(R.string.titulo7), getString(R.string.sec4titulo7sub), getString(R.string.rangartisec4tit7), SectionFilterType.TITULO);
        filterBinder.bind(
                SeccionCuarta.class.getName(),
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