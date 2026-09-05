package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** The single local source for questions awaiting review. */
public final class QuizErrorStore {
    private static final String PREFS_NAME = "quiz_error_review";
    private static final String KEY_QUESTION_IDS = "question_ids";

    private final SharedPreferences preferences;

    public QuizErrorStore(@NonNull Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void recordIncorrect(@NonNull QuestionModel question) {
        Set<String> updated = getMutableIds();
        updated.add(QuizQuestionIdentity.forQuestion(question));
        save(updated);
    }

    public void resolve(@NonNull QuestionModel question) {
        Set<String> updated = getMutableIds();
        updated.remove(QuizQuestionIdentity.forQuestion(question));
        save(updated);
    }

    public boolean contains(@NonNull QuestionModel question) {
        return getQuestionIds().contains(QuizQuestionIdentity.forQuestion(question));
    }

    @NonNull
    public Set<String> getQuestionIds() {
        Set<String> ids = preferences.getStringSet(KEY_QUESTION_IDS, Collections.emptySet());
        return Collections.unmodifiableSet(new HashSet<>(ids));
    }

    public void clear() {
        preferences.edit().remove(KEY_QUESTION_IDS).apply();
    }

    @NonNull
    private Set<String> getMutableIds() {
        return new HashSet<>(getQuestionIds());
    }

    private void save(@NonNull Set<String> ids) {
        preferences.edit().putStringSet(KEY_QUESTION_IDS, new HashSet<>(ids)).apply();
    }
}
