package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jobalistudios.codigoprocesalcivilpe.model.QuestionBank;
import com.jobalistudios.codigoprocesalcivilpe.model.QuestionModel;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizzCPCViewModel extends AndroidViewModel {

    private final QuizErrorStore errorStore;

    public QuizzCPCViewModel(Application application) {
        this(application, new QuizErrorStore(application));
    }

    QuizzCPCViewModel(Application application, QuizErrorStore errorStore) {
        super(application);
        this.errorStore = errorStore;
    }

    private final MutableLiveData<List<QuestionModel>> questionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<QuestionModel> currentQuestionLiveData = new MutableLiveData<>();
    private final Map<Integer, QuizAnswerResult> answersByPosition = new LinkedHashMap<>();
    private int totalQuestions = 0;
    private QuizSessionConfig config;
    private QuizSessionStats stats;
    private int currentAnswerPoints;

    public void initQuestions(int questionCount) {
        initSession(new QuizSessionConfig(
                        QuizPracticeMode.ALL, null, questionCount, true, new ArrayList<>()),
                QuestionBank.getRandomQuestions(getApplication(), questionCount));
    }

    public void initSession(QuizSessionConfig config, List<QuestionModel> questions) {
        if (questionsLiveData.getValue() != null) {
            return;
        }
        this.config = config;
        questionsLiveData.setValue(new ArrayList<>(questions));
        totalQuestions = questions.size();
        stats = new QuizSessionStats(totalQuestions);
        currentQuestionIndex.setValue(0);
        currentAnswerPoints = 0;
        answersByPosition.clear();
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
        return getStats().getCorrect();
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

        QuizAnswerResult existingAnswer = answersByPosition.get(index);
        if (existingAnswer != null) {
            return existingAnswer.isCorrect();
        }

        QuestionModel currentQuestion = questions.get(index);
        if (currentQuestion.getOptions() == null
                || selectedIndex < 0
                || selectedIndex >= currentQuestion.getOptions().size()
                || currentQuestion.getCorrectAnswerIndex() < 0
                || currentQuestion.getCorrectAnswerIndex() >= currentQuestion.getOptions().size()) {
            return false;
        }

        int correctAnswerIndex = currentQuestion.getCorrectAnswerIndex();
        boolean isCorrect = selectedIndex == correctAnswerIndex;
        currentQuestion.setCorrect(isCorrect);
        answersByPosition.put(index, new QuizAnswerResult(
                currentQuestion.getQuestionText(),
                selectedIndex,
                currentQuestion.getOptions().get(selectedIndex),
                correctAnswerIndex,
                currentQuestion.getOptions().get(correctAnswerIndex),
                currentQuestion.getRelatedArticle(),
                index,
                isCorrect
        ));

        currentAnswerPoints = getStats().recordAnswer(isCorrect);
        if (isCorrect && config != null && config.getMode() == QuizPracticeMode.PREVIOUS_ERRORS) {
            errorStore.resolve(currentQuestion);
        } else if (!isCorrect) {
            errorStore.recordIncorrect(currentQuestion);
        }

        return isCorrect;
    }

    public QuizAnswerResult getCurrentAnswer() {
        return answersByPosition.get(getCurrentQuestionIndex());
    }

    public int getAnswerCount() {
        return answersByPosition.size();
    }

    public QuizSessionStats getStats() {
        if (stats == null) {
            stats = new QuizSessionStats(totalQuestions);
        }
        return stats;
    }

    public int getCurrentAnswerPoints() {
        return currentAnswerPoints;
    }

    public QuizSessionConfig getConfig() {
        return config;
    }

    public List<QuizAnswerResult> getIncorrectAnswers() {
        List<QuizAnswerResult> incorrectAnswers = new ArrayList<>();
        for (QuizAnswerResult answer : answersByPosition.values()) {
            if (!answer.isCorrect()) {
                incorrectAnswers.add(answer);
            }
        }
        return incorrectAnswers;
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
