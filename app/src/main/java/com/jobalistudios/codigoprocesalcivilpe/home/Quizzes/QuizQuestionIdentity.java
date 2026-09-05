package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Stable opaque key for local error review; it never alters question content. */
public final class QuizQuestionIdentity {
    private static final char FIELD_SEPARATOR = '\u001f';

    private QuizQuestionIdentity() {
    }

    @NonNull
    public static String forQuestion(@NonNull QuestionModel question) {
        StringBuilder source = new StringBuilder();
        append(source, question.getQuestionText());
        append(source, question.getRelatedArticle());
        append(source, String.valueOf(question.getCorrectAnswerIndex()));
        if (question.getOptions() != null) {
            for (String option : question.getOptions()) {
                append(source, option);
            }
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(source.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder encoded = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                encoded.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
            }
            return encoded.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }

    private static void append(StringBuilder target, String value) {
        target.append(value == null ? "" : value).append(FIELD_SEPARATOR);
    }
}
