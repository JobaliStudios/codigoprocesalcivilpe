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
import com.jobalistudios.codigoprocesalcivilpe.R;

public class SeccionSextaTit2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_seccion_sexta_tit2);

        MobileAds.initialize(this, initializationStatus -> {});

        // Inicializacion de Ads en background
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Configuración del AdView
        FrameLayout adContainer = findViewById(R.id.adContainer27);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/4320859702");
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
        CardView section8 = findViewById(R.id.section8);
        CardView section9 = findViewById(R.id.section9);
        CardView section10 = findViewById(R.id.section10);
        CardView section11 = findViewById(R.id.section11);
        CardView section12 = findViewById(R.id.section12);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap2.class)));

        section3.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap3.class)));

        section4.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap4.class)));

        section5.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap5.class)));

        section6.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap6.class)));

        section7.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap7.class)));

        section8.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap8.class)));

        section9.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap9.class)));

        section10.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap10.class)));

        section11.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap11.class)));

        section12.setOnClickListener(v -> startActivity(new Intent(SeccionSextaTit2.this, SeccionSextaTit2Subcap12.class)));
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}