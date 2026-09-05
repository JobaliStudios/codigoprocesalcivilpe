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
    public static final String EXTRA_CONFIG = "QUIZ_SESSION_CONFIG";
    public static final String EXTRA_STATS = "QUIZ_SESSION_STATS";

    private QuizSessionContract() {
    }

    @NonNull
    public static Intent createQuestionIntent(@NonNull Context context, @NonNull QuizSessionConfig config) {
        return new Intent(context, QuizzCPCQuestions.class).putExtra(EXTRA_CONFIG, config);
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
    public static Intent createResultIntent(
            @NonNull Context context,
            @NonNull QuizSessionStats stats,
            @NonNull QuizSessionConfig config,
            long elapsedMillis,
            @NonNull List<QuizAnswerResult> incorrectAnswers
    ) {
        Intent intent = putSessionData(new Intent(context, QuizzCPCResult.class), stats.getCorrect(),
                stats.getTotal(), elapsedMillis, incorrectAnswers);
        intent.putExtra(EXTRA_STATS, stats);
        intent.putExtra(EXTRA_CONFIG, config);
        return intent;
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

    @NonNull
    public static QuizSessionConfig getConfig(@NonNull Intent intent) {
        QuizSessionConfig config;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            config = intent.getParcelableExtra(EXTRA_CONFIG, QuizSessionConfig.class);
        } else {
            config = getLegacyConfig(intent);
        }
        return config == null ? QuizSessionConfig.all() : config;
    }

    @SuppressWarnings("deprecation")
    private static QuizSessionConfig getLegacyConfig(Intent intent) {
        return intent.getParcelableExtra(EXTRA_CONFIG);
    }

    public static QuizSessionStats getStats(@NonNull Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(EXTRA_STATS, QuizSessionStats.class);
        }
        return getLegacyStats(intent);
    }

    @SuppressWarnings("deprecation")
    private static QuizSessionStats getLegacyStats(Intent intent) { return intent.getParcelableExtra(EXTRA_STATS); }

    @SuppressWarnings("deprecation")
    private static ArrayList<QuizAnswerResult> getLegacyParcelableArrayList(Intent intent) {
        return intent.getParcelableArrayListExtra(EXTRA_INCORRECT_ANSWERS);
    }
}
