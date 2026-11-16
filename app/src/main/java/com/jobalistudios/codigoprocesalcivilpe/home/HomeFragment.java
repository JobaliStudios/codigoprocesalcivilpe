package com.jobalistudios.codigoprocesalcivilpe.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.res.ColorStateList;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jobalistudios.codigoprocesalcivilpe.apuntes.MyNotesActivity;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.databinding.FragmentHomeBinding;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigoProcesalCivilMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCStartScreen;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeDashboardStateBuilder stateBuilder;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        stateBuilder = new HomeDashboardStateBuilder(requireContext());
        configureActions();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderDashboard(stateBuilder.build());
    }

    private void configureActions() {
        binding.buttonConsultarCodigo.setOnClickListener(view -> openCode());
        binding.buttonExploreCode.setOnClickListener(view -> openCode());
        binding.buttonHomeCode.setOnClickListener(view -> openCode());
        binding.buttonHomeSearch.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.navigation_busqueda));
        binding.buttonHomeQuiz.setOnClickListener(view -> startActivity(
                new Intent(requireContext(), QuizzCPCStartScreen.class)));
        binding.buttonHomeNotes.setOnClickListener(view -> startActivity(
                new Intent(requireContext(), MyNotesActivity.class)));
        binding.buttonPracticeQuiz.setOnClickListener(view -> startActivity(
                new Intent(requireContext(), QuizzCPCStartScreen.class)));
        binding.buttonViewAllFavorites.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.navigation_favoritos));
    }

    private void openCode() {
        startActivity(new Intent(requireContext(), CodigoProcesalCivilMain.class));
    }

    private void renderDashboard(HomeDashboardState state) {
        if (binding == null) {
            return;
        }
        renderReadingHistory(state);
        renderDailyArticle(state);
        renderRecentFavorites(state.recentFavorites);
    }

    private void renderReadingHistory(HomeDashboardState state) {
        boolean hasContinueReading = state.continueReading != null;
        boolean hasRecentlyViewed = !state.recentlyViewed.isEmpty();
        binding.readingHistorySection.setVisibility(
                hasContinueReading || hasRecentlyViewed ? View.VISIBLE : View.GONE);
        binding.continueReadingSection.setVisibility(
                hasContinueReading ? View.VISIBLE : View.GONE);
        binding.recentlyViewedSection.setVisibility(
                hasRecentlyViewed ? View.VISIBLE : View.GONE);

        if (state.continueReading != null) {
            bindContinueReading(state.continueReading);
            if (state.continueTotal > 0) {
                binding.continueReadingProgressLabel.setText(getString(
                        R.string.premium_home_reading_position,
                        getString(R.string.article_number_format, state.continueReading.number),
                        state.continuePosition, state.continueTotal));
                binding.continueReadingProgress.setMax(state.continueTotal);
                binding.continueReadingProgress.setProgressCompat(state.continuePosition, false);
                binding.continueReadingProgress.setContentDescription(getString(
                        R.string.reader_chapter_progress_accessibility,
                        state.continuePosition, state.continueTotal));
            }
        }
        bindRecentChips(state.recentlyViewed);
    }

    private void bindContinueReading(HomeDashboardState.ArticleEntry article) {
        binding.continueArticleNumber.setText(
                getString(R.string.article_number_format, article.number));
        bindOptionalTitle(binding.continueArticleTitle, article.title);
        binding.cardContinueReading.setContentDescription(getString(
                R.string.home_continue_article_description,
                accessibleArticleLabel(article)
        ));
        binding.cardContinueReading.setOnClickListener(view -> openArticle(article.number));
        binding.buttonContinueReading.setOnClickListener(view -> openArticle(article.number));
    }


    private void renderDailyArticle(HomeDashboardState state) {
        boolean hasDaily = state.articleOfTheDay != null;
        binding.cardArticleOfTheDay.setVisibility(hasDaily ? View.VISIBLE : View.GONE);
        if (!hasDaily) return;
        ArticleOfTheDaySelector.Selection selection = state.articleOfTheDay;
        Article article = selection.article;
        binding.dailyDateLabel.setText(selection.dateLabel);
        binding.dailyArticleNumber.setText(getString(R.string.article_number_format, article.number));
        bindOptionalTitle(binding.dailyArticleTitle, article.title);
        binding.dailyArticleExcerpt.setText(selection.excerpt);
        binding.dailyArticleExcerpt.setVisibility(selection.excerpt.isEmpty() ? View.GONE : View.VISIBLE);
        binding.buttonDailyRead.setOnClickListener(v -> openArticle(article.number));
        binding.cardArticleOfTheDay.setContentDescription(getString(
                R.string.premium_home_daily_label) + ", "
                + accessibleArticleLabel(new HomeDashboardState.ArticleEntry(
                article.number, article.title)));
        binding.buttonDailyFavorite.setSelected(state.articleOfTheDayIsFavorite);
        binding.buttonDailyFavorite.setImageResource(state.articleOfTheDayIsFavorite
                ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
        binding.buttonDailyFavorite.setContentDescription(getString(
                state.articleOfTheDayIsFavorite
                        ? R.string.premium_home_daily_favorite_remove
                        : R.string.premium_home_daily_favorite_add, article.number));
        binding.buttonDailyFavorite.setOnClickListener(view -> {
            new FavoritesManager(requireContext()).toggle(ArticleFavorites.itemForArticle(article));
            renderDashboard(stateBuilder.build());
        });
    }

    private void bindRecentChips(List<HomeDashboardState.ArticleEntry> recent) {
        binding.recentArticlesContainer.removeAllViews();
        for (HomeDashboardState.ArticleEntry item : recent) {
            Chip chip = new Chip(requireContext());
            chip.setText(getString(R.string.article_number_format, item.number));
            chip.setCheckable(false);
            chip.setEnsureMinTouchTargetSize(true);
            chip.setMinHeight(getResources().getDimensionPixelSize(R.dimen.touch_target_min));
            chip.setContentDescription(getString(R.string.premium_home_recent_chip,
                    item.number, item.title));
            chip.setOnClickListener(v -> openArticle(item.number));
            binding.recentArticlesContainer.addView(chip);
        }
    }

    private void renderRecentFavorites(List<HomeDashboardState.ArticleEntry> favorites) {
        boolean empty = favorites.isEmpty();
        binding.favoriteArticlesContainer.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.favoritesEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        bindArticleRows(binding.favoriteArticlesContainer, favorites, true);
    }

    private void bindArticleRows(
            ViewGroup container,
            List<HomeDashboardState.ArticleEntry> articles,
            boolean favorite
    ) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (HomeDashboardState.ArticleEntry article : articles) {
            View item = inflater.inflate(R.layout.item_recent_article, container, false);
            ImageView icon = item.findViewById(R.id.dashboard_article_icon);
            TextView number = item.findViewById(R.id.dashboard_article_number);
            TextView title = item.findViewById(R.id.dashboard_article_title);
            icon.setImageResource(favorite
                    ? R.drawable.baseline_star_24
                    : R.drawable.baseline_history_24);
            number.setText(getString(R.string.article_number_format, article.number));
            bindOptionalTitle(title, article.title);
            item.setContentDescription(getString(
                    favorite
                            ? R.string.home_open_favorite_article_description
                            : R.string.home_open_recent_article_description,
                    accessibleArticleLabel(article)
            ));
            item.setOnClickListener(view -> openArticle(article.number));
            container.addView(item);
        }
    }

    private void openArticle(String articleNumber) {
        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                requireContext(), articleNumber);
        if (target == null) {
            renderDashboard(stateBuilder.build());
            return;
        }
        startActivity(target.createIntent(requireContext()));
    }

    private String accessibleArticleLabel(HomeDashboardState.ArticleEntry article) {
        String number = getString(R.string.article_number_format, article.number);
        return article.title.isEmpty() ? number : number + ", " + article.title;
    }

    private void bindOptionalTitle(TextView view, String title) {
        view.setText(title);
        view.setVisibility(title.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
