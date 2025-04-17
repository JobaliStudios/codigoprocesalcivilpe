package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.MainActivity;
import com.jobalistudios.codigoprocesalcivilpe.databinding.ActivityQuizzesMainBinding;

public class QuizzesMain extends AppCompatActivity {

    private ActivityQuizzesMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizzesMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MaterialCardView cardProcesal = binding.cardCodigoProcesal;
        cardProcesal.setOnClickListener(v -> {
            Intent intent = new Intent(QuizzesMain.this, QuizzCPCStartScreen.class);
            startActivity(intent);
        });


        MaterialCardView cardCivil = binding.cardCodigoCivil;
        cardCivil.setOnClickListener(v -> Toast.makeText(QuizzesMain.this, "Código Civil próximamente", Toast.LENGTH_SHORT).show());


        MaterialCardView cardPenal = binding.cardCodigoPenal;
        cardPenal.setOnClickListener(v -> Toast.makeText(QuizzesMain.this, "Código Penal próximamente", Toast.LENGTH_SHORT).show());


        MaterialCardView cardProcesalPenal = binding.cardCodigoProcesalPenal;
        cardProcesalPenal.setOnClickListener(v -> Toast.makeText(QuizzesMain.this, "Código Procesal Penal próximamente", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}