package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

public class QuizzCPCQuestions extends AppCompatActivity {
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuizzCPCViewModel viewModel;

    private LinearProgressIndicator progressBar;
    private RadioGroup radioGroup;
    private TextView tvQuestion;
    private TextView tvProgressText;
    private TextView tvAnswerFeedback;
    private Button btnNext;
    private final Handler feedbackHandler = new Handler(Looper.getMainLooper());

    private static final String STATE_QUESTION_COUNT = "STATE_QUESTION_COUNT";
    private static final String STATE_CURRENT_INDEX = "STATE_CURRENT_INDEX";
    private static final String STATE_SCORE = "STATE_SCORE";
    private boolean isNavigatingBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcquestions);

        viewModel = new ViewModelProvider(this).get(QuizzCPCViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        radioGroup = findViewById(R.id.radioGroupOptions);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressText = findViewById(R.id.tvProgressText);
        tvAnswerFeedback = findViewById(R.id.tvAnswerFeedback);
        btnNext = findViewById(R.id.btnNext);

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

        btnNext.setOnClickListener(v -> handleNextQuestion());
        btnNext.setEnabled(false);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToStartScreen();
            }
        });
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
        radioGroup.clearCheck();
        radioGroup.setEnabled(true);
        btnNext.setEnabled(false);
        tvAnswerFeedback.setVisibility(View.GONE);

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTextSize(16);
            radioButton.setPadding(24, 24, 24, 24);
            radioButton.setBackgroundTintList(ColorStateList.valueOf(getColor(android.R.color.transparent)));
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            btnNext.setEnabled(checkedId != -1);
            highlightSelectedOption(group, checkedId);
        });

        int currentQuestionNumber = viewModel.getCurrentQuestionIndex() + 1;
        progressBar.setProgress(currentQuestionNumber);
        tvProgressText.setText(getString(R.string.quiz_cpc_progress_text, currentQuestionNumber, viewModel.getTotalQuestions()));
    }

    private void handleNextQuestion() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return;
        }

        int selectedIndex = radioGroup.indexOfChild(findViewById(selectedId));
        boolean isCorrect = viewModel.submitAnswer(selectedIndex);
        playAnswerSound(isCorrect);
        showAnswerFeedback(isCorrect);

        btnNext.setEnabled(false);
        radioGroup.setEnabled(false);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }

        feedbackHandler.postDelayed(() -> {
            if (!viewModel.moveToNextQuestion()) {
                showResult();
            }
        }, 700);
    }

    private void highlightSelectedOption(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton option = (RadioButton) child;
                boolean isSelected = option.getId() == checkedId;
                option.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
                option.setAlpha(isSelected ? 1f : 0.8f);
            }
        }
    }

    private void showAnswerFeedback(boolean isCorrect) {
        tvAnswerFeedback.setVisibility(View.VISIBLE);
        tvAnswerFeedback.setText(isCorrect ? R.string.quiz_cpc_feedback_correct : R.string.quiz_cpc_feedback_wrong);
        tvAnswerFeedback.setTextColor(getColor(isCorrect ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));
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
        feedbackHandler.removeCallbacksAndMessages(null);
        releaseMediaPlayer(correctSound);
        releaseMediaPlayer(wrongSound);
        correctSound = null;
        wrongSound = null;
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

    private void navigateToStartScreen() {
        if (isNavigatingBack) {
            return;
        }

        isNavigatingBack = true;
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
