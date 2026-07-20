package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.NodeFavorites;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;


public class SectionContentActivity extends AppCompatActivity {

    public static final String EXTRA_LAYOUT_RES_ID = "EXTRA_LAYOUT_RES_ID";
    public static final String EXTRA_TEXT_RES_ID = "EXTRA_TEXT_RES_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_SUBTITLE = "EXTRA_SUBTITLE";
    public static final String EXTRA_INITIAL_QUERY = "EXTRA_INITIAL_QUERY";
    public static final String EXTRA_SCROLL_TO_OFFSET = "EXTRA_SCROLL_TO_OFFSET";
    public static final String EXTRA_SECTION_LABEL = "EXTRA_SECTION_LABEL";
    public static final String EXTRA_TITLE_LABEL = "EXTRA_TITLE_LABEL";
    public static final String EXTRA_CHAPTER_LABEL = "EXTRA_CHAPTER_LABEL";
    public static final String EXTRA_SOURCE = "EXTRA_SOURCE";
    public static final String EXTRA_LAST_UPDATED = "EXTRA_LAST_UPDATED";
    public static final String EXTRA_SHOW_BREADCRUMB = "EXTRA_SHOW_BREADCRUMB";
    public static final String EXTRA_CURRENT_DESTINATION_ID = "EXTRA_CURRENT_DESTINATION_ID";
    public static final String EXTRA_PREVIOUS_DESTINATION_ID = "EXTRA_PREVIOUS_DESTINATION_ID";
    public static final String EXTRA_NEXT_DESTINATION_ID = "EXTRA_NEXT_DESTINATION_ID";

    private static final float FONT_SCALE_STEP = 0.05f;

    private HighlightController highlightController;
    private SectionSearchController searchController;
    private float baseContentTextSizePx;

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

        applyKeepScreenOnPreference();
        baseContentTextSizePx = contentView.getTextSize();
        applyFontScale(contentView, ReadingPreferenceManager.getContentFontScale(this));

        String blockKey = getResources().getResourceEntryName(textResId);
        highlightController = new HighlightController(this, contentView, blockKey,
                () -> refreshContent(contentView, textResId));
        renderContent(contentView, textResId);

        setupHeaderActions(contentView, textResId);
        setupInPageSearch(contentView, textResId);

        int scrollToOffset = getIntent().getIntExtra(EXTRA_SCROLL_TO_OFFSET, -1);
        if (scrollToOffset >= 0) {
            scrollToOffset(contentView, scrollToOffset);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyKeepScreenOnPreference();
    }

    private void applyKeepScreenOnPreference() {
        if (ReadingPreferenceManager.isKeepScreenOnEnabled(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /** Desplaza el contenido hasta el offset de carácter indicado (ej. inicio de un artículo). */
    private void scrollToOffset(TextView contentView, int offset) {
        ScrollView scrollView = findViewById(R.id.scrollViewContent);
        if (scrollView == null) {
            return;
        }
        contentView.post(() -> {
            Layout layout = contentView.getLayout();
            if (layout == null) {
                return;
            }
            int clamped = Math.max(0, Math.min(offset, contentView.getText().length() - 1));
            int line = layout.getLineForOffset(clamped);
            scrollView.smoothScrollTo(0, contentView.getTop() + layout.getLineTop(line));
        });
    }

    /** Estrella de favorito y botón de tamaño de letra sobre la tarjeta del encabezado. */
    private void setupHeaderActions(TextView contentView, @StringRes int textResId) {
        ImageButton favoriteButton = findViewById(R.id.btnFavorito);
        String nodeId = getIntent().getStringExtra(LegalHierarchyRepository.EXTRA_NODE_ID);
        LegalHierarchyRepository.Node node = nodeId != null
                ? LegalHierarchyRepository.findNodeById(nodeId)
                : LegalHierarchyRepository.findNodeByTextRes(textResId);
        NodeFavorites.bindToggle(favoriteButton, node);

        ImageButton fontSizeButton = findViewById(R.id.btnTamanoLetra);
        if (fontSizeButton != null) {
            fontSizeButton.setOnClickListener(v -> showFontSizeDialog(contentView));
        }
    }

    private void applyFontScale(TextView contentView, float scale) {
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseContentTextSizePx * scale);
    }

    /** Diálogo de tamaño de letra; el cambio se aplica en vivo y queda guardado. */
    private void showFontSizeDialog(TextView contentView) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_font_size, null);
        TextView valueLabel = dialogView.findViewById(R.id.fontSizeValue);
        SeekBar seekBar = dialogView.findViewById(R.id.fontSizeSeekBar);

        float current = ReadingPreferenceManager.getContentFontScale(this);
        seekBar.setProgress(Math.round((current - ReadingPreferenceManager.MIN_FONT_SCALE) / FONT_SCALE_STEP));
        valueLabel.setText(getString(R.string.font_size_value, Math.round(current * 100)));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                float scale = ReadingPreferenceManager.MIN_FONT_SCALE + FONT_SCALE_STEP * progress;
                valueLabel.setText(getString(R.string.font_size_value, Math.round(scale * 100)));
                applyFontScale(contentView, scale);
                ReadingPreferenceManager.setContentFontScale(SectionContentActivity.this, scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.font_size_title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(R.string.font_size_reset, (dialog, which) -> {
                    ReadingPreferenceManager.setContentFontScale(this, ReadingPreferenceManager.DEFAULT_FONT_SCALE);
                    applyFontScale(contentView, ReadingPreferenceManager.DEFAULT_FONT_SCALE);
                })
                .show();
    }

    private void renderContent(TextView contentView, @StringRes int textResId) {
        contentView.setText(buildText(textResId));
    }

    /** Texto base con formato de artículos y los resaltados guardados del usuario. */
    private SpannableString buildText(@StringRes int textResId) {
        String content = ArticleRepository.getContentText(this, textResId);
        SpannableString text = SectionTextFormatter.buildFormattedText(this, content);
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
