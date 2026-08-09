package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import java.util.ArrayList;
import java.util.List;

/** Contrato único para transportar el resumen pequeño de una sesión del cuestionario. */
public final class QuizSessionContract {
    public static final String EXTRA_SCORE = "SCORE";
    public static final String EXTRA_TOTAL = "TOTAL";
    public static final String EXTRA_ELAPSED_MILLIS = "ELAPSED_MILLIS";
    public static final String EXTRA_INCORRECT_ANSWERS = "INCORRECT_ANSWERS";

    private QuizSessionContract() {
    }

    @NonNull
    public static Intent createResultIntent(
            @NonNull Context context,
            int score,
            int total,
            long elapsedMillis,
            @NonNull List<QuizAnswerResult> incorrectAnswers
    ) {
        return putSessionData(
                new Intent(context, QuizzCPCResult.class),
                score,
                total,
                elapsedMillis,
                incorrectAnswers
        );
    }

    @NonNull
    public static Intent createReviewIntent(
            @NonNull Context context,
            int score,
            int total,
            long elapsedMillis,
            @NonNull List<QuizAnswerResult> incorrectAnswers
    ) {
        return putSessionData(
                new Intent(context, QuizzCPCReviewErrors.class),
                score,
                total,
                elapsedMillis,
                incorrectAnswers
        );
    }

    @NonNull
    private static Intent putSessionData(
            @NonNull Intent intent,
            int score,
            int total,
            long elapsedMillis,
            @NonNull List<QuizAnswerResult> incorrectAnswers
    ) {
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_TOTAL, total);
        intent.putExtra(EXTRA_ELAPSED_MILLIS, elapsedMillis);
        intent.putParcelableArrayListExtra(
                EXTRA_INCORRECT_ANSWERS,
                new ArrayList<>(incorrectAnswers)
        );
        return intent;
    }

    @NonNull
    public static ArrayList<QuizAnswerResult> getIncorrectAnswers(@NonNull Intent intent) {
        ArrayList<QuizAnswerResult> answers;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            answers = intent.getParcelableArrayListExtra(
                    EXTRA_INCORRECT_ANSWERS,
                    QuizAnswerResult.class
            );
        } else {
            answers = getLegacyParcelableArrayList(intent);
        }
        return answers != null ? answers : new ArrayList<>();
    }

    @SuppressWarnings("deprecation")
    private static ArrayList<QuizAnswerResult> getLegacyParcelableArrayList(Intent intent) {
        return intent.getParcelableArrayListExtra(EXTRA_INCORRECT_ANSWERS);
    }
}
