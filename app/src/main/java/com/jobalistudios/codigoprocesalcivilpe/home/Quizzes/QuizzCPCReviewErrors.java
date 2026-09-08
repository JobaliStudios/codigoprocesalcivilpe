package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import com.jobalistudios.codigoprocesalcivilpe.AppBaseActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.model.QuizAnswerResult;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import com.jobalistudios.codigoprocesalcivilpe.navigation.RelatedArticleNumberExtractor;

import java.util.ArrayList;

public class QuizzCPCReviewErrors extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz_cpcreview_errors);

        findViewById(R.id.btnReviewBack).setOnClickListener(view -> finish());

        ArrayList<QuizAnswerResult> incorrectAnswers =
                QuizSessionContract.getIncorrectAnswers(getIntent());
        LinearLayout container = findViewById(R.id.reviewErrorsContainer);
        TextView emptyState = findViewById(R.id.tvReviewEmpty);

        if (incorrectAnswers.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int index = 0; index < incorrectAnswers.size(); index++) {
            View item = inflater.inflate(R.layout.item_quiz_error_review, container, false);
            bindError(item, incorrectAnswers.get(index), index, incorrectAnswers.size());
            container.addView(item);
        }
    }

    private void bindError(
            View item,
            QuizAnswerResult answer,
            int errorIndex,
            int totalErrors
    ) {
        TextView position = item.findViewById(R.id.tvReviewItemPosition);
        TextView question = item.findViewById(R.id.tvReviewQuestion);
        TextView selectedAnswer = item.findViewById(R.id.tvReviewSelectedAnswer);
        TextView correctAnswer = item.findViewById(R.id.tvReviewCorrectAnswer);
        LinearLayout relatedArticleGroup = item.findViewById(R.id.relatedArticleGroup);
        TextView relatedArticle = item.findViewById(R.id.tvReviewRelatedArticle);
        MaterialButton openArticle = item.findViewById(R.id.btnReviewOpenArticle);

        position.setText(getString(
                R.string.quiz_cpc_review_item_position,
                errorIndex + 1,
                totalErrors,
                answer.getOriginalPosition() + 1
        ));
        question.setText(answer.getQuestionText());
        selectedAnswer.setText(answer.getSelectedAnswerText());
        correctAnswer.setText(answer.getCorrectAnswerText());

        String relatedArticleText = answer.getRelatedArticle();
        if (relatedArticleText == null || relatedArticleText.trim().isEmpty()) {
            relatedArticleGroup.setVisibility(View.GONE);
        } else {
            relatedArticle.setText(relatedArticleText);
        }

        String articleNumber = RelatedArticleNumberExtractor.extract(relatedArticleText);
        ArticleNavigationResolver.Target target = articleNumber == null
                ? null
                : ArticleNavigationResolver.resolve(this, articleNumber);
        if (target == null) {
            openArticle.setVisibility(View.GONE);
            return;
        }

        openArticle.setOnClickListener(view -> startActivity(target.createIntent(this)));
    }
}
