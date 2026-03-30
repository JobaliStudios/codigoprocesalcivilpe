package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.Locale;

public class QuizzCPCResult extends AppCompatActivity {
    private int score;
    private int total;
    private long elapsedMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcresult);

        score = getIntent().getIntExtra("SCORE", 0);
        total = Math.max(getIntent().getIntExtra("TOTAL", 0), 1);
        elapsedMillis = getIntent().getLongExtra("ELAPSED_MILLIS", 0L);

        int wrongAnswers = Math.max(total - score, 0);
        int percentage = Math.round((score * 100f) / total);

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvResultTitle = findViewById(R.id.tvResultTitle);
        TextView tvCorrectCount = findViewById(R.id.tvCorrectCount);
        TextView tvWrongCount = findViewById(R.id.tvWrongCount);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvRecommendation = findViewById(R.id.tvRecommendation);
        TextView tvQuickReview = findViewById(R.id.tvQuickReview);
        CircularProgressIndicator circularScore = findViewById(R.id.circularScore);

        tvResult.setText(getString(R.string.quiz_cpc_percentage_format, percentage));
        tvResultTitle.setText(getTitleByPerformance(percentage));
        tvCorrectCount.setText(String.valueOf(score));
        tvWrongCount.setText(String.valueOf(wrongAnswers));
        tvTime.setText(formatElapsedTime(elapsedMillis));
        tvRecommendation.setText(getRecommendation(percentage));
        tvQuickReview.setText(getString(R.string.quiz_cpc_quick_review_dynamic, wrongAnswers, score, total));

        circularScore.setMax(100);
        circularScore.setProgress(percentage);

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            startActivity(new Intent(this, QuizzCPCStartScreen.class));
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
