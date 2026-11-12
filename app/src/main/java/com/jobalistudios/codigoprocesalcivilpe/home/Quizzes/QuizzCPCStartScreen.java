package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.List;

public class QuizzCPCStartScreen extends AppBaseActivity {
    private static final String STATE_QUESTION_COUNT = "quiz_start_question_count";
    private int selectedQuestionCount = 10;
    private TextView status;
    private List<QuestionModel> questions;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_quizz_cpcstart_gamified);
        questions = QuestionBank.getQuestions(this);
        status = findViewById(R.id.tvQuizStartStatus);
        SwitchMaterial metricsOptIn = findViewById(R.id.switchQuizLocalMetrics);
        metricsOptIn.setChecked(new QuizLocalMetricsStore(this).isEnabled());
        metricsOptIn.setOnCheckedChangeListener((button, enabled) -> {
            new QuizLocalMetricsStore(this).setEnabled(enabled);
            bindMetrics();
        });
        selectedQuestionCount = restoredCount(state == null ? 10 : state.getInt(STATE_QUESTION_COUNT, 10));
        MaterialButtonToggleGroup sizeGroup = findViewById(R.id.quizQuestionCountGroup);
        sizeGroup.check(selectedQuestionCount == 5 ? R.id.btnQuizCount5
                : selectedQuestionCount == 20 ? R.id.btnQuizCount20 : R.id.btnQuizCount10);
        sizeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                selectedQuestionCount = checkedId == R.id.btnQuizCount5 ? 5
                        : checkedId == R.id.btnQuizCount20 ? 20 : 10;
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnQuizResumeDraft).setOnClickListener(view -> {
            if (new QuizDraftStore(this).load() != null) {
                startActivity(QuizSessionContract.createResumeQuestionIntent(this));
            } else {
                bindDraft();
            }
        });
        findViewById(R.id.btnQuickReview).setOnClickListener(view -> start(QuizSessionConfig.quickReview()));
        findViewById(R.id.cardQuizAll).setOnClickListener(view -> start(QuizSessionConfig.all()));
        findViewById(R.id.cardQuizFavorites).setOnClickListener(view -> start(QuizSessionConfig.favorites()));
        findViewById(R.id.cardQuizErrors).setOnClickListener(view -> start(QuizSessionConfig.previousErrors()));
        findViewById(R.id.cardQuizSection).setOnClickListener(view -> showSections());
    }

    @Override protected void onResume() { super.onResume(); bindCards(); bindDraft(); bindMetrics(); }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_QUESTION_COUNT, selectedQuestionCount);
    }

    private static int restoredCount(int count) {
        return count == 5 || count == 20 ? count : 10;
    }

    private void bindMetrics() {
        QuizLocalMetricsStore store = new QuizLocalMetricsStore(this);
        SwitchMaterial toggle = findViewById(R.id.switchQuizLocalMetrics);
        toggle.setChecked(store.isEnabled());
        TextView summary = findViewById(R.id.tvQuizMetricsSummary);
        if (!store.isEnabled()) {
            summary.setText(R.string.quiz_metrics_disabled);
            return;
        }
        QuizLocalMetricsStore.Snapshot snapshot = store.snapshot();
        if (snapshot.getSessions() == 0L) {
            summary.setText(R.string.quiz_metrics_empty);
        } else {
            summary.setText(getString(R.string.quiz_metrics_summary,
                    snapshot.getSessions(), snapshot.getAnswered(),
                    snapshot.getAccuracyPercent()));
        }
    }

    private void bindDraft() {
        QuizDraftStore.Draft draft = new QuizDraftStore(this).load();
        View card = findViewById(R.id.cardQuizDraft);
        if (draft == null) {
            card.setVisibility(View.GONE);
            return;
        }
        ((TextView) findViewById(R.id.tvQuizDraftProgress)).setText(getString(
                R.string.quiz_draft_resume_summary,
                draft.getAnsweredCount(), draft.getQuestions().size()));
        card.setVisibility(View.VISIBLE);
    }

    private void bindCards() {
        bindCard(R.id.cardQuizAll, R.string.quiz_mode_all_title, R.string.quiz_mode_all_subtitle);
        QuizQuestionSelector available = selector();
        int favorites = available.select(QuizSessionConfig.favorites()
                .withQuestionCount(questions.size())).getQuestions().size();
        bindCard(R.id.cardQuizFavorites, R.string.quiz_mode_favorites_title,
                favorites == 0 ? R.string.quiz_mode_favorites_subtitle : R.string.quiz_questions_available, favorites);
        int errors = available.select(QuizSessionConfig.previousErrors()
                .withQuestionCount(questions.size())).getQuestions().size();
        bindCard(R.id.cardQuizErrors, R.string.quiz_mode_errors_title,
                errors == 0 ? R.string.quiz_all_caught_up : R.string.quiz_errors_pending, errors);
        bindCard(R.id.cardQuizSection, R.string.quiz_mode_section_title, R.string.quiz_mode_section_subtitle);
    }

    private void bindCard(int cardId, int titleRes, int subtitleRes, Object... args) {
        View card = findViewById(cardId);
        ((TextView) card.findViewById(R.id.tvPracticeModeTitle)).setText(titleRes);
        TextView subtitle = card.findViewById(R.id.tvPracticeModeSubtitle);
        subtitle.setText(args.length == 0 ? getString(subtitleRes) : getString(subtitleRes, args));
    }

    private void start(QuizSessionConfig config) {
        QuizSessionConfig sized = config.withQuestionCount(selectedQuestionCount);
        QuizSelection selection = selector().select(sized);
        if (!selection.isAvailable()) { showEmpty(selection.getEmptyReason()); return; }
        if (new QuizDraftStore(this).load() != null) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.quiz_draft_new_title)
                    .setMessage(R.string.quiz_draft_new_message)
                    .setPositiveButton(R.string.quiz_draft_new_confirm,
                            (dialog, which) -> startNewSession(sized))
                    .setNegativeButton(R.string.quiz_draft_keep_saved, null)
                    .show();
            return;
        }
        startNewSession(sized);
    }

    private void startNewSession(QuizSessionConfig config) {
        new QuizDraftStore(this).clear();
        status.setVisibility(View.GONE);
        startActivity(QuizSessionContract.createQuestionIntent(this, config));
    }

    private void showSections() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_quiz_sections,
                findViewById(android.R.id.content), false);
        LinearLayout list = sheet.findViewById(R.id.quizSectionList);
        for (QuizSection section : QuizSectionCatalog.getAvailableSections(this, questions)) {
            MaterialButton button = new MaterialButton(this);
            button.setText(getString(R.string.quiz_section_with_count, section.getLabel(),
                    section.getQuestionCount()));
            button.setAllCaps(false);
            button.setOnClickListener(view -> { dialog.dismiss(); start(QuizSessionConfig.section(section.getId())); });
            list.addView(button);
        }
        dialog.setContentView(sheet); dialog.show();
    }

    private QuizQuestionSelector selector() {
        return QuizSelectorFactory.create(this);
    }

    private void showEmpty(QuizSelection.EmptyReason reason) {
        status.setVisibility(View.VISIBLE);
        status.setText(reason == QuizSelection.EmptyReason.NO_FAVORITES ? R.string.quiz_no_favorites
                : reason == QuizSelection.EmptyReason.NO_FAVORITE_QUESTIONS ? R.string.quiz_no_favorite_questions
                : reason == QuizSelection.EmptyReason.NO_PREVIOUS_ERRORS ? R.string.quiz_all_caught_up
                : R.string.quiz_cpc_review_unavailable);
    }
}
