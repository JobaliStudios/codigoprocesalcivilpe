package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sugerencia local y efímera basada únicamente en los errores de ESTA sesión.
 * No es una medición de dominio general ni crea historial del estudiante.
 */
public final class QuizSectionFocusAdvisor {
    private QuizSectionFocusAdvisor() { }

    public static final class Focus {
        @NonNull private final QuizSection section;
        private final int incorrectCount;

        private Focus(@NonNull QuizSection section, int incorrectCount) {
            this.section = section;
            this.incorrectCount = incorrectCount;
        }

        @NonNull public QuizSection getSection() { return section; }
        public int getIncorrectCount() { return incorrectCount; }
    }

    /**
     * Elige la sección con más preguntas falladas resolubles.
     * En empate, conserva el orden legal I–VI del catálogo existente.
     */
    @Nullable
    public static Focus recommend(
            @NonNull Context context,
            @NonNull List<QuizAnswerResult> sessionErrors
    ) {
        if (sessionErrors.isEmpty()) {
            return null;
        }

        Map<String, Integer> errorCounts = new HashMap<>();
        for (QuizAnswerResult answer : sessionErrors) {
            if (answer.isCorrect()) {
                continue;
            }
            String sectionId = QuizSectionCatalog.resolveSectionId(
                    context, answer.getRelatedArticle());
            if (sectionId != null) {
                errorCounts.put(sectionId, errorCounts.getOrDefault(sectionId, 0) + 1);
            }
        }

        Focus best = null;
        for (QuizSection section : QuizSectionCatalog.getAvailableSections(
                context, QuestionBank.getQuestions(context))) {
            int count = errorCounts.getOrDefault(section.getId(), 0);
            if (count > 0 && (best == null || count > best.getIncorrectCount())) {
                best = new Focus(section, count);
            }
        }
        return best;
    }
}
