package com.jobalistudios.codigoprocesalcivilpe.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Resultado inmutable de una pregunta respondida durante una sesión del cuestionario. */
public final class QuizAnswerResult implements Parcelable {
    private final String questionText;
    private final int selectedAnswerIndex;
    private final String selectedAnswerText;
    private final int correctAnswerIndex;
    private final String correctAnswerText;
    private final String relatedArticle;
    private final int originalPosition;
    private final boolean correct;

    public QuizAnswerResult(
            @NonNull String questionText,
            int selectedAnswerIndex,
            @NonNull String selectedAnswerText,
            int correctAnswerIndex,
            @NonNull String correctAnswerText,
            @Nullable String relatedArticle,
            int originalPosition,
            boolean correct
    ) {
        this.questionText = questionText;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.selectedAnswerText = selectedAnswerText;
        this.correctAnswerIndex = correctAnswerIndex;
        this.correctAnswerText = correctAnswerText;
        this.relatedArticle = relatedArticle;
        this.originalPosition = originalPosition;
        this.correct = correct;
    }

    private QuizAnswerResult(Parcel source) {
        questionText = source.readString();
        selectedAnswerIndex = source.readInt();
        selectedAnswerText = source.readString();
        correctAnswerIndex = source.readInt();
        correctAnswerText = source.readString();
        relatedArticle = source.readString();
        originalPosition = source.readInt();
        correct = source.readByte() != 0;
    }

    @NonNull
    public String getQuestionText() {
        return questionText;
    }

    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    @NonNull
    public String getSelectedAnswerText() {
        return selectedAnswerText;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    @NonNull
    public String getCorrectAnswerText() {
        return correctAnswerText;
    }

    @Nullable
    public String getRelatedArticle() {
        return relatedArticle;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel destination, int flags) {
        destination.writeString(questionText);
        destination.writeInt(selectedAnswerIndex);
        destination.writeString(selectedAnswerText);
        destination.writeInt(correctAnswerIndex);
        destination.writeString(correctAnswerText);
        destination.writeString(relatedArticle);
        destination.writeInt(originalPosition);
        destination.writeByte((byte) (correct ? 1 : 0));
    }

    public static final Creator<QuizAnswerResult> CREATOR = new Creator<QuizAnswerResult>() {
        @Override
        public QuizAnswerResult createFromParcel(Parcel source) {
            return new QuizAnswerResult(source);
        }

        @Override
        public QuizAnswerResult[] newArray(int size) {
            return new QuizAnswerResult[size];
        }
    };
}
