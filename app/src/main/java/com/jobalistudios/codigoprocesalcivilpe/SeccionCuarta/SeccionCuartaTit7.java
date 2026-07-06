package com.jobalistudios.codigoprocesalcivilpe.SeccionCuarta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionCuartaTit7 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_cuarta_tit7);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer13), "ca-app-pub-6018202881039088/4438046258");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionCuartaTit7.this, SeccionCuartaTit7Cap1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionCuartaTit7.this, SeccionCuartaTit7Cap2.class)));
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
