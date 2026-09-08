package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialAdCoordinator;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleLegalStatusResolver;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleSequenceResolver;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleShareFormatter;
import com.jobalistudios.codigoprocesalcivilpe.contenido.VisibleArticleResolver;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.ArticleFavorites;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
import com.jobalistudios.codigoprocesalcivilpe.historial.ReadingHistoryManager;
import com.jobalistudios.codigoprocesalcivilpe.lectura.ArticleSpeechContent;
import com.jobalistudios.codigoprocesalcivilpe.lectura.ArticleSpeechContentResolver;
import com.jobalistudios.codigoprocesalcivilpe.lectura.ArticleTextToSpeechController;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryBottomSheet;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryEntry;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryResolver;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceResolver;
import com.jobalistudios.codigoprocesalcivilpe.referencias.RelatedArticlesResolver;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ResolvedArticleCrossReference;

import java.util.Collections;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SectionContentActivity extends AppBaseActivity {

    public static final String EXTRA_LAYOUT_RES_ID = "EXTRA_LAYOUT_RES_ID";
    public static final String EXTRA_TEXT_RES_ID = "EXTRA_TEXT_RES_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_SUBTITLE = "EXTRA_SUBTITLE";
    public static final String EXTRA_INITIAL_QUERY = "EXTRA_INITIAL_QUERY";
    public static final String EXTRA_INITIAL_QUERY_OFFSET = "EXTRA_INITIAL_QUERY_OFFSET";
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
    private static final long HISTORY_SCROLL_SETTLE_DELAY_MS = 350L;
    private static final String STATE_CURRENT_ARTICLE_NUMBER = "currentArticleNumber";
    private static final int MAX_RELATED_ARTICLES = 6;

    private final Deque<Intent> logicalArticleHistory = new ArrayDeque<>();
    private int currentTextResId;
    private ReaderTypography readerTypography;

    private HighlightController highlightController;
    private SectionSearchController searchController;
    private float baseContentTextSizePx;
    private ReadingHistoryManager readingHistoryManager;
    private ArticleBlock trackedArticleBlock;
    private TextView trackedContentView;
    private ScrollView trackedScrollView;
    private Article currentVisibleArticle;
    private Article preferredProgrammaticArticle;
    private ArticleSequenceResolver.Result currentSequence;
    private ArticleShareFormatter articleShareFormatter;
    private FavoritesManager favoritesManager;
    private TextView currentArticleNumberView;
    private TextView currentArticleTitleView;
    private Chip currentArticleStatusChip;
    private View readingBottomBar;
    private MaterialButton previousArticleButton;
    private MaterialButton favoriteArticleButton;
    private MaterialButton articleNoteButton;
    private MaterialButton copyArticleButton;
    private MaterialButton shareArticleButton;
    private MaterialButton nextArticleButton;
    private View articleSpeechControls;
    private MaterialButton listenArticleButton;
    private View articleSpeechPlaybackControls;
    private TextView articleSpeechStatusView;
    private MaterialButton pauseResumeArticleButton;
    private MaterialButton stopArticleSpeechButton;
    private ArticleTextToSpeechController articleSpeechController;
    private final ArticleSpeechContentResolver articleSpeechContentResolver =
            new ArticleSpeechContentResolver();
    private View relatedArticlesContainer;
    private ChipGroup relatedArticlesChipGroup;
    private String renderedRelatedArticleNumber;
    private final ArticleCrossReferenceResolver crossReferenceResolver =
            new ArticleCrossReferenceResolver();
    private final RelatedArticlesResolver relatedArticlesResolver = new RelatedArticlesResolver();
    private final NormativeHistoryResolver normativeHistoryResolver =
            new NormativeHistoryResolver();
    private final Map<String, List<ResolvedArticleCrossReference>> crossReferencesByBlock =
            new HashMap<>();
    private final Map<String, List<ArticleNavigationResolver.Target>> relatedArticlesByNumber =
            new HashMap<>();
    private final Runnable updateVisibleArticle = this::updateCurrentlyVisibleArticle;

    public static Intent createIntent(
            Context context,
            @LayoutRes int layoutResId,
            @StringRes int textResId,
            @StringRes int titleResId,
            @StringRes int subtitleResId
    ) {
        return new Intent(context, SectionContentActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .putExtra(EXTRA_LAYOUT_RES_ID, layoutResId)
                .putExtra(EXTRA_TEXT_RES_ID, textResId)
                .putExtra(EXTRA_TITLE, titleResId)
                .putExtra(EXTRA_SUBTITLE, subtitleResId);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyKeepScreenOnPreference();
        articleShareFormatter = new ArticleShareFormatter(getString(R.string.app_name));
        favoritesManager = new FavoritesManager(this);
        installLogicalBackNavigation();
        String restoredArticleNumber = savedInstanceState == null
                ? null
                : savedInstanceState.getString(STATE_CURRENT_ARTICLE_NUMBER);
        renderDestination(getIntent(), restoredArticleNumber);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void renderDestination(Intent intent, @Nullable String restoredArticleNumber) {
        tearDownCurrentDestination();
        setIntent(intent);

        int layoutResId = intent.getIntExtra(EXTRA_LAYOUT_RES_ID, R.layout.activity_section_content);
        int textResId = intent.getIntExtra(EXTRA_TEXT_RES_ID, 0);
        int titleResId = intent.getIntExtra(EXTRA_TITLE, 0);
        int subtitleResId = intent.getIntExtra(EXTRA_SUBTITLE, 0);
        currentTextResId = textResId;

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

        baseContentTextSizePx = contentView.getTextSize();
        applyFontScale(contentView, ReadingPreferenceManager.getContentFontScale(this));

        String blockKey = getResources().getResourceEntryName(textResId);
        highlightController = new HighlightController(this, contentView, blockKey, () -> {
            refreshContent(contentView, textResId);
            renderCurrentArticleState();
        });
        renderContent(contentView, textResId);
        setupReadingHistoryTracking(contentView, blockKey);

        setupHeaderActions(contentView);
        setupArticleActions();
        setupInPageSearch(contentView, textResId);

        Article restoredArticle = findArticleInTrackedBlock(restoredArticleNumber);
        int scrollToOffset = restoredArticle != null
                ? restoredArticle.offsetInBlock
                : intent.getIntExtra(EXTRA_SCROLL_TO_OFFSET, -1);
        if (scrollToOffset >= 0) {
            Article directArticle = restoredArticle != null
                    ? restoredArticle
                    : (trackedArticleBlock == null
                    ? null
                    : VisibleArticleResolver.findAtOffset(trackedArticleBlock, scrollToOffset));
            scrollToOffset(contentView, scrollToOffset, directArticle);
        } else {
            contentView.post(this::scheduleVisibleArticleRegistration);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        logicalArticleHistory.clear();
        renderDestination(intent, null);
    }

    private void installLogicalBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent previous = logicalArticleHistory.pollLast();
                if (previous != null) {
                    stopArticleSpeech();
                    renderDestination(previous, null);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void tearDownCurrentDestination() {
        if (trackedContentView != null) {
            trackedContentView.removeCallbacks(updateVisibleArticle);
        }
        if (trackedScrollView != null) {
            trackedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
        }
        if (articleSpeechController != null) {
            articleSpeechController.stop();
            articleSpeechController.release();
        }
        articleSpeechController = null;
        searchController = null;
        highlightController = null;
        trackedArticleBlock = null;
        trackedContentView = null;
        trackedScrollView = null;
        currentVisibleArticle = null;
        preferredProgrammaticArticle = null;
        currentSequence = null;
        renderedRelatedArticleNumber = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyKeepScreenOnPreference();
        renderCurrentArticleState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Article article = currentVisibleArticle != null
                ? currentVisibleArticle
                : findCurrentlyVisibleArticle();
        if (article != null) {
            outState.putString(STATE_CURRENT_ARTICLE_NUMBER, article.number);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (trackedContentView != null) {
            trackedContentView.removeCallbacks(updateVisibleArticle);
            updateCurrentlyVisibleArticle();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (articleSpeechController != null) {
            articleSpeechController.stop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (trackedContentView != null) {
            trackedContentView.removeCallbacks(updateVisibleArticle);
        }
        if (trackedScrollView != null) {
            trackedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) null);
        }
        if (articleSpeechController != null) {
            articleSpeechController.release();
            articleSpeechController = null;
        }
        super.onDestroy();
    }

    private void applyKeepScreenOnPreference() {
        if (ReadingPreferenceManager.isKeepScreenOnEnabled(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /** Desplaza el contenido hasta el offset de carácter indicado (ej. inicio de un artículo). */
    private void scrollToOffset(
            TextView contentView,
            int offset,
            @Nullable Article expectedArticle
    ) {
        ScrollView scrollView = findViewById(R.id.scrollViewContent);
        if (scrollView == null) {
            return;
        }
        contentView.post(() -> {
            preferredProgrammaticArticle = expectedArticle;
            if (expectedArticle != null) {
                applyCurrentVisibleArticle(expectedArticle, false);
            }
            // La sección de relacionados puede cambiar la altura del header. Esperamos su
            // relayout antes de calcular el scroll, especialmente en el final de un bloque.
            scrollView.post(() -> {
                Layout layout = contentView.getLayout();
                if (layout == null || contentView.getText().length() == 0) {
                    return;
                }
                int clamped = Math.max(
                        0,
                        Math.min(offset, contentView.getText().length() - 1)
                );
                int line = layout.getLineForOffset(clamped);
                scrollView.scrollTo(0, contentView.getTop() + layout.getLineTop(line));
                scheduleVisibleArticleRegistration();
            });
        });
    }

    private void setupReadingHistoryTracking(TextView contentView, String blockKey) {
        ScrollView scrollView = findViewById(R.id.scrollViewContent);
        ArticleBlock block = ArticleRepository.getBlock(this, blockKey);
        if (scrollView == null || block == null || block.articles.isEmpty()) {
            return;
        }

        readingHistoryManager = new ReadingHistoryManager(this);
        trackedArticleBlock = block;
        trackedContentView = contentView;
        trackedScrollView = scrollView;
        scrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) ->
                scheduleVisibleArticleRegistration());
    }

    @Nullable
    private Article findArticleInTrackedBlock(@Nullable String articleNumber) {
        if (articleNumber == null || trackedArticleBlock == null) {
            return null;
        }
        for (Article article : trackedArticleBlock.articles) {
            if (article.number.equals(articleNumber)) {
                return article;
            }
        }
        return null;
    }

    private void scheduleVisibleArticleRegistration() {
        if (trackedContentView == null) {
            return;
        }
        trackedContentView.removeCallbacks(updateVisibleArticle);
        trackedContentView.postDelayed(updateVisibleArticle, HISTORY_SCROLL_SETTLE_DELAY_MS);
    }

    private void updateCurrentlyVisibleArticle() {
        Article article = findCurrentlyVisibleArticle();
        applyCurrentVisibleArticle(article, true);
    }

    private void applyCurrentVisibleArticle(
            @Nullable Article article,
            boolean recordHistory
    ) {
        currentVisibleArticle = article;
        currentSequence = article == null
                ? null
                : ArticleSequenceResolver.resolve(this, article.number);
        renderCurrentArticleState();
        if (recordHistory && article != null && readingHistoryManager != null) {
            readingHistoryManager.recordArticle(article.number, article.title);
        }
    }

    private Article findCurrentlyVisibleArticle() {
        if (trackedContentView == null || trackedScrollView == null || trackedArticleBlock == null
                || trackedContentView.getLayout() == null || trackedContentView.getLayout().getHeight() == 0) {
            return null;
        }

        Layout layout = trackedContentView.getLayout();
        int viewportProbe = trackedScrollView.getScrollY() - trackedContentView.getTop()
                + trackedScrollView.getHeight() / 3;
        int vertical = Math.max(0, Math.min(viewportProbe, layout.getHeight() - 1));
        int line = layout.getLineForVertical(vertical);
        int characterOffset = layout.getLineStart(line);

        Article detected = VisibleArticleResolver.findAtOffset(trackedArticleBlock, characterOffset);
        if (preferredProgrammaticArticle != null) {
            if (preferredProgrammaticArticle == detected) {
                preferredProgrammaticArticle = null;
            } else if (isArticleHeaderVisible(preferredProgrammaticArticle, layout)) {
                return preferredProgrammaticArticle;
            } else {
                preferredProgrammaticArticle = null;
            }
        }
        return detected;
    }

    /** Mantiene un destino explícito en el encabezado cuando el final del bloque impide alinearlo arriba. */
    private boolean isArticleHeaderVisible(Article article, Layout layout) {
        int textLength = trackedContentView.getText().length();
        if (textLength == 0 || article.offsetInBlock >= textLength) {
            return false;
        }
        int headerEnd = article.offsetInBlock + article.text.indexOf('\n');
        if (headerEnd < article.offsetInBlock) {
            headerEnd = article.offsetInBlock + article.text.length();
        }
        int startLine = layout.getLineForOffset(article.offsetInBlock);
        int endOffset = Math.max(article.offsetInBlock,
                Math.min(headerEnd, textLength - 1));
        int endLine = layout.getLineForOffset(endOffset);
        int headerTop = trackedContentView.getTop() + layout.getLineTop(startLine);
        int headerBottom = trackedContentView.getTop() + layout.getLineBottom(endLine);
        int viewportTop = trackedScrollView.getScrollY();
        int viewportBottom = viewportTop + trackedScrollView.getHeight();
        return headerBottom > viewportTop && headerTop < viewportBottom;
    }

    private void setupArticleActions() {
        currentArticleNumberView = findViewById(R.id.currentArticleNumber);
        currentArticleTitleView = findViewById(R.id.currentArticleTitle);
        currentArticleStatusChip = findViewById(R.id.currentArticleStatusChip);
        readingBottomBar = findViewById(R.id.readingBottomBar);
        previousArticleButton = findViewById(R.id.btnPreviousArticle);
        favoriteArticleButton = findViewById(R.id.btnFavoriteArticle);
        articleNoteButton = findViewById(R.id.btnArticleNote);
        copyArticleButton = findViewById(R.id.btnCopyArticle);
        shareArticleButton = findViewById(R.id.btnShareArticle);
        nextArticleButton = findViewById(R.id.btnNextArticle);
        articleSpeechControls = findViewById(R.id.articleSpeechControls);
        listenArticleButton = findViewById(R.id.btnListenArticle);
        articleSpeechPlaybackControls = findViewById(R.id.articleSpeechPlaybackControls);
        articleSpeechStatusView = findViewById(R.id.articleSpeechStatus);
        pauseResumeArticleButton = findViewById(R.id.btnPauseResumeArticle);
        stopArticleSpeechButton = findViewById(R.id.btnStopArticleSpeech);
        relatedArticlesContainer = findViewById(R.id.relatedArticlesContainer);
        relatedArticlesChipGroup = findViewById(R.id.relatedArticlesChipGroup);

        if (previousArticleButton != null) {
            previousArticleButton.setOnClickListener(view -> navigateRelativeArticle(false));
        }
        if (favoriteArticleButton != null) {
            favoriteArticleButton.setOnClickListener(view -> toggleCurrentArticleFavorite());
        }
        if (articleNoteButton != null) {
            articleNoteButton.setOnClickListener(view -> openCurrentArticleNote());
        }
        if (copyArticleButton != null) {
            copyArticleButton.setOnClickListener(view -> copyCurrentArticle());
        }
        if (shareArticleButton != null) {
            shareArticleButton.setOnClickListener(view -> shareCurrentArticle());
        }
        if (nextArticleButton != null) {
            nextArticleButton.setOnClickListener(view -> navigateRelativeArticle(true));
        }
        setupArticleSpeechActions();
        renderCurrentArticleState();
    }

    private void renderCurrentArticleState() {
        Article article = currentVisibleArticle;
        boolean available = article != null;
        boolean repealed = ArticleLegalStatusResolver.isRepealed(article);
        if (currentArticleNumberView != null) {
            currentArticleNumberView.setVisibility(available ? View.VISIBLE : View.GONE);
            if (available) {
                currentArticleNumberView.setText(getString(
                        R.string.article_number_format,
                        article.number
                ));
            }
        }
        if (currentArticleStatusChip != null) {
            currentArticleStatusChip.setVisibility(repealed ? View.VISIBLE : View.GONE);
            currentArticleStatusChip.setContentDescription(repealed
                    ? getString(R.string.article_status_repealed_description, article.number)
                    : null);
        }
        if (currentArticleTitleView != null) {
            boolean hasTitle = available && !repealed && !article.title.trim().isEmpty();
            currentArticleTitleView.setVisibility(hasTitle ? View.VISIBLE : View.GONE);
            if (hasTitle) {
                currentArticleTitleView.setText(article.title);
            }
        }
        renderRelatedArticles(article);
        renderArticleSpeechControls();

        boolean hasPrevious = currentSequence != null && currentSequence.previous != null;
        boolean hasNext = currentSequence != null && currentSequence.next != null;
        setEnabled(previousArticleButton, hasPrevious);
        setEnabled(nextArticleButton, hasNext);
        setEnabled(favoriteArticleButton, available);
        setEnabled(articleNoteButton, available);
        if (copyArticleButton != null) {
            copyArticleButton.setEnabled(available);
        }
        if (shareArticleButton != null) {
            shareArticleButton.setEnabled(available);
        }

        if (!available) {
            if (favoriteArticleButton != null) {
                favoriteArticleButton.setSelected(false);
                favoriteArticleButton.setIconResource(R.drawable.baseline_star_border_24);
            }
            if (articleNoteButton != null) {
                articleNoteButton.setSelected(false);
            }
            return;
        }

        boolean favorite = favoritesManager != null
                && favoritesManager.isFavorite(ArticleFavorites.itemForArticle(article).getId());
        if (favoriteArticleButton != null) {
            favoriteArticleButton.setSelected(favorite);
            favoriteArticleButton.setIconResource(favorite
                    ? R.drawable.baseline_star_24
                    : R.drawable.baseline_star_border_24);
            favoriteArticleButton.setContentDescription(getString(
                    favorite
                            ? R.string.article_favorite_remove_description
                            : R.string.article_favorite_add_description,
                    article.number
            ));
        }

        boolean hasQuickNote = highlightController != null
                && highlightController.hasArticleQuickNote(article);
        if (articleNoteButton != null) {
            articleNoteButton.setSelected(hasQuickNote);
            articleNoteButton.setContentDescription(getString(
                    R.string.article_note_content_description,
                    article.number
            ));
        }
        if (shareArticleButton != null) {
            shareArticleButton.setContentDescription(getString(
                    R.string.article_share_dynamic_description,
                    article.number
            ));
        }
        if (previousArticleButton != null) {
            previousArticleButton.setContentDescription(getString(
                    R.string.article_previous_content_description,
                    article.number
            ));
        }
        if (nextArticleButton != null) {
            nextArticleButton.setContentDescription(getString(
                    R.string.article_next_content_description,
                    article.number
            ));
        }
        if (copyArticleButton != null) {
            copyArticleButton.setContentDescription(getString(
                    R.string.article_copy_dynamic_description,
                    article.number
            ));
        }
    }

    private void setEnabled(@Nullable MaterialButton button, boolean enabled) {
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    private void renderRelatedArticles(@Nullable Article article) {
        if (relatedArticlesContainer == null || relatedArticlesChipGroup == null) {
            return;
        }
        if (article == null) {
            relatedArticlesContainer.setVisibility(View.GONE);
            relatedArticlesChipGroup.removeAllViews();
            renderedRelatedArticleNumber = null;
            return;
        }
        if (article.number.equals(renderedRelatedArticleNumber)) {
            return;
        }
        renderedRelatedArticleNumber = article.number;
        List<ArticleNavigationResolver.Target> targets = relatedArticlesByNumber.get(article.number);
        if (targets == null) {
            targets = relatedArticlesResolver.resolve(this, article);
            relatedArticlesByNumber.put(article.number, targets);
        }
        relatedArticlesChipGroup.removeAllViews();
        int visibleCount = Math.min(MAX_RELATED_ARTICLES, targets.size());
        for (int index = 0; index < visibleCount; index++) {
            ArticleNavigationResolver.Target target = targets.get(index);
            Chip chip = new Chip(new ContextThemeWrapper(
                    this,
                    R.style.App_Chip_ArticleReference
            ));
            chip.setText(getString(R.string.related_article_chip, target.getNumber()));
            chip.setCheckable(false);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setEnsureMinTouchTargetSize(true);
            String title = target.getTitle().trim();
            chip.setContentDescription(title.isEmpty()
                    ? getString(R.string.related_article_open_description_short, target.getNumber())
                    : getString(
                    R.string.related_article_open_description,
                    target.getNumber(),
                    title
            ));
            chip.setOnClickListener(view -> navigateToArticle(target.getNumber(), false));
            relatedArticlesChipGroup.addView(chip);
        }
        relatedArticlesContainer.setVisibility(visibleCount > 0 ? View.VISIBLE : View.GONE);
    }

    /** Las referencias cruzadas conservan un único nivel lógico de regreso dentro del lector. */
    void openArticleReference(String articleNumber) {
        navigateToArticle(articleNumber, true);
    }

    private void navigateToArticle(String articleNumber, boolean rememberOrigin) {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(this, articleNumber);
        if (target == null) {
            return;
        }
        stopArticleSpeech();
        InterstitialAdCoordinator.getInstance().navigate(
                this,
                getString(R.string.admob_interstitial_ad_unit_id),
                () -> replaceCurrentDestination(target, rememberOrigin)
        );
    }

    private void replaceCurrentDestination(
            ArticleNavigationResolver.Target target,
            boolean rememberOrigin
    ) {
        if (rememberOrigin) {
            Intent origin = intentForCurrentArticle();
            if (origin != null) {
                logicalArticleHistory.addLast(origin);
            }
        }
        ArticleRepository.Location location = ArticleRepository.findArticle(
                this, target.getNumber()).get(0);
        if (trackedArticleBlock != null
                && trackedArticleBlock.key.equals(location.block.key)
                && trackedContentView != null) {
            scrollToOffset(trackedContentView, location.article.offsetInBlock, location.article);
            return;
        }
        renderDestination(target.createIntent(this), null);
    }

    @Nullable
    private Intent intentForCurrentArticle() {
        Article article = currentVisibleArticle != null
                ? currentVisibleArticle
                : findCurrentlyVisibleArticle();
        ArticleNavigationResolver.Target target = article == null
                ? null
                : ArticleNavigationResolver.resolve(this, article.number);
        return target == null ? null : target.createIntent(this);
    }

    /** Abre una capa informativa sin navegar ni modificar el artículo visible. */
    void openNormativeHistory(String articleNumber) {
        if (trackedArticleBlock == null
                || getSupportFragmentManager().isStateSaved()
                || getSupportFragmentManager().findFragmentByTag(
                NormativeHistoryBottomSheet.TAG) != null) {
            return;
        }
        NormativeHistoryBottomSheet.newInstance(
                trackedArticleBlock.key,
                articleNumber
        ).show(getSupportFragmentManager(), NormativeHistoryBottomSheet.TAG);
    }

    private void navigateRelativeArticle(boolean forward) {
        ArticleRepository.Location target = currentSequence == null
                ? null
                : (forward ? currentSequence.next : currentSequence.previous);
        if (target == null) {
            return;
        }

        stopArticleSpeech();

        if (trackedArticleBlock != null
                && trackedArticleBlock.key.equals(target.block.key)
                && trackedContentView != null) {
            InterstitialAdCoordinator.getInstance().recordNavigationWithoutInterruption();
            scrollToOffset(trackedContentView, target.article.offsetInBlock, target.article);
            return;
        }

        ArticleNavigationResolver.Target navigationTarget =
                ArticleNavigationResolver.resolve(this, target.article.number);
        if (navigationTarget != null) {
            InterstitialAdCoordinator.getInstance().navigate(
                    this,
                    getString(R.string.admob_interstitial_ad_unit_id),
                    () -> replaceCurrentDestination(navigationTarget, false)
            );
        }
    }

    private void setupArticleSpeechActions() {
        if (articleSpeechControls == null || listenArticleButton == null) {
            return;
        }
        articleSpeechController = new ArticleTextToSpeechController(
                this,
                new ArticleTextToSpeechController.Listener() {
                    @Override
                    public void onStateChanged(
                            ArticleTextToSpeechController.State state,
                            @Nullable ArticleSpeechContent activeContent,
                            int currentSpeechOffset
                    ) {
                        renderArticleSpeechControls();
                        announceArticleSpeechState(state, activeContent);
                        if (state == ArticleTextToSpeechController.State.COMPLETED
                                || state == ArticleTextToSpeechController.State.ERROR) {
                            articleSpeechControls.post(() -> {
                                if (articleSpeechController != null) {
                                    articleSpeechController.acknowledgeTerminalState();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ArticleTextToSpeechController.ErrorReason reason) {
                        showReadingSnackbar(articleSpeechErrorMessage(reason));
                    }
                }
        );
        listenArticleButton.setOnClickListener(view -> playCurrentArticle());
        if (pauseResumeArticleButton != null) {
            pauseResumeArticleButton.setOnClickListener(view -> toggleArticleSpeechPause());
        }
        if (stopArticleSpeechButton != null) {
            stopArticleSpeechButton.setOnClickListener(view -> stopArticleSpeech());
        }
    }

    private void playCurrentArticle() {
        synchronizeCurrentVisibleArticle();
        Article article = currentVisibleArticle;
        if (article == null || trackedArticleBlock == null || articleSpeechController == null) {
            showArticleUnavailableFeedback();
            return;
        }
        ArticleSpeechContent content = articleSpeechContentResolver.resolve(
                trackedArticleBlock,
                article.number
        );
        if (content == null) {
            showArticleUnavailableFeedback();
            return;
        }
        articleSpeechController.play(content);
    }

    private void toggleArticleSpeechPause() {
        if (articleSpeechController == null) {
            return;
        }
        if (articleSpeechController.getState() == ArticleTextToSpeechController.State.PLAYING) {
            articleSpeechController.pause();
        } else if (articleSpeechController.getState()
                == ArticleTextToSpeechController.State.PAUSED) {
            articleSpeechController.resume();
        }
    }

    private void stopArticleSpeech() {
        if (articleSpeechController != null) {
            articleSpeechController.stop();
        }
    }

    private void renderArticleSpeechControls() {
        if (articleSpeechControls == null || listenArticleButton == null) {
            return;
        }
        ArticleTextToSpeechController.State speechState = articleSpeechController == null
                ? ArticleTextToSpeechController.State.IDLE
                : articleSpeechController.getState();
        ArticleSpeechContent activeContent = articleSpeechController == null
                ? null
                : articleSpeechController.getActiveContent();
        boolean active = activeContent != null && (
                speechState == ArticleTextToSpeechController.State.INITIALIZING
                        || speechState == ArticleTextToSpeechController.State.PLAYING
                        || speechState == ArticleTextToSpeechController.State.PAUSED
        );
        boolean currentArticleAvailable = currentVisibleArticle != null;
        boolean visibleArticleIsActive = active && currentArticleAvailable
                && activeContent.articleNumber.equals(currentVisibleArticle.number);

        articleSpeechControls.setVisibility(
                currentArticleAvailable || active ? View.VISIBLE : View.GONE
        );
        listenArticleButton.setVisibility(currentArticleAvailable && !visibleArticleIsActive
                ? View.VISIBLE
                : View.GONE);
        if (currentArticleAvailable) {
            listenArticleButton.setContentDescription(getString(
                    R.string.article_speech_listen_description,
                    currentVisibleArticle.number
            ));
        }
        if (!active) {
            if (articleSpeechPlaybackControls != null) {
                articleSpeechPlaybackControls.setVisibility(View.GONE);
            }
            return;
        }

        if (articleSpeechPlaybackControls != null) {
            articleSpeechPlaybackControls.setVisibility(View.VISIBLE);
        }
        String articleNumber = activeContent.articleNumber;
        if (articleSpeechStatusView != null) {
            int statusRes = speechState == ArticleTextToSpeechController.State.INITIALIZING
                    ? R.string.article_speech_preparing_format
                    : (speechState == ArticleTextToSpeechController.State.PAUSED
                    ? R.string.article_speech_paused_format
                    : R.string.article_speech_playing_format);
            articleSpeechStatusView.setText(getString(statusRes, articleNumber));
        }
        if (pauseResumeArticleButton != null) {
            boolean initializing = speechState
                    == ArticleTextToSpeechController.State.INITIALIZING;
            boolean paused = speechState == ArticleTextToSpeechController.State.PAUSED;
            pauseResumeArticleButton.setVisibility(initializing ? View.GONE : View.VISIBLE);
            pauseResumeArticleButton.setText(paused
                    ? R.string.article_speech_resume
                    : R.string.article_speech_pause);
            pauseResumeArticleButton.setIconResource(paused
                    ? R.drawable.baseline_play_arrow_24
                    : R.drawable.baseline_pause_24);
            pauseResumeArticleButton.setContentDescription(getString(
                    paused
                            ? R.string.article_speech_resume_description
                            : R.string.article_speech_pause_description,
                    articleNumber
            ));
        }
        if (stopArticleSpeechButton != null) {
            stopArticleSpeechButton.setContentDescription(getString(
                    R.string.article_speech_stop_description,
                    articleNumber
            ));
        }
    }

    private void announceArticleSpeechState(
            ArticleTextToSpeechController.State state,
            @Nullable ArticleSpeechContent content
    ) {
        if (articleSpeechStatusView == null || content == null) {
            return;
        }
        if (state == ArticleTextToSpeechController.State.PLAYING) {
            articleSpeechStatusView.announceForAccessibility(getString(
                    R.string.article_speech_playing_announcement,
                    content.articleNumber
            ));
        } else if (state == ArticleTextToSpeechController.State.PAUSED) {
            articleSpeechStatusView.announceForAccessibility(getString(
                    R.string.article_speech_paused_announcement,
                    content.articleNumber
            ));
        }
    }

    private String articleSpeechErrorMessage(
            ArticleTextToSpeechController.ErrorReason reason
    ) {
        switch (reason) {
            case INITIALIZATION_FAILED:
                return getString(R.string.article_speech_error_initialization);
            case MISSING_LANGUAGE_DATA:
                return getString(R.string.article_speech_error_missing_language);
            case LANGUAGE_NOT_SUPPORTED:
                return getString(R.string.article_speech_error_unsupported_language);
            case PLAYBACK_FAILED:
            default:
                return getString(R.string.article_speech_error_playback);
        }
    }

    private void toggleCurrentArticleFavorite() {
        synchronizeCurrentVisibleArticle();
        Article article = currentVisibleArticle;
        if (article == null || favoritesManager == null) {
            showArticleUnavailableFeedback();
            return;
        }

        FavoriteItem item = ArticleFavorites.itemForArticle(article);
        favoritesManager.toggle(item);
        boolean favorite = favoritesManager.isFavorite(item.getId());
        renderCurrentArticleState();
        showReadingSnackbar(favorite
                ? R.string.favorite_added_message
                : R.string.favorite_removed_message);
    }

    private void openCurrentArticleNote() {
        synchronizeCurrentVisibleArticle();
        Article article = currentVisibleArticle;
        if (article == null || highlightController == null) {
            showArticleUnavailableFeedback();
            return;
        }
        highlightController.openArticleQuickNote(article);
    }

    private void copyCurrentArticle() {
        synchronizeCurrentVisibleArticle();
        Article article = currentVisibleArticle;
        if (article == null) {
            showArticleUnavailableFeedback();
            return;
        }

        ClipboardManager clipboard = getSystemService(ClipboardManager.class);
        if (clipboard == null) {
            showArticleUnavailableFeedback();
            return;
        }

        String formattedArticle = articleShareFormatter.format(article);
        String label = getString(R.string.article_clipboard_label, article.number);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, formattedArticle));
        showReadingSnackbar(getString(R.string.article_copy_success, article.number));
    }

    private void shareCurrentArticle() {
        synchronizeCurrentVisibleArticle();
        Article article = currentVisibleArticle;
        if (article == null) {
            showArticleUnavailableFeedback();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.article_share_subject, article.number)
        );
        shareIntent.putExtra(Intent.EXTRA_TEXT, articleShareFormatter.format(article));
        startActivity(Intent.createChooser(
                shareIntent,
                getString(R.string.article_share_chooser_title)
        ));
    }

    private void showArticleUnavailableFeedback() {
        showReadingSnackbar(R.string.article_current_unavailable);
    }

    private void synchronizeCurrentVisibleArticle() {
        applyCurrentVisibleArticle(findCurrentlyVisibleArticle(), false);
    }

    private void showReadingSnackbar(int messageRes) {
        showReadingSnackbar(getString(messageRes));
    }

    private void showReadingSnackbar(String message) {
        View feedbackAnchor = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(feedbackAnchor, message, Snackbar.LENGTH_SHORT);
        if (readingBottomBar != null) {
            snackbar.setAnchorView(readingBottomBar);
        }
        snackbar.show();
    }

    /** Acciones secundarias compactas del encabezado: Aa y Copiar. */
    private void setupHeaderActions(TextView contentView) {
        MaterialButton fontSizeButton = findViewById(R.id.btnTamanoLetra);
        if (fontSizeButton != null) {
            fontSizeButton.setOnClickListener(v -> showFontSizeDialog(contentView));
        }
    }

    private void applyFontScale(TextView contentView, float scale) {
        Article anchor = currentVisibleArticle != null
                ? currentVisibleArticle
                : findCurrentlyVisibleArticle();
        readerTypography = ReaderTypography.create(baseContentTextSizePx, scale);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, readerTypography.getBodySizePx());
        contentView.setLineSpacing(readerTypography.getLineSpacingExtraPx(), 1f);
        if (highlightController != null && currentTextResId != 0) {
            refreshContent(contentView, currentTextResId);
            if (anchor != null) {
                Article restored = findArticleInTrackedBlock(anchor.number);
                if (restored != null) {
                    scrollToOffset(contentView, restored.offsetInBlock, restored);
                }
            }
        }
        scheduleVisibleArticleRegistration();
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
        ReaderTypography typography = readerTypography == null
                ? ReaderTypography.create(baseContentTextSizePx, 1f)
                : readerTypography;
        SpannableString text = SectionTextFormatter.buildFormattedText(this, content, typography);
        String blockKey = getResources().getResourceEntryName(textResId);
        ArticleBlock block = ArticleRepository.getBlock(this, blockKey);
        List<ResolvedArticleCrossReference> references = block == null
                ? Collections.emptyList()
                : crossReferencesByBlock.computeIfAbsent(
                blockKey,
                ignored -> crossReferenceResolver.resolveBlock(this, block)
        );
        SectionTextFormatter.applyArticleCrossReferenceFormatting(
                this,
                text,
                references,
                this::openArticleReference
        );
        List<NormativeHistoryEntry> normativeHistory = block == null
                ? Collections.emptyList()
                : normativeHistoryResolver.resolveBlock(block, content);
        SectionTextFormatter.applyNormativeHistoryInteraction(
                this,
                text,
                normativeHistory,
                this::openNormativeHistory
        );
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
            int preferredOffset = getIntent().getIntExtra(EXTRA_INITIAL_QUERY_OFFSET, -1);
            if (preferredOffset >= 0) {
                searchController.submitQueryAtOffset(initialQuery.trim(), true, preferredOffset);
            } else {
                searchController.submitQuery(initialQuery.trim(), true);
            }
        }
    }
}
