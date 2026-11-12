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

    /**
     * Reconstruye el puntaje y la racha sin volver a modificar la lista de errores.
     * La selección y las respuestas son verificadas previamente por QuizDraftStore.
     */
    public void restoreDraft(QuizDraftStore.Draft draft) {
        if (questionsLiveData.getValue() != null) {
            return;
        }
        List<QuestionModel> savedQuestions = draft.getQuestions();
        List<Integer> choices = draft.getSelectedAnswers();
        config = draft.getConfig();
        totalQuestions = savedQuestions.size();
        stats = new QuizSessionStats(totalQuestions);
        answersByPosition.clear();
        currentAnswerPoints = 0;

        for (int index = 0; index < choices.size(); index++) {
            QuestionModel question = savedQuestions.get(index);
            int selected = choices.get(index);
            int correctIndex = question.getCorrectAnswerIndex();
            boolean correct = selected == correctIndex;
            question.setCorrect(correct);
            int awarded = stats.recordAnswer(correct);
            answersByPosition.put(index, new QuizAnswerResult(
                    question.getQuestionText(),
                    selected,
                    question.getOptions().get(selected),
                    correctIndex,
                    question.getOptions().get(correctIndex),
                    question.getRelatedArticle(),
                    index,
                    correct
            ));
            if (index == draft.getCurrentIndex()) {
                currentAnswerPoints = awarded;
            }
        }

        questionsLiveData.setValue(new ArrayList<>(savedQuestions));
        currentQuestionIndex.setValue(draft.getCurrentIndex());
        updateCurrentQuestion();
    }

    public List<QuestionModel> getSessionQuestions() {
        List<QuestionModel> questions = questionsLiveData.getValue();
        return questions == null ? new ArrayList<>() : new ArrayList<>(questions);
    }

    /** Las respuestas son un prefijo consecutivo, nunca se salta una pregunta. */
    public List<Integer> getSelectedAnswers() {
        List<Integer> choices = new ArrayList<>();
        for (int index = 0; index < answersByPosition.size(); index++) {
            QuizAnswerResult answer = answersByPosition.get(index);
            if (answer == null) {
                break;
            }
            choices.add(answer.getSelectedAnswerIndex());
        }
        return choices;
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
        if (!isCorrect) {
            errorStore.recordIncorrect(currentQuestion);
        } else if (errorStore.contains(currentQuestion)) {
            // Un acierto consolida el aprendizaje en cualquier modo, sin borrar otros errores.
            errorStore.resolve(currentQuestion);
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
