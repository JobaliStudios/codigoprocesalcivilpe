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
import androidx.lifecycle.ViewModelProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

public class QuizzCPCQuestions extends AppCompatActivity {
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuizzCPCViewModel viewModel;

    private ProgressBar progressBar;
    private RadioGroup radioGroup;
    private TextView tvQuestion;

    private static final String STATE_QUESTION_COUNT = "STATE_QUESTION_COUNT";
    private static final String STATE_CURRENT_INDEX = "STATE_CURRENT_INDEX";
    private static final String STATE_SCORE = "STATE_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcquestions);

        viewModel = new ViewModelProvider(this).get(QuizzCPCViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        radioGroup = findViewById(R.id.radioGroupOptions);
        tvQuestion = findViewById(R.id.tvQuestion);

        int questionCount = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_QUESTION_COUNT, getIntent().getIntExtra("QUESTION_COUNT", 10))
                : getIntent().getIntExtra("QUESTION_COUNT", 10);

        viewModel.initQuestions(questionCount);
        if (savedInstanceState != null) {
            viewModel.restoreState(
                    savedInstanceState.getInt(STATE_CURRENT_INDEX, 0),
                    savedInstanceState.getInt(STATE_SCORE, 0)
            );
        }

        setupProgressBar();

        viewModel.getCurrentQuestionLiveData().observe(this, this::renderQuestion);

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> handleNextQuestion());
    }

    private void setupProgressBar() {
        progressBar.setMax(viewModel.getTotalQuestions());
    }

    private void renderQuestion(QuestionModel currentQuestion) {
        if (currentQuestion == null) {
            return;
        }
        tvQuestion.setText(currentQuestion.getQuestionText());
        radioGroup.removeAllViews();

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        progressBar.setProgress(viewModel.getCurrentQuestionIndex() + 1);
    }

    private void handleNextQuestion() {
        RadioGroup radioGroup = findViewById(R.id.radioGroupOptions);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return;
        }

        int selectedIndex = radioGroup.indexOfChild(findViewById(selectedId));
        boolean isCorrect = viewModel.submitAnswer(selectedIndex);
        playAnswerSound(isCorrect);

        if (!viewModel.moveToNextQuestion()) {
            showResult();
        }
    }

    private void playAnswerSound(boolean isCorrect) {
        MediaPlayer mediaPlayer = isCorrect ? correctSound : wrongSound;

        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (correctSound == null) {
            correctSound = MediaPlayer.create(this, R.raw.correct);
            correctSound.setOnCompletionListener(mp -> mp.seekTo(0));
        }
        if (wrongSound == null) {
            wrongSound = MediaPlayer.create(this, R.raw.wrong);
            wrongSound.setOnCompletionListener(mp -> mp.seekTo(0));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseMediaPlayer(correctSound);
        pauseMediaPlayer(wrongSound);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer(correctSound);
        releaseMediaPlayer(wrongSound);
        correctSound = null;
        wrongSound = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(correctSound);
        releaseMediaPlayer(wrongSound);
    }

    private void showResult() {
        Intent intent = new Intent(this, QuizzCPCResult.class);
        intent.putExtra("SCORE", viewModel.getScore());
        intent.putExtra("TOTAL", viewModel.getTotalQuestions());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_QUESTION_COUNT, viewModel.getTotalQuestions());
        outState.putInt(STATE_CURRENT_INDEX, viewModel.getCurrentQuestionIndex());
        outState.putInt(STATE_SCORE, viewModel.getScore());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, QuizzCPCStartScreen.class));
        finish();
    }

    private void pauseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}