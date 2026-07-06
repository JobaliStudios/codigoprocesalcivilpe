package com.jobalistudios.codigoprocesalcivilpe.SeccionTercera;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionTerceraTit1 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_tercera_tit1);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer8), "ca-app-pub-6018202881039088/7255781285");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit1.this, SeccionTerceraTit1Cap1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit1.this, SeccionTerceraTit1Cap2.class)));

    }
}