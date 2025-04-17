package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jobalistudios.codigoprocesalcivilpe.R;

public class QuizzCPCStartScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcstart_screen);

        setupButton(R.id.btn5Questions, 5);
        setupButton(R.id.btn10Questions, 10);
        setupButton(R.id.btn20Questions, 20);
    }

    private void setupButton(int buttonId, final int questionCount) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizzCPCQuestions.class);
            intent.putExtra("QUESTION_COUNT", questionCount);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, QuizzesMain.class));
        finish();
    }
}