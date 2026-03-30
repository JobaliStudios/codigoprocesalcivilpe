package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
}
