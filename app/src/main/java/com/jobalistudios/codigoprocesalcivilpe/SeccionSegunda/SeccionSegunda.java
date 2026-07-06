package com.jobalistudios.codigoprocesalcivilpe.SeccionSegunda;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionSegunda extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccionsegunda);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer4), "ca-app-pub-6018202881039088/8712333185");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionSegunda.this, SeccionSegundaTit1.class)));

        section2.setOnClickListener(v -> startActivity(new Intent(SeccionSegunda.this, SeccionSegundaTit2.class)));

        section3.setOnClickListener(v -> startActivity(new Intent(SeccionSegunda.this, SeccionSegundaTit3.class)));

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
