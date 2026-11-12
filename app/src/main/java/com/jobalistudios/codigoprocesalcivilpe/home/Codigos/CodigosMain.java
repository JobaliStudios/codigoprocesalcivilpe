package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class CodigosMain extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigos_main); // Asegúrate que sea el layout correcto

        // Configurar Cards
        MaterialCardView cardProcesal = findViewById(R.id.card_codigo_procesal);
        cardProcesal.setOnClickListener(v -> {
            startActivity(new Intent(this, CodigoProcesalCivilMain.class));
        });

    }

}
