package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Selection result including the empty-state reason needed by the UI. */
public final class QuizSelection {
    public enum EmptyReason {
        NONE,
        NO_FAVORITES,
        NO_FAVORITE_QUESTIONS,
        NO_PREVIOUS_ERRORS,
        NO_SECTION_QUESTIONS,
        NO_QUESTIONS
    }

    @NonNull private final List<QuestionModel> questions;
    @NonNull private final EmptyReason emptyReason;

    public QuizSelection(@NonNull List<QuestionModel> questions, @NonNull EmptyReason emptyReason) {
        this.questions = Collections.unmodifiableList(new ArrayList<>(questions));
        this.emptyReason = emptyReason;
    }

    @NonNull public List<QuestionModel> getQuestions() { return questions; }
    @NonNull public EmptyReason getEmptyReason() { return emptyReason; }
    public boolean isAvailable() { return !questions.isEmpty(); }
}
