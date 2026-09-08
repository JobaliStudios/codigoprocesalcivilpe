package com.jobalistudios.codigoprocesalcivilpe.home.Codigos;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


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

        MaterialCardView cardCivil = findViewById(R.id.card_codigo_civil);
        MaterialCardView cardPenal = findViewById(R.id.card_codigo_penal);
        MaterialCardView cardProcesalPenal = findViewById(R.id.card_codigo_procesal_penal);

        disableUnavailableCard(cardCivil);
        disableUnavailableCard(cardPenal);
        disableUnavailableCard(cardProcesalPenal);

        MaterialCardView proximamenteHeader = findViewById(R.id.card_proximamente_header);
        LinearLayout proximamenteContent = findViewById(R.id.layout_proximamente_content);
        ImageView proximamenteChevron = findViewById(R.id.iv_proximamente_chevron);

        proximamenteHeader.setOnClickListener(v -> {
            boolean isCollapsed = proximamenteContent.getVisibility() == View.GONE;
            proximamenteContent.setVisibility(isCollapsed ? View.VISIBLE : View.GONE);
            proximamenteChevron.animate().rotation(isCollapsed ? 90f : 0f).setDuration(150).start();
        });
    }

    private void disableUnavailableCard(MaterialCardView card) {
        card.setClickable(false);
        card.setFocusable(false);
        card.setEnabled(false);
        card.setAlpha(0.55f);
    }

}
