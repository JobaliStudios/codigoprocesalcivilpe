package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.List;

public class QuizzCPCStartScreen extends AppCompatActivity {
    private TextView status;
    private List<QuestionModel> questions;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_quizz_cpcstart_gamified);
        questions = QuestionBank.getQuestions(this);
        status = findViewById(R.id.tvQuizStartStatus);
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnQuickReview).setOnClickListener(view -> start(QuizSessionConfig.quickReview()));
        findViewById(R.id.cardQuizAll).setOnClickListener(view -> start(QuizSessionConfig.all()));
        findViewById(R.id.cardQuizFavorites).setOnClickListener(view -> start(QuizSessionConfig.favorites()));
        findViewById(R.id.cardQuizErrors).setOnClickListener(view -> start(QuizSessionConfig.previousErrors()));
        findViewById(R.id.cardQuizSection).setOnClickListener(view -> showSections());
    }

    @Override protected void onResume() { super.onResume(); bindCards(); }

    private void bindCards() {
        bindCard(R.id.cardQuizAll, R.string.quiz_mode_all_title, R.string.quiz_mode_all_subtitle);
        int favorites = selector().select(QuizSessionConfig.favorites()).getQuestions().size();
        bindCard(R.id.cardQuizFavorites, R.string.quiz_mode_favorites_title,
                favorites == 0 ? R.string.quiz_mode_favorites_subtitle : R.string.quiz_questions_available, favorites);
        int errors = selector().select(QuizSessionConfig.previousErrors()).getQuestions().size();
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
        QuizSelection selection = selector().select(config);
        if (!selection.isAvailable()) { showEmpty(selection.getEmptyReason()); return; }
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
