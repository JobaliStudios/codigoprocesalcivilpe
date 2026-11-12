package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Borrador único del cuestionario, guardado solo en el almacenamiento privado del dispositivo.
 * Persiste IDs opacos y opciones escogidas, nunca el texto jurídico o datos personales.
 */
public final class QuizDraftStore {
    private static final String PREFS = "quiz_session_draft";
    private static final String KEY_DRAFT = "draft_v1";
    private static final int SCHEMA = 1;

    private final Context appContext;
    private final SharedPreferences preferences;

    public QuizDraftStore(@NonNull Context context) {
        appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Persiste el borrador antes de dejar la pantalla, con una sola edición atómica. */
    public boolean save(
            @NonNull QuizSessionConfig config,
            @NonNull List<QuestionModel> questions,
            @NonNull List<Integer> selectedAnswers,
            int currentIndex,
            long elapsedMillis
    ) {
        if (questions.isEmpty() || questions.size() > QuestionBank.getQuestions(appContext).size()
                || currentIndex < 0 || currentIndex >= questions.size()
                || !(selectedAnswers.size() == currentIndex
                        || selectedAnswers.size() == currentIndex + 1)
                || elapsedMillis < 0) {
            return false;
        }

        try {
            JSONArray ids = new JSONArray();
            Set<String> unique = new HashSet<>();
            for (QuestionModel question : questions) {
                String id = QuizQuestionIdentity.forQuestion(question);
                if (!unique.add(id)) {
                    return false;
                }
                ids.put(id);
            }
            JSONArray choices = new JSONArray();
            for (int i = 0; i < selectedAnswers.size(); i++) {
                int index = selectedAnswers.get(i);
                QuestionModel question = questions.get(i);
                if (index < 0 || index >= question.getOptions().size()) {
                    return false;
                }
                choices.put(index);
            }
            JSONArray errorIds = new JSONArray();
            for (String id : config.getSessionErrorIds()) {
                errorIds.put(id);
            }

            JSONObject payload = new JSONObject();
            payload.put("schema", SCHEMA);
            payload.put("mode", config.getMode().name());
            payload.put("section", config.getSectionId() == null
                    ? JSONObject.NULL : config.getSectionId());
            payload.put("requested", config.getRequestedQuestionCount());
            payload.put("shuffle", config.shouldShuffle());
            payload.put("errors", errorIds);
            payload.put("questionIds", ids);
            payload.put("choices", choices);
            payload.put("current", currentIndex);
            payload.put("elapsed", elapsedMillis);
            return preferences.edit().putString(KEY_DRAFT, payload.toString()).commit();
        } catch (JSONException exception) {
            return false;
        }
    }

    @Nullable
    public Draft load() {
        String raw = preferences.getString(KEY_DRAFT, null);
        if (raw == null) {
            return null;
        }
        try {
            JSONObject payload = new JSONObject(raw);
            if (payload.getInt("schema") != SCHEMA) {
                throw new JSONException("Versión de borrador desconocida");
            }

            JSONArray ids = payload.getJSONArray("questionIds");
            JSONArray choices = payload.getJSONArray("choices");
            JSONArray savedErrors = payload.getJSONArray("errors");
            int current = payload.getInt("current");
            long elapsed = payload.getLong("elapsed");
            int count = payload.getInt("requested");
            if (ids.length() < 1 || ids.length() > QuestionBank.getQuestions(appContext).size()
                    || current < 0 || current >= ids.length()
                    || !(choices.length() == current || choices.length() == current + 1)
                    || elapsed < 0 || count < 1 || count > 200 || savedErrors.length() > 200) {
                throw new JSONException("Estructura de borrador no válida");
            }

            Map<String, QuestionModel> bank = new HashMap<>();
            for (QuestionModel question : QuestionBank.getQuestions(appContext)) {
                bank.put(QuizQuestionIdentity.forQuestion(question), question);
            }
            Set<String> unique = new HashSet<>();
            List<QuestionModel> questions = new ArrayList<>();
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                QuestionModel question = bank.get(id);
                if (!unique.add(id) || question == null) {
                    throw new JSONException("El banco jurídico ya no coincide con el borrador");
                }
                questions.add(question);
            }

            List<Integer> answers = new ArrayList<>();
            for (int i = 0; i < choices.length(); i++) {
                int choice = choices.getInt(i);
                if (choice < 0 || choice >= questions.get(i).getOptions().size()) {
                    throw new JSONException("Respuesta fuera de rango");
                }
                answers.add(choice);
            }
            List<String> errorIds = new ArrayList<>();
            for (int i = 0; i < savedErrors.length(); i++) {
                errorIds.add(savedErrors.getString(i));
            }
            String section = payload.isNull("section") ? null : payload.getString("section");
            QuizSessionConfig config = new QuizSessionConfig(
                    QuizPracticeMode.valueOf(payload.getString("mode")),
                    section,
                    count,
                    payload.getBoolean("shuffle"),
                    errorIds
            );
            return new Draft(config, questions, answers, current, elapsed);
        } catch (JSONException | IllegalArgumentException exception) {
            clear();
            return null;
        }
    }

    public void clear() {
        preferences.edit().remove(KEY_DRAFT).commit();
    }

    public static final class Draft {
        private final QuizSessionConfig config;
        private final List<QuestionModel> questions;
        private final List<Integer> selectedAnswers;
        private final int currentIndex;
        private final long elapsedMillis;

        private Draft(QuizSessionConfig config, List<QuestionModel> questions,
                      List<Integer> selectedAnswers, int currentIndex, long elapsedMillis) {
            this.config = config;
            this.questions = new ArrayList<>(questions);
            this.selectedAnswers = new ArrayList<>(selectedAnswers);
            this.currentIndex = currentIndex;
            this.elapsedMillis = elapsedMillis;
        }

        @NonNull public QuizSessionConfig getConfig() { return config; }
        @NonNull public List<QuestionModel> getQuestions() {
            return new ArrayList<>(questions);
        }
        @NonNull public List<Integer> getSelectedAnswers() {
            return new ArrayList<>(selectedAnswers);
        }
        public int getCurrentIndex() { return currentIndex; }
        public int getAnsweredCount() { return selectedAnswers.size(); }
        public long getElapsedMillis() { return elapsedMillis; }
    }
}
