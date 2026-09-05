package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuizzCPCQuestions extends AppCompatActivity {
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuizzCPCViewModel viewModel;

    private LinearProgressIndicator progressBar;
    private TextView tvQuestion;
    private TextView tvProgressText;
    private TextView tvPercentage;
    private TextView tvArticle;
    private TextView tvAnswerFeedback;
    private TextView tvQuizStreak;
    private TextView tvQuizPoints;
    private MaterialCardView cardFeedback;
    private MaterialButton btnNext;
    private LinearLayout optionsContainer;

    private static final String STATE_QUESTION_COUNT = "STATE_QUESTION_COUNT";
    private static final String STATE_START_TIME = "STATE_START_TIME";

    private final List<MaterialCardView> optionCards = new ArrayList<>();
    private boolean answered;
    private long startTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcquestions);

        viewModel = new ViewModelProvider(this).get(QuizzCPCViewModel.class);

        progressBar = findViewById(R.id.progressBar);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressText = findViewById(R.id.tvProgressText);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvArticle = findViewById(R.id.tvArticle);
        tvAnswerFeedback = findViewById(R.id.tvAnswerFeedback);
        tvQuizStreak = findViewById(R.id.tvQuizStreak);
        tvQuizPoints = findViewById(R.id.tvQuizPoints);
        cardFeedback = findViewById(R.id.cardFeedback);
        btnNext = findViewById(R.id.btnNext);
        optionsContainer = findViewById(R.id.optionsContainer);

        startTimeMillis = savedInstanceState != null
                ? savedInstanceState.getLong(STATE_START_TIME, System.currentTimeMillis())
                : System.currentTimeMillis();

        QuizSessionConfig config = QuizSessionContract.getConfig(getIntent());
        if (getIntent().hasExtra("QUESTION_COUNT")) {
            config = new QuizSessionConfig(QuizPracticeMode.ALL, null,
                    getIntent().getIntExtra("QUESTION_COUNT", 10), true, new ArrayList<>());
        }
        QuizSelection selection = QuizSelectorFactory.create(this).select(config);
        if (!selection.isAvailable()) {
            finish();
            return;
        }
        viewModel.initSession(config, selection.getQuestions());

        progressBar.setMax(viewModel.getTotalQuestions());

        viewModel.getCurrentQuestionLiveData().observe(this, this::renderQuestion);

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (!answered) {
                return;
            }
            if (!viewModel.moveToNextQuestion()) {
                showResult();
            }
        });
        btnNext.setEnabled(false);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void renderQuestion(QuestionModel currentQuestion) {
        if (currentQuestion == null) {
            return;
        }

        answered = false;
        btnNext.setEnabled(false);
        cardFeedback.setVisibility(View.GONE);

        tvQuestion.setText(currentQuestion.getQuestionText());
        tvArticle.setText(formatArticle(currentQuestion.getRelatedArticle()));

        int currentQuestionNumber = viewModel.getCurrentQuestionIndex() + 1;
        int totalQuestions = viewModel.getTotalQuestions();
        progressBar.setProgress(currentQuestionNumber);
        tvProgressText.setText(getString(R.string.quiz_cpc_progress_text, currentQuestionNumber, totalQuestions));
        int percentage = Math.round((currentQuestionNumber * 100f) / totalQuestions);
        tvPercentage.setText(getString(R.string.quiz_cpc_percentage_format, percentage));
        renderStats();

        optionsContainer.removeAllViews();
        optionCards.clear();
        for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
            optionsContainer.addView(buildOptionCard(currentQuestion, i));
        }

        QuizAnswerResult savedAnswer = viewModel.getCurrentAnswer();
        if (savedAnswer != null) {
            answered = true;
            showSelectedAnswer(
                    currentQuestion,
                    savedAnswer.getSelectedAnswerIndex(),
                    savedAnswer.isCorrect()
            );
        }
    }

    private MaterialCardView buildOptionCard(QuestionModel question, int optionIndex) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.topMargin = dimensionPixelSize(R.dimen.space_m);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(getColor(R.color.quiz_option_default_bg));
        card.setStrokeColor(getColor(R.color.quiz_option_default_stroke));
        card.setStrokeWidth(dimensionPixelSize(R.dimen.stroke_width_thin));
        card.setRadius(dimensionPixelSize(R.dimen.radius_l));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int cardPadding = dimensionPixelSize(R.dimen.space_l);
        row.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);

        ImageView radio = new ImageView(this);
        radio.setImageResource(R.drawable.quiz_radio_unselected);
        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(dp(32), dp(32));
        radio.setLayoutParams(radioParams);

        TextView optionText = new TextView(this);
        optionText.setText(question.getOptions().get(optionIndex));
        optionText.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_title_m)
        );
        optionText.setTextColor(getColor(R.color.quiz_text_primary));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = dimensionPixelSize(R.dimen.space_m);
        optionText.setLayoutParams(textParams);

        ImageView resultIcon = new ImageView(this);
        LinearLayout.LayoutParams resultParams = new LinearLayout.LayoutParams(dp(30), dp(30));
        resultIcon.setLayoutParams(resultParams);
        resultIcon.setVisibility(View.GONE);

        row.addView(radio);
        row.addView(optionText);
        row.addView(resultIcon);
        card.addView(row);

        card.setOnClickListener(v -> onOptionSelected(question, optionIndex));

        optionCards.add(card);
        card.setTag(R.id.btnNext, radio);
        card.setTag(R.id.tvQuestion, resultIcon);
        return card;
    }

    private void onOptionSelected(QuestionModel question, int selectedIndex) {
        if (answered) {
            return;
        }

        answered = true;
        boolean isCorrect = viewModel.submitAnswer(selectedIndex);
        playAnswerSound(isCorrect);

        showSelectedAnswer(question, selectedIndex, isCorrect);
        renderStats();
    }

    private void showSelectedAnswer(
            QuestionModel question,
            int selectedIndex,
            boolean isCorrect
    ) {

        int correctIndex = question.getCorrectAnswerIndex();
        for (int i = 0; i < optionCards.size(); i++) {
            MaterialCardView card = optionCards.get(i);
            ImageView radio = (ImageView) card.getTag(R.id.btnNext);
            ImageView result = (ImageView) card.getTag(R.id.tvQuestion);

            card.setClickable(false);
            radio.setImageResource(i == selectedIndex ? R.drawable.quiz_radio_selected : R.drawable.quiz_radio_unselected);
            if (i == selectedIndex) {
                card.setStrokeColor(getColor(R.color.quiz_selected_stroke));
                card.setStrokeWidth(dimensionPixelSize(R.dimen.stroke_width_emphasis));
            }

            if (i == selectedIndex && selectedIndex != correctIndex) {
                result.setImageResource(R.drawable.baseline_close_24);
                result.setImageTintList(ColorStateList.valueOf(getColor(R.color.quiz_wrong)));
                result.setVisibility(View.VISIBLE);
            } else if (i == correctIndex) {
                result.setImageResource(R.drawable.baseline_check_24);
                result.setImageTintList(ColorStateList.valueOf(getColor(R.color.quiz_correct)));
                result.setVisibility(View.VISIBLE);
            } else {
                result.setVisibility(View.GONE);
            }
        }

        showAnswerFeedback(isCorrect, question);
        btnNext.setEnabled(true);
    }

    private void showAnswerFeedback(boolean isCorrect, QuestionModel question) {
        cardFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            cardFeedback.setCardBackgroundColor(getColor(R.color.quiz_feedback_success_bg));
            cardFeedback.setStrokeColor(getColor(R.color.quiz_correct));
            tvAnswerFeedback.setTextColor(getColor(R.color.quiz_correct));
            tvAnswerFeedback.setText(getString(R.string.quiz_feedback_correct_points,
                    viewModel.getCurrentAnswerPoints(), viewModel.getStats().getCurrentStreak()));
            return;
        }

        cardFeedback.setCardBackgroundColor(getColor(R.color.quiz_feedback_error_bg));
        cardFeedback.setStrokeColor(getColor(R.color.quiz_wrong));
        tvAnswerFeedback.setTextColor(getColor(R.color.quiz_wrong));
        String correctAnswer = question.getOptions().get(question.getCorrectAnswerIndex());
        tvAnswerFeedback.setText(getString(R.string.quiz_feedback_wrong_answer, correctAnswer));
    }

    private String formatArticle(String relatedArticle) {
        if (relatedArticle == null || relatedArticle.isEmpty()) {
            return getString(R.string.quiz_cpc_article_fallback);
        }
        return relatedArticle.toUpperCase(Locale.getDefault());
    }

    private void showResult() {
        Intent intent = QuizSessionContract.createResultIntent(
                this,
                viewModel.getStats(),
                viewModel.getConfig() == null ? QuizSessionConfig.all() : viewModel.getConfig(),
                System.currentTimeMillis() - startTimeMillis,
                viewModel.getIncorrectAnswers()
        );
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_QUESTION_COUNT, viewModel.getTotalQuestions());
        outState.putLong(STATE_START_TIME, startTimeMillis);
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
        }
        if (wrongSound == null) {
            wrongSound = MediaPlayer.create(this, R.raw.wrong);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer(correctSound);
        releaseMediaPlayer(wrongSound);
        correctSound = null;
        wrongSound = null;
    }

    private void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private void renderStats() {
        tvQuizStreak.setText(getString(R.string.quiz_streak_format, viewModel.getStats().getCurrentStreak()));
        tvQuizPoints.setText(getString(R.string.quiz_points_format, viewModel.getStats().getScore()));
    }

    private int dimensionPixelSize(int resourceId) {
        return getResources().getDimensionPixelSize(resourceId);
    }
}
