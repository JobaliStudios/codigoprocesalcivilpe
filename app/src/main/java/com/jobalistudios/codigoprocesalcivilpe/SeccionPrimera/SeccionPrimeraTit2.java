package com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera;

import android.content.Intent;
import android.os.Build;
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
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

public class SeccionPrimeraTit2 extends AppCompatActivity {

    private AdView adView;

    // Get the ad size with screen width.
    public AdSize getAdSize () {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_primera_tit2);

        MobileAds.initialize(this, initializationStatus -> {});

        // Inicializacion de Ads en background
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        // Configuración del AdView
        FrameLayout adContainer = findViewById(R.id.adContainer3);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-6018202881039088/1899512824");
        adView.setAdSize(getAdSize());
        adContainer.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionPrimeraTit2.this, SeccionPrimeraTit2Cap1.class)));

        section2.setOnClickListener(v -> startActivity(SectionContentActivity.createIntent(
                SeccionPrimeraTit2.this,
                R.layout.activity_section_content,
                R.string.seccionprimeratit2cap2txt,
                R.string.capitulo2,
                R.string.tit2cap2sub
        )
                .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, getString(R.string.seccion_primeratit))
                .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, getString(R.string.titulo2))
                .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, getString(R.string.capitulo2))
                .putExtra(SectionContentActivity.EXTRA_CURRENT_DESTINATION_ID, com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_2_CHAPTER_2)
                .putExtra(SectionContentActivity.EXTRA_PREVIOUS_DESTINATION_ID, com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_2_CHAPTER_1)
                .putExtra(SectionContentActivity.EXTRA_NEXT_DESTINATION_ID, com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_2_CHAPTER_3)));

        section3.setOnClickListener(v -> startActivity(SectionContentActivity.createIntent(
                SeccionPrimeraTit2.this,
                R.layout.activity_section_content,
                R.string.seccionprimeratit2cap3txt,
                R.string.capitulo3,
                R.string.tit2cap3sub
        )
                .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, getString(R.string.seccion_primeratit))
                .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, getString(R.string.titulo2))
                .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, getString(R.string.capitulo3))
                .putExtra(SectionContentActivity.EXTRA_CURRENT_DESTINATION_ID, com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_2_CHAPTER_3)
                .putExtra(SectionContentActivity.EXTRA_PREVIOUS_DESTINATION_ID, com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper.DEST_ART_SECTION_1_TITLE_2_CHAPTER_2)));

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}