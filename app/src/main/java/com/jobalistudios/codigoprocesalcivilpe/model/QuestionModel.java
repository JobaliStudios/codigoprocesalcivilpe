package com.jobalistudios.codigoprocesalcivilpe.model;

import java.util.List;

public class QuestionModel {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex; // 0-based
    private String relatedArticle;

    private boolean isCorrect;


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