package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.R;

public class QuizzCPCStartScreen extends AppCompatActivity {

    private MaterialCardView option5;
    private MaterialCardView option10;
    private MaterialCardView option20;
    private ImageView indicator5;
    private ImageView indicator10;
    private ImageView indicator20;
    private int selectedQuestionCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcstart_screen);

        option5 = findViewById(R.id.option5);
        option10 = findViewById(R.id.option10);
        option20 = findViewById(R.id.option20);
        indicator5 = findViewById(R.id.option5Indicator);
        indicator10 = findViewById(R.id.option10Indicator);
        indicator20 = findViewById(R.id.option20Indicator);
        MaterialButton btnStart = findViewById(R.id.btnStartQuiz);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        option5.setOnClickListener(v -> selectQuestionCount(5));
        option10.setOnClickListener(v -> selectQuestionCount(10));
        option20.setOnClickListener(v -> selectQuestionCount(20));

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizzCPCQuestions.class);
            intent.putExtra("QUESTION_COUNT", selectedQuestionCount);
            startActivity(intent);
        });

        selectQuestionCount(selectedQuestionCount);
    }

    private void selectQuestionCount(int questionCount) {
        selectedQuestionCount = questionCount;

        styleOption(option5, indicator5, questionCount == 5);
        styleOption(option10, indicator10, questionCount == 10);
        styleOption(option20, indicator20, questionCount == 20);
    }

    private void styleOption(MaterialCardView card, ImageView indicator, boolean isSelected) {
        card.setCardBackgroundColor(getColor(isSelected ? R.color.quiz_option_selected_bg : android.R.color.white));
        card.setStrokeColor(getColor(isSelected ? R.color.quiz_option_selected_stroke : R.color.quiz_option_default_stroke));
        indicator.setImageResource(isSelected ? R.drawable.quiz_radio_selected : R.drawable.quiz_radio_unselected);
    }
}
