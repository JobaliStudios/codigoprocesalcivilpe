package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jobalistudios.codigoprocesalcivilpe.R;

public class QuizzCPCResult extends AppCompatActivity {
    private int score;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcresult);

        score = getIntent().getIntExtra("SCORE", 0);
        total = getIntent().getIntExtra("TOTAL", 0);

        TextView tvResult = findViewById(R.id.tvResult);
        tvResult.setText(getString(R.string.quiz_cpc_result_score_format, score, total));

        Button btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizzCPCStartScreen.class));
            finish();
        });

        Button btnReviewErrors = findViewById(R.id.btnReviewErrors);
        btnReviewErrors.setOnClickListener(v -> Toast.makeText(this, R.string.quiz_cpc_review_errors_hint, Toast.LENGTH_SHORT).show());

        Button btnBackToStart = findViewById(R.id.btnBackToStart);
        btnBackToStart.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizzCPCStartScreen.class));
            finish();
        });

        Button btnShareScore = findViewById(R.id.btnShareScore);
        btnShareScore.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.quiz_cpc_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.quiz_cpc_share_text, score, total));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.quiz_cpc_share_chooser_title)));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, QuizzesMain.class));
        finish();
    }

}
