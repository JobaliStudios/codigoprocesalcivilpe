package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.List;

public class QuizzCPCQuestions extends AppCompatActivity {
    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions;

    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcquestions);

        int questionCount = getIntent().getIntExtra("QUESTION_COUNT", 10);
        questions = QuestionBank.getRandomQuestions(questionCount);
        totalQuestions = questions.size();

        setupProgressBar();
        loadQuestion();

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> handleNextQuestion());

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);
    }

    private void setupProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(totalQuestions);
    }

    private void loadQuestion() {
        QuestionModel currentQuestion = questions.get(currentQuestionIndex);
        TextView tvQuestion = findViewById(R.id.tvQuestion);
        RadioGroup radioGroup = findViewById(R.id.radioGroupOptions);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        tvQuestion.setText(currentQuestion.getQuestionText());
        radioGroup.removeAllViews();

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        progressBar.setProgress(currentQuestionIndex + 1);
    }

    private void handleNextQuestion() {
        checkAnswer();
        currentQuestionIndex++;

        if (currentQuestionIndex < totalQuestions) {
            loadQuestion();
        } else {
            showResult();
        }
    }

    private void checkAnswer() {
        RadioGroup radioGroup = findViewById(R.id.radioGroupOptions);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        QuestionModel currentQuestion = questions.get(currentQuestionIndex);

        if (selectedId != -1) {
            int selectedIndex = radioGroup.indexOfChild(findViewById(selectedId));
            boolean isCorrect = selectedIndex == currentQuestion.getCorrectAnswerIndex();

            currentQuestion.setCorrect(isCorrect);

            // Reproducir sonido
            if (isCorrect) {
                score++;
                correctSound.start();
            } else {
                wrongSound.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos de audio
        if (correctSound != null) {
            correctSound.release();
        }
        if (wrongSound != null) {
            wrongSound.release();
        }
    }

    private void showResult() {
        Intent intent = new Intent(this, QuizzCPCResult.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", totalQuestions);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, QuizzCPCStartScreen.class));
        finish();
    }
}