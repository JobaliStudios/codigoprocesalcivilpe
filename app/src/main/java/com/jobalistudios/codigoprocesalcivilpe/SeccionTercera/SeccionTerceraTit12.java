package com.jobalistudios.codigoprocesalcivilpe.SeccionTercera;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionTerceraTit12 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_tercera_tit12);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer11), "ca-app-pub-6018202881039088/7064209591");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);
        CardView section4 = findViewById(R.id.section4);
        CardView section5 = findViewById(R.id.section5);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit12.this, SeccionTerceraTit12Cap1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit12.this, SeccionTerceraTit12Cap2.class)));

        section3.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit12.this, SeccionTerceraTit12Cap3.class)));

        section4.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit12.this, SeccionTerceraTit12Cap4.class)));

        section5.setOnClickListener(v -> startActivity(new Intent(SeccionTerceraTit12.this, SeccionTerceraTit12Cap5.class)));
    }
}
