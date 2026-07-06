package com.jobalistudios.codigoprocesalcivilpe.SeccionQuinta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionQuintaTit5Cap5 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_quinta_tit5_cap5);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer25), "ca-app-pub-6018202881039088/8852831774");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);
        CardView section4 = findViewById(R.id.section4);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5Cap5.this,SeccionQuintaTit5Cap5Subcap1.class)));
        section2.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5Cap5.this,SeccionQuintaTit5Cap5Subcap2.class)));
        section3.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5Cap5.this,SeccionQuintaTit5Cap5Subcap3.class)));
        section4.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5Cap5.this,SeccionQuintaTit5Cap5Subcap4.class)));

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}