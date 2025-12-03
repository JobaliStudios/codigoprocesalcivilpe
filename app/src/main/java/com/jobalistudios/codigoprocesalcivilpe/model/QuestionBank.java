package com.jobalistudios.codigoprocesalcivilpe.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobalistudios.codigoprocesalcivilpe.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {

    private static List<QuestionModel> cachedQuestions;

    private QuestionBank() {
    }

    public static List<QuestionModel> getRandomQuestions(Context context, int count) {
        List<QuestionModel> allQuestions = new ArrayList<>();
        for (QuestionModel question : getQuestions(context)) {
            allQuestions.add(new QuestionModel(
                    question.getQuestionText(),
                    new ArrayList<>(question.getOptions()),
                    question.getCorrectAnswerIndex(),
                    question.getRelatedArticle()
            ));
        }
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }

    private static List<QuestionModel> getQuestions(Context context) {
        if (cachedQuestions == null) {
            synchronized (QuestionBank.class) {
                if (cachedQuestions == null) {
                    cachedQuestions = Collections.unmodifiableList(loadQuestionsFromJson(context));
                }
            }
        }
        return cachedQuestions;
    }

    private static List<QuestionModel> loadQuestionsFromJson(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.questions);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<QuestionModel>>() {}.getType();
            List<QuestionModel> questions = new Gson().fromJson(reader, listType);
            if (questions == null) {
                return Collections.emptyList();
            }
            return questions;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron cargar las preguntas", e);
        }
    }
}
