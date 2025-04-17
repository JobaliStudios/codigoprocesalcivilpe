package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.MainActivity;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class CodigosMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigos_main); // Asegúrate que sea el layout correcto

        // Configurar Cards
        MaterialCardView cardProcesal = findViewById(R.id.card_codigo_procesal);
        cardProcesal.setOnClickListener(v -> {
            startActivity(new Intent(this, CodigoProcesalCivilMain.class));
        });

        // Configurar otros Cards
        MaterialCardView cardCivil = findViewById(R.id.card_codigo_civil);
        cardCivil.setOnClickListener(v -> Toast.makeText(this, "Código Civil próximamente", Toast.LENGTH_SHORT).show());

        MaterialCardView cardPenal = findViewById(R.id.card_codigo_penal);
        cardPenal.setOnClickListener(v -> Toast.makeText(this, "Código Penal próximamente", Toast.LENGTH_SHORT).show());

        MaterialCardView cardProcesalPenal = findViewById(R.id.card_codigo_procesal_penal);
        cardProcesalPenal.setOnClickListener(v -> Toast.makeText(this, "Código Procesal Penal próximamente", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}