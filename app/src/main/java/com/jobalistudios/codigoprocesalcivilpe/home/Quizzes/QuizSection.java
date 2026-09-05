package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import androidx.annotation.NonNull;

/** A legal CPC section with at least one local quiz question. */
public final class QuizSection {
    @NonNull private final String id;
    @NonNull private final String label;
    private final int questionCount;

    public QuizSection(@NonNull String id, @NonNull String label, int questionCount) {
        this.id = id;
        this.label = label;
        this.questionCount = Math.max(0, questionCount);
    }

    @NonNull public String getId() { return id; }
    @NonNull public String getLabel() { return label; }
    public int getQuestionCount() { return questionCount; }
}
