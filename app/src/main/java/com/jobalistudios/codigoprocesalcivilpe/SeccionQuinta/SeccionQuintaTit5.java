package com.jobalistudios.codigoprocesalcivilpe.SeccionQuinta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.BannerAdHelper;

public class SeccionQuintaTit5 extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_quinta_tit5);

        // Configuración del AdView
        adView = BannerAdHelper.loadBanner(this, findViewById(R.id.adContainer23), "ca-app-pub-6018202881039088/5661560214");

        CardView section1 = findViewById(R.id.section1);
        CardView section2 = findViewById(R.id.section2);
        CardView section3 = findViewById(R.id.section3);
        CardView section4 = findViewById(R.id.section4);
        CardView section5 = findViewById(R.id.section5);

        section1.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5.this,SeccionQuintaTit5Cap1.class)));
        section2.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5.this,SeccionQuintaTit5Cap2.class)));
        section3.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5.this,SeccionQuintaTit5Cap3.class)));
        section4.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5.this,SeccionQuintaTit5Cap4.class)));
        section5.setOnClickListener(v -> startActivity(new Intent(SeccionQuintaTit5.this,SeccionQuintaTit5Cap5.class)));

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
