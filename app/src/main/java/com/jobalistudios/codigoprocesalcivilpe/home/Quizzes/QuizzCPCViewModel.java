package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;

import java.util.List;

public class QuizzCPCViewModel extends ViewModel {

    private final MutableLiveData<List<QuestionModel>> questionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<QuestionModel> currentQuestionLiveData = new MutableLiveData<>();
    private int totalQuestions = 0;

    public void initQuestions(int questionCount) {
        if (questionsLiveData.getValue() == null) {
            List<QuestionModel> questions = QuestionBank.getRandomQuestions(questionCount);
            questionsLiveData.setValue(questions);
            totalQuestions = questions.size();
            currentQuestionIndex.setValue(0);
            score.setValue(0);
            updateCurrentQuestion();
        }
    }

    public void restoreState(int savedIndex, int savedScore) {
        if (questionsLiveData.getValue() == null || totalQuestions == 0) {
            return;
        }
        int boundedIndex = Math.min(savedIndex, totalQuestions - 1);
        currentQuestionIndex.setValue(Math.max(boundedIndex, 0));
        score.setValue(Math.max(savedScore, 0));
        updateCurrentQuestion();
    }

    public LiveData<QuestionModel> getCurrentQuestionLiveData() {
        return currentQuestionLiveData;
    }

    public int getCurrentQuestionIndex() {
        Integer index = currentQuestionIndex.getValue();
        return index != null ? index : 0;
    }

    public int getScore() {
        Integer currentScore = score.getValue();
        return currentScore != null ? currentScore : 0;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public boolean submitAnswer(int selectedIndex) {
        List<QuestionModel> questions = questionsLiveData.getValue();
        Integer index = currentQuestionIndex.getValue();

        if (questions == null || index == null || index < 0 || index >= questions.size()) {
            return false;
        }

        QuestionModel currentQuestion = questions.get(index);
        boolean isCorrect = selectedIndex == currentQuestion.getCorrectAnswerIndex();
        currentQuestion.setCorrect(isCorrect);

        if (isCorrect) {
            score.setValue(getScore() + 1);
        }

        return isCorrect;
    }

    public boolean moveToNextQuestion() {
        List<QuestionModel> questions = questionsLiveData.getValue();
        Integer index = currentQuestionIndex.getValue();

        if (questions == null || index == null) {
            return false;
        }

        int nextIndex = index + 1;
        if (nextIndex < questions.size()) {
            currentQuestionIndex.setValue(nextIndex);
            updateCurrentQuestion();
            return true;
        }
        return false;
    }

    private void updateCurrentQuestion() {
        List<QuestionModel> questions = questionsLiveData.getValue();
        Integer index = currentQuestionIndex.getValue();

        if (questions != null && index != null && index >= 0 && index < questions.size()) {
            currentQuestionLiveData.setValue(questions.get(index));
        }
    }
}
