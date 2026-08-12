package com.jobalistudios.codigoprocesalcivilpe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionModel {
    @SerializedName("questionText")
    private String questionText;
    @SerializedName("options")
    private List<String> options;
    @SerializedName("correctAnswerIndex")
    private int correctAnswerIndex; // 0-based
    @SerializedName("relatedArticle")
    private String relatedArticle;

    private boolean isCorrect;


    public QuestionModel() {
        // Requerido para la deserialización con Gson
    }

    public QuestionModel(String questionText, List<String> options, int correctAnswerIndex, String relatedArticle) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.relatedArticle = relatedArticle;
    }

    // Getters y setters
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public String getRelatedArticle() { return relatedArticle; }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
