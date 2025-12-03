package com.jobalistudios.codigoprocesalcivilpe.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class QuestionBankTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void questionsLoadFromRawResource() {
        List<QuestionModel> questions = QuestionBank.getRandomQuestions(context, 100);

        assertThat(questions.size(), is(greaterThan(0)));
    }

    @Test
    public void getRandomQuestionsHonorsRequestedLimit() {
        int requested = 5;

        List<QuestionModel> questions = QuestionBank.getRandomQuestions(context, requested);

        assertThat(questions.size(), is(requested));
    }
}
