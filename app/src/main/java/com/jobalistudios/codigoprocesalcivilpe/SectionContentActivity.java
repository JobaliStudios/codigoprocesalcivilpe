package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;


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
    public static final String EXTRA_SHOW_BREADCRUMB = "EXTRA_SHOW_BREADCRUMB";
    public static final String EXTRA_CURRENT_DESTINATION_ID = "EXTRA_CURRENT_DESTINATION_ID";
    public static final String EXTRA_PREVIOUS_DESTINATION_ID = "EXTRA_PREVIOUS_DESTINATION_ID";
    public static final String EXTRA_NEXT_DESTINATION_ID = "EXTRA_NEXT_DESTINATION_ID";

    private HighlightController highlightController;
    private SectionSearchController searchController;

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

        setContentView(layoutResId);

        TextView titleView = findViewById(R.id.sectionTitle);
        TextView subtitleView = findViewById(R.id.sectionSubtitle);
        TextView contentView = findViewById(R.id.textView2);
        if (titleView != null && titleResId != 0) {
            titleView.setText(titleResId);
        }
        if (subtitleView != null && subtitleResId != 0) {
            subtitleView.setText(subtitleResId);
        }

        if (textResId == 0 || contentView == null) {
            finish();
            return;
        }

        String blockKey = getResources().getResourceEntryName(textResId);
        highlightController = new HighlightController(this, contentView, blockKey,
                () -> refreshContent(contentView, textResId));
        renderContent(contentView, textResId);

        setupInPageSearch(contentView, textResId);
    }

    private void renderContent(TextView contentView, @StringRes int textResId) {
        contentView.setText(buildText(textResId));
    }

    /** Texto base con formato de artículos y los resaltados guardados del usuario. */
    private SpannableString buildText(@StringRes int textResId) {
        SpannableString text = SectionTextFormatter.buildFormattedText(this, textResId);
        highlightController.applyHighlights(text);
        return text;
    }

    /** Re-render tras cambios de resaltados, conservando la búsqueda interna activa. */
    private void refreshContent(TextView contentView, @StringRes int textResId) {
        if (searchController != null && searchController.hasActiveQuery()) {
            searchController.refreshSearch();
        } else {
            renderContent(contentView, textResId);
        }
    }

    private void setupInPageSearch(TextView contentView, @StringRes int textResId) {
        EditText searchInput = findViewById(R.id.edtBusqueda);
        View searchBarContainer = findViewById(R.id.searchBarContainer);
        FloatingActionButton searchFab = findViewById(R.id.fabBuscar);
        ImageButton nextButton = findViewById(R.id.btnSiguiente);
        ImageButton previousButton = findViewById(R.id.btnAnterior);
        ImageButton closeButton = findViewById(R.id.btnCerrarBusqueda);
        ImageButton clearButton = findViewById(R.id.btnLimpiarBusqueda);
        TextView counterView = findViewById(R.id.searchCounter);
        TextView noResultsView = findViewById(R.id.searchNoResults);
        ScrollView scrollView = findViewById(R.id.scrollViewContent);

        if (searchInput == null || searchFab == null || scrollView == null) {
            return; // el layout no incluye la búsqueda interna
        }

        searchController = new SectionSearchController(
                this, contentView, searchInput, searchBarContainer, searchFab,
                nextButton, previousButton, closeButton, clearButton,
                counterView, noResultsView, scrollView,
                () -> buildText(textResId));

        String initialQuery = getIntent().getStringExtra(EXTRA_INITIAL_QUERY);
        if (initialQuery != null && !initialQuery.trim().isEmpty()) {
            searchController.submitQuery(initialQuery.trim(), true);
        }
    }
}
