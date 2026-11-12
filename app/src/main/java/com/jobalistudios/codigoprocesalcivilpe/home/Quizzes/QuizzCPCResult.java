package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.widget.Toast;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
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
    private static final String STATE_REVIEW_VISIBLE = "review_invite_visible";
    private boolean reviewVisible;

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
        configureDetailedReviewAction(incorrectAnswers);
        configureFocusSectionAction(incorrectAnswers);
        configureOptionalReviewAction(savedInstanceState, stats != null);

        circularScore.setMax(100);
        circularScore.setProgress(percentage);

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            startActivity(QuizSessionContract.createQuestionIntent(this, config.forAnotherRound()));
            finish();
        });

        findViewById(R.id.btnBackToStart).setOnClickListener(v ->
                com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialAdCoordinator
                .getInstance().navigateAtNaturalBreak(this,
                        getString(R.string.admob_interstitial_ad_unit_id),
                        com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialPlacement.QUIZ_RESULT_EXIT,
                        () -> {
            startActivity(new Intent(this, QuizzCPCStartScreen.class));
            finish();
        }));

        findViewById(R.id.btnShareScore).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.quiz_cpc_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.quiz_cpc_share_text, score, total));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.quiz_cpc_share_chooser_title)));
        });
    }

    private void configureOptionalReviewAction(Bundle savedState, boolean completedSession) {
        MaterialCardView card = findViewById(R.id.cardQuizRatingInvite);
        reviewVisible = savedState != null
                ? savedState.getBoolean(STATE_REVIEW_VISIBLE, false)
                : completedSession && new QuizReviewPromptPolicy(this)
                        .claimIfEligible(System.currentTimeMillis());
        card.setVisibility(reviewVisible ? View.VISIBLE : View.GONE);
        if (!reviewVisible) {
            return;
        }
        findViewById(R.id.btnQuizRatePlay).setOnClickListener(v -> {
            hideReviewInvite();
            openPlayListing();
        });
        findViewById(R.id.btnQuizRateLater).setOnClickListener(v -> hideReviewInvite());
        findViewById(R.id.btnQuizRateNever).setOnClickListener(v -> {
            new QuizReviewPromptPolicy(this).neverAskAgain();
            hideReviewInvite();
        });
    }

    private void hideReviewInvite() {
        reviewVisible = false;
        findViewById(R.id.cardQuizRatingInvite).setVisibility(View.GONE);
    }

    private void openPlayListing() {
        String packageName = getPackageName();
        Intent market = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + packageName));
        market.setPackage("com.android.vending");
        try {
            startActivity(market);
        } catch (ActivityNotFoundException missingStore) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + packageName)));
            } catch (ActivityNotFoundException missingBrowser) {
                Toast.makeText(this, R.string.quiz_review_no_store, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(STATE_REVIEW_VISIBLE, reviewVisible);
    }

    private void configureFocusSectionAction(ArrayList<QuizAnswerResult> incorrectAnswers) {
        MaterialCardView card = findViewById(R.id.cardFocusSection);
        MaterialButton practice = findViewById(R.id.btnPracticeFocusSection);
        QuizSectionFocusAdvisor.Focus focus =
                QuizSectionFocusAdvisor.recommend(this, incorrectAnswers);
        if (focus == null) {
            card.setVisibility(View.GONE);
            practice.setOnClickListener(null);
            return;
        }

        QuizSection section = focus.getSection();
        int count = focus.getIncorrectCount();
        TextView summary = findViewById(R.id.tvFocusSectionSummary);
        summary.setText(getResources().getQuantityString(
                R.plurals.quiz_focus_section_summary, count, count, section.getLabel()));
        practice.setContentDescription(getString(
                R.string.quiz_focus_section_action_accessibility, section.getLabel()));
        practice.setOnClickListener(v -> startActivity(
                QuizSessionContract.createQuestionIntent(this,
                        QuizSessionConfig.section(section.getId()))));
        card.setVisibility(View.VISIBLE);
    }

    private void configureDetailedReviewAction(ArrayList<QuizAnswerResult> incorrectAnswers) {
        MaterialButton button = findViewById(R.id.btnReviewDetails);
        if (incorrectAnswers.isEmpty()) {
            button.setVisibility(View.GONE);
            button.setOnClickListener(null);
            return;
        }
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(v -> startActivity(QuizSessionContract.createReviewIntent(
                this, score, total, elapsedMillis, incorrectAnswers)));
    }

    private void configureReviewAction(ArrayList<QuizAnswerResult> incorrectAnswers) {
        TextView title = findViewById(R.id.tvQuickReviewTitle);
        TextView summary = findViewById(R.id.tvQuickReview);
        ImageView statusIcon = findViewById(R.id.ivQuickReviewStatus);
        ImageView chevron = findViewById(R.id.ivQuickReviewChevron);
        MaterialCardView card = findViewById(R.id.cardQuickReview);

        if (!incorrectAnswers.isEmpty()) {
            int errorCount = incorrectAnswers.size();
            title.setText(R.string.quiz_cpc_review_practice_title);
            summary.setText(getResources().getQuantityString(
                    R.plurals.quiz_cpc_review_practice_error_count,
                    errorCount,
                    errorCount
            ));
            statusIcon.setImageResource(R.drawable.baseline_close_24);
            statusIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.quiz_wrong)));
            chevron.setVisibility(View.VISIBLE);
            card.setClickable(true);
            card.setFocusable(true);
            card.setContentDescription(getString(
                    R.string.quiz_cpc_review_practice_card_description,
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
