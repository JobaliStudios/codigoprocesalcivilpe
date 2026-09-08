package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuizzCPCResult extends AppBaseActivity {
    private int score;
    private int total;
    private long elapsedMillis;
    private QuizSessionConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcresult);

        score = getIntent().getIntExtra(QuizSessionContract.EXTRA_SCORE, 0);
        total = Math.max(getIntent().getIntExtra(QuizSessionContract.EXTRA_TOTAL, 0), 1);
        elapsedMillis = getIntent().getLongExtra(
                QuizSessionContract.EXTRA_ELAPSED_MILLIS,
                0L
        );
        ArrayList<QuizAnswerResult> incorrectAnswers =
                QuizSessionContract.getIncorrectAnswers(getIntent());
        QuizSessionStats stats = QuizSessionContract.getStats(getIntent());
        config = QuizSessionContract.getConfig(getIntent());

        if (stats != null) {
            score = stats.getCorrect();
            total = Math.max(stats.getTotal(), 1);
        }

        int wrongAnswers = Math.max(total - score, 0);
        int percentage = Math.round((score * 100f) / total);

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvResultTitle = findViewById(R.id.tvResultTitle);
        TextView tvCorrectCount = findViewById(R.id.tvCorrectCount);
        TextView tvWrongCount = findViewById(R.id.tvWrongCount);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvRecommendation = findViewById(R.id.tvRecommendation);
        CircularProgressIndicator circularScore = findViewById(R.id.circularScore);

        tvResult.setText(getString(R.string.quiz_cpc_percentage_format, percentage));
        tvResultTitle.setText(getTitleByPerformance(percentage));
        tvCorrectCount.setText(String.valueOf(score));
        tvWrongCount.setText(String.valueOf(wrongAnswers));
        tvTime.setText(stats == null ? formatElapsedTime(elapsedMillis)
                : getString(R.string.quiz_points_format, stats.getScore()));
        tvRecommendation.setText(stats == null ? getRecommendation(percentage)
                : getRecommendation(percentage) + "\n" + getString(R.string.quiz_streak_format, stats.getBestStreak()));
        configureReviewAction(incorrectAnswers);

        circularScore.setMax(100);
        circularScore.setProgress(percentage);

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            startActivity(QuizSessionContract.createQuestionIntent(this, config.forAnotherRound()));
            finish();
        });

        findViewById(R.id.btnBackToStart).setOnClickListener(v -> {
            startActivity(new Intent(this, QuizzCPCStartScreen.class));
            finish();
        });

        findViewById(R.id.btnShareScore).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.quiz_cpc_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.quiz_cpc_share_text, score, total));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.quiz_cpc_share_chooser_title)));
        });
    }

    private void configureReviewAction(ArrayList<QuizAnswerResult> incorrectAnswers) {
        TextView title = findViewById(R.id.tvQuickReviewTitle);
        TextView summary = findViewById(R.id.tvQuickReview);
        ImageView statusIcon = findViewById(R.id.ivQuickReviewStatus);
        ImageView chevron = findViewById(R.id.ivQuickReviewChevron);
        MaterialCardView card = findViewById(R.id.cardQuickReview);

        if (!incorrectAnswers.isEmpty()) {
            int errorCount = incorrectAnswers.size();
            title.setText(R.string.quiz_cpc_review_errors);
            summary.setText(getResources().getQuantityString(
                    R.plurals.quiz_cpc_review_error_count,
                    errorCount,
                    errorCount
            ));
            statusIcon.setImageResource(R.drawable.baseline_close_24);
            statusIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.quiz_wrong)));
            chevron.setVisibility(View.VISIBLE);
            card.setClickable(true);
            card.setFocusable(true);
            card.setContentDescription(getString(
                    R.string.quiz_cpc_review_card_description,
                    errorCount
            ));
            card.setOnClickListener(view -> startActivity(QuizSessionContract.createQuestionIntent(
                    this, QuizSessionConfig.previousErrors().withSessionErrorIds(sessionErrorIds(incorrectAnswers))
            )));
            return;
        }

        card.setOnClickListener(null);
        card.setClickable(false);
        card.setFocusable(false);
        card.setContentDescription(null);
        chevron.setVisibility(View.GONE);

        if (score >= total) {
            title.setText(R.string.quiz_cpc_perfect_title);
            summary.setText(R.string.quiz_cpc_perfect_text);
            statusIcon.setImageResource(R.drawable.baseline_check_24);
            statusIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.quiz_correct)));
        } else {
            title.setText(R.string.quiz_cpc_review_errors);
            summary.setText(R.string.quiz_cpc_review_unavailable);
        }
    }

    private List<String> sessionErrorIds(List<QuizAnswerResult> answers) {
        List<String> ids = new ArrayList<>();
        for (QuizAnswerResult answer : answers) {
            for (QuestionModel question : QuestionBank.getQuestions(this)) {
                if (answer.getQuestionText().equals(question.getQuestionText())) {
                    ids.add(QuizQuestionIdentity.forQuestion(question));
                    break;
                }
            }
        }
        return ids;
    }

    private String formatElapsedTime(long elapsedMs) {
        long safeElapsed = Math.max(elapsedMs, 0L) / 1000L;
        long minutes = safeElapsed / 60L;
        long seconds = safeElapsed % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private String getTitleByPerformance(int percentage) {
        if (percentage >= 80) {
            return getString(R.string.quiz_cpc_result_title_excellent);
        }
        if (percentage >= 60) {
            return getString(R.string.quiz_cpc_result_title_good);
        }
        return getString(R.string.quiz_cpc_result_title_keep_practicing);
    }

    private String getRecommendation(int percentage) {
        if (percentage >= 80) {
            return getString(R.string.quiz_cpc_result_recommendation_high);
        }
        if (percentage >= 60) {
            return getString(R.string.quiz_cpc_result_recommendation_mid);
        }
        return getString(R.string.quiz_cpc_result_recommendation_low);
    }
}
