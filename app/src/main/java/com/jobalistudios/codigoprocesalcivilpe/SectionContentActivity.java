package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteDestinationMapper;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SectionContentActivity extends AppCompatActivity {

    public static final String EXTRA_LAYOUT_RES_ID = "EXTRA_LAYOUT_RES_ID";
    public static final String EXTRA_TEXT_RES_ID = "EXTRA_TEXT_RES_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_SUBTITLE = "EXTRA_SUBTITLE";
    public static final String EXTRA_INITIAL_QUERY = "EXTRA_INITIAL_QUERY";
    public static final String EXTRA_SECTION_LABEL = "EXTRA_SECTION_LABEL";
    public static final String EXTRA_TITLE_LABEL = "EXTRA_TITLE_LABEL";
    public static final String EXTRA_CHAPTER_LABEL = "EXTRA_CHAPTER_LABEL";
    public static final String EXTRA_SOURCE = "EXTRA_SOURCE";
    public static final String EXTRA_LAST_UPDATED = "EXTRA_LAST_UPDATED";
    public static final String EXTRA_CURRENT_DESTINATION_ID = "EXTRA_CURRENT_DESTINATION_ID";
    public static final String EXTRA_PREVIOUS_DESTINATION_ID = "EXTRA_PREVIOUS_DESTINATION_ID";
    public static final String EXTRA_NEXT_DESTINATION_ID = "EXTRA_NEXT_DESTINATION_ID";

    public static Intent createIntent(
            Context context,
            @LayoutRes int layoutResId,
            @StringRes int textResId,
            @StringRes int titleResId,
            @StringRes int subtitleResId
    ) {
        return new Intent(context, SectionContentActivity.class)
                .putExtra(EXTRA_LAYOUT_RES_ID, layoutResId)
                .putExtra(EXTRA_TEXT_RES_ID, textResId)
                .putExtra(EXTRA_TITLE, titleResId)
                .putExtra(EXTRA_SUBTITLE, subtitleResId);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutResId = getIntent().getIntExtra(EXTRA_LAYOUT_RES_ID, R.layout.activity_section_content);
        int textResId = getIntent().getIntExtra(EXTRA_TEXT_RES_ID, 0);
        int titleResId = getIntent().getIntExtra(EXTRA_TITLE, 0);
        int subtitleResId = getIntent().getIntExtra(EXTRA_SUBTITLE, 0);
        String initialQuery = getIntent().getStringExtra(EXTRA_INITIAL_QUERY);

        setContentView(layoutResId);

        TextView titleView = findViewById(R.id.sectionTitle);
        TextView subtitleView = findViewById(R.id.sectionSubtitle);
        TextView contentView = findViewById(R.id.textView2);
        EditText searchInput = findViewById(R.id.edtBusqueda);
        FloatingActionButton searchFab = findViewById(R.id.fabBuscar);
        ScrollView scrollView = findViewById(R.id.scrollViewContent);
        TextView breadcrumbView = findViewById(R.id.sectionBreadcrumb);
        TextView articleRangeView = findViewById(R.id.sectionArticleRange);
        TextView updateDateView = findViewById(R.id.sectionLastUpdated);
        TextView sourceView = findViewById(R.id.sectionSource);
        ProgressBar scrollProgressBar = findViewById(R.id.readingProgressBar);
        TextView readPercentView = findViewById(R.id.readingPercentText);
        Button goSectionStartButton = findViewById(R.id.btnGoSectionStart);
        Button nextChapterButton = findViewById(R.id.btnNextChapter);
        Button previousChapterButton = findViewById(R.id.btnPreviousChapter);

        if (titleView != null && titleResId != 0) {
            titleView.setText(titleResId);
        }
        if (subtitleView != null && subtitleResId != 0) {
            subtitleView.setText(subtitleResId);
        }

        if (textResId == 0 || contentView == null || searchInput == null || searchFab == null || scrollView == null) {
            finish();
            return;
        }

        contentView.setText(SectionTextFormatter.buildFormattedText(this, textResId));
        String plainContent = getString(textResId);

        if (breadcrumbView != null) {
            String chapterLabel = getIntent().getStringExtra(EXTRA_CHAPTER_LABEL);
            String titleLabel = getIntent().getStringExtra(EXTRA_TITLE_LABEL);
            String sectionLabel = getIntent().getStringExtra(EXTRA_SECTION_LABEL);

            if (TextUtils.isEmpty(chapterLabel) && titleResId != 0) {
                chapterLabel = getString(titleResId);
            }
            if (TextUtils.isEmpty(titleLabel) && subtitleResId != 0) {
                titleLabel = getString(subtitleResId);
            }
            if (TextUtils.isEmpty(sectionLabel)) {
                sectionLabel = getString(R.string.section_default_label);
            }

            breadcrumbView.setText(getString(
                    R.string.section_breadcrumb_format,
                    sectionLabel,
                    titleLabel,
                    chapterLabel
            ));
        }

        if (articleRangeView != null) {
            articleRangeView.setText(getString(R.string.section_metadata_articles, extractArticleRange(plainContent)));
        }
        if (updateDateView != null) {
            String updated = getIntent().getStringExtra(EXTRA_LAST_UPDATED);
            if (TextUtils.isEmpty(updated)) {
                updated = getString(R.string.section_default_last_updated);
            }
            updateDateView.setText(getString(R.string.section_metadata_updated, updated));
        }
        if (sourceView != null) {
            String source = getIntent().getStringExtra(EXTRA_SOURCE);
            if (TextUtils.isEmpty(source)) {
                source = getString(R.string.section_default_source);
            }
            sourceView.setText(getString(R.string.section_metadata_source, source));
        }

        if (goSectionStartButton != null) {
            goSectionStartButton.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
        }

        setupChapterNavigationButtons(previousChapterButton, nextChapterButton);
        setupReadingProgress(scrollView, scrollProgressBar, readPercentView);

        SectionSearchController searchController = new SectionSearchController(
                this,
                contentView,
                searchInput,
                findViewById(R.id.searchBarContainer),
                searchFab,
                findViewById(R.id.btnSiguiente),
                findViewById(R.id.btnAnterior),
                findViewById(R.id.btnCerrarBusqueda),
                findViewById(R.id.btnLimpiarBusqueda),
                findViewById(R.id.txtBusquedaContador),
                findViewById(R.id.txtBusquedaSinResultados),
                scrollView,
                () -> SectionTextFormatter.buildFormattedText(this, textResId)
        );

        if (initialQuery != null && !initialQuery.trim().isEmpty()) {
            searchController.submitQuery(initialQuery.trim(), true);
        }
    }

    private void setupReadingProgress(ScrollView scrollView, ProgressBar progressBar, TextView percentView) {
        if (progressBar == null || percentView == null || scrollView == null) {
            return;
        }

        Runnable updateProgress = () -> {
            int contentHeight = scrollView.getChildAt(0) != null ? scrollView.getChildAt(0).getHeight() : 0;
            int visibleHeight = scrollView.getHeight();
            int scrollRange = Math.max(contentHeight - visibleHeight, 1);
            int progress = Math.min(100, Math.max(0, (scrollView.getScrollY() * 100) / scrollRange));
            progressBar.setProgress(progress);
            percentView.setText(getString(R.string.reading_percent_format, progress));
        };

        scrollView.getViewTreeObserver().addOnScrollChangedListener(updateProgress::run);
        scrollView.post(updateProgress);
    }

    private void setupChapterNavigationButtons(Button previousButton, Button nextButton) {
        String currentDestination = getIntent().getStringExtra(EXTRA_CURRENT_DESTINATION_ID);
        String previousDestination = getIntent().getStringExtra(EXTRA_PREVIOUS_DESTINATION_ID);
        String nextDestination = getIntent().getStringExtra(EXTRA_NEXT_DESTINATION_ID);

        if (TextUtils.isEmpty(previousDestination) || TextUtils.isEmpty(nextDestination)) {
            List<String> relatedFromFavorites = getRelatedDestinationsFromFavorites(currentDestination);
            if (TextUtils.isEmpty(previousDestination) && !relatedFromFavorites.isEmpty()) {
                previousDestination = relatedFromFavorites.get(0);
            }
            if (TextUtils.isEmpty(nextDestination) && relatedFromFavorites.size() > 1) {
                nextDestination = relatedFromFavorites.get(1);
            }
        }

        setupNavigationButton(previousButton, previousDestination);
        setupNavigationButton(nextButton, nextDestination);
    }

    private void setupNavigationButton(Button button, String destinationId) {
        if (button == null) {
            return;
        }
        if (TextUtils.isEmpty(destinationId)) {
            button.setEnabled(false);
            button.setVisibility(View.GONE);
            return;
        }

        button.setOnClickListener(v -> {
            Intent destinationIntent = FavoriteDestinationMapper.toIntent(this, destinationId);
            if (destinationIntent == null) {
                Toast.makeText(this, R.string.destination_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(destinationIntent);
        });
    }

    private List<String> getRelatedDestinationsFromFavorites(String currentDestinationId) {
        List<String> neighbors = new ArrayList<>();
        if (TextUtils.isEmpty(currentDestinationId)) {
            return neighbors;
        }

        String normalizedCurrent = FavoriteDestinationMapper.normalizeDestinationId(currentDestinationId);
        List<FavoriteItem> favorites = new FavoritesManager(this).getAll();
        List<String> destinations = new ArrayList<>();
        for (FavoriteItem item : favorites) {
            String destinationId = FavoriteDestinationMapper.normalizeDestinationId(item.getDestinationId());
            if (TextUtils.isEmpty(destinationId) || FavoriteDestinationMapper.toIntent(this, destinationId) == null) {
                continue;
            }
            destinations.add(destinationId);
        }

        int index = destinations.indexOf(normalizedCurrent);
        if (index > 0) {
            neighbors.add(destinations.get(index - 1));
        }
        if (index >= 0 && index < destinations.size() - 1) {
            neighbors.add(destinations.get(index + 1));
        }
        return neighbors;
    }

    private String extractArticleRange(String content) {
        Pattern pattern = Pattern.compile("Artículo\\s+(\\d+)");
        Matcher matcher = pattern.matcher(content);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            min = Math.min(min, number);
            max = Math.max(max, number);
        }
        if (min == Integer.MAX_VALUE || max == Integer.MIN_VALUE) {
            return getString(R.string.metadata_not_available);
        }
        if (min == max) {
            return getString(R.string.section_single_article_format, min);
        }
        return getString(R.string.section_article_range_format, min, max);
    }
}
