package com.jobalistudios.codigoprocesalcivilpe.SeccionCuarta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionCuarta extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccioncuarta);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer12), "ca-app-pub-6018202881039088/4510160454");

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

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}