package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/** Mutable only while a quiz session is active; it is never persisted as user history. */
public final class QuizSessionStats implements Parcelable {
    private final int total;
    private int correct;
    private int incorrect;
    private int currentStreak;
    private int bestStreak;
    private int score;

    public QuizSessionStats(int total) {
        this.total = Math.max(0, total);
    }

    private QuizSessionStats(Parcel source) {
        total = source.readInt();
        correct = source.readInt();
        incorrect = source.readInt();
        currentStreak = source.readInt();
        bestStreak = source.readInt();
        score = source.readInt();
    }

    /** Records one first-time answer and returns its awarded points. */
    public int recordAnswer(boolean isCorrect) {
        if (!isCorrect) {
            incorrect++;
            currentStreak = 0;
            return 0;
        }
        correct++;
        currentStreak++;
        bestStreak = Math.max(bestStreak, currentStreak);
        int awarded = QuizScoreCalculator.pointsForCorrectAnswer(currentStreak);
        score += awarded;
        return awarded;
    }

    public int getTotal() { return total; }
    public int getCorrect() { return correct; }
    public int getIncorrect() { return incorrect; }
    public int getCurrentStreak() { return currentStreak; }
    public int getBestStreak() { return bestStreak; }
    public int getScore() { return score; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel destination, int flags) {
        destination.writeInt(total);
        destination.writeInt(correct);
        destination.writeInt(incorrect);
        destination.writeInt(currentStreak);
        destination.writeInt(bestStreak);
        destination.writeInt(score);
    }

    public static final Creator<QuizSessionStats> CREATOR = new Creator<QuizSessionStats>() {
        @Override
        public QuizSessionStats createFromParcel(Parcel source) {
            return new QuizSessionStats(source);
        }

        @Override
        public QuizSessionStats[] newArray(int size) {
            return new QuizSessionStats[size];
        }
    };
}
