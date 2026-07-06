package com.jobalistudios.codigoprocesalcivilpe.SeccionQuinta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionQuintaTit3 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_quinta_tit3);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer18), "ca-app-pub-6018202881039088/4535077159");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit3.this,SeccionQuintaTit3Cap1.class)));
        section2.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit3.this,SeccionQuintaTit3Cap2.class)));

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
