package com.jobalistudios.codigoprocesalcivilpe.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigoProcesalCivilMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCStartScreen;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentHomeBinding;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.RecentArticle;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ReadingHistoryManager readingHistoryManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        readingHistoryManager = new ReadingHistoryManager(requireContext());

        MaterialCardView cardProcesal = root.findViewById(R.id.card_codigos);
        cardProcesal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CodigoProcesalCivilMain.class);
            startActivity(intent);
        });

        MaterialCardView cardQuizzes = root.findViewById(R.id.card_quizzes);
        cardQuizzes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizzCPCStartScreen.class);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        renderReadingHistory();
    }

    private void renderReadingHistory() {
        if (binding == null) {
            return;
        }

        List<RecentArticle> validArticles = new ArrayList<>();
        List<ArticleNavigationResolver.Target> targets = new ArrayList<>();
        for (RecentArticle recent : readingHistoryManager.getRecentArticles()) {
            ArticleNavigationResolver.Target target =
                    ArticleNavigationResolver.resolve(requireContext(), recent.getNumber());
            if (target == null) {
                readingHistoryManager.removeArticle(recent.getNumber());
                continue;
            }
            validArticles.add(recent);
            targets.add(target);
        }

        if (validArticles.isEmpty()) {
            binding.readingHistorySection.setVisibility(View.GONE);
            binding.recentArticlesContainer.removeAllViews();
            return;
        }

        binding.readingHistorySection.setVisibility(View.VISIBLE);
        bindContinueReading(validArticles.get(0), targets.get(0));
        bindRecentArticles(validArticles, targets);
    }

    private void bindContinueReading(RecentArticle recent, ArticleNavigationResolver.Target target) {
        String title = displayTitle(recent, target);
        binding.continueArticleNumber.setText(
                getString(R.string.article_number_format, target.getNumber()));
        bindOptionalTitle(binding.continueArticleTitle, title);
        binding.cardContinueReading.setContentDescription(
                articleContentDescription(target.getNumber(), title));
        binding.cardContinueReading.setOnClickListener(v -> openArticle(target.getNumber()));
    }

    private void bindRecentArticles(List<RecentArticle> recent,
                                    List<ArticleNavigationResolver.Target> targets) {
        binding.recentArticlesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int index = 0; index < recent.size(); index++) {
            ArticleNavigationResolver.Target target = targets.get(index);
            String title = displayTitle(recent.get(index), target);
            View item = inflater.inflate(R.layout.item_recent_article,
                    binding.recentArticlesContainer, false);
            TextView numberView = item.findViewById(R.id.recent_article_number);
            TextView titleView = item.findViewById(R.id.recent_article_title);
            numberView.setText(getString(R.string.article_number_format, target.getNumber()));
            bindOptionalTitle(titleView, title);
            item.setContentDescription(articleContentDescription(target.getNumber(), title));
            item.setOnClickListener(v -> openArticle(target.getNumber()));
            binding.recentArticlesContainer.addView(item);
        }
    }

    private void openArticle(String articleNumber) {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(requireContext(), articleNumber);
        if (target == null) {
            readingHistoryManager.removeArticle(articleNumber);
            renderReadingHistory();
            return;
        }
        startActivity(target.createIntent(requireContext()));
    }

    private String displayTitle(RecentArticle recent, ArticleNavigationResolver.Target target) {
        return target.getTitle().isEmpty() ? recent.getTitle() : target.getTitle();
    }

    private void bindOptionalTitle(TextView view, String title) {
        view.setText(title);
        view.setVisibility(title.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private String articleContentDescription(String number, String title) {
        String article = getString(R.string.article_number_format, number);
        return title.isEmpty() ? article : article + ". " + title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
