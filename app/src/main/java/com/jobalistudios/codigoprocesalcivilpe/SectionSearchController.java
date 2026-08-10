package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import com.jobalistudios.codigoprocesalcivilpe.resaltados.HighlightController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SectionSearchController {

    public interface FormattedTextProvider {
        SpannableString getFormattedText();
    }

    private final Context context;
    private final TextView textView;
    private final EditText searchInput;
    private final View searchBarContainer;
    private final FloatingActionButton searchFab;
    private final ScrollView scrollView;
    private final FormattedTextProvider textProvider;
    private final TextView searchCounterView;
    private final TextView noResultsView;
    private final ImageButton clearButton;

    private final List<Integer> searchPositions = new ArrayList<>();
    private int currentSearchIndex = 0;

    public SectionSearchController(
            Context context,
            TextView textView,
            EditText searchInput,
            View searchBarContainer,
            FloatingActionButton searchFab,
            ImageButton nextButton,
            ImageButton previousButton,
            ImageButton closeButton,
            ImageButton clearButton,
            TextView searchCounterView,
            TextView noResultsView,
            ScrollView scrollView,
            FormattedTextProvider textProvider
    ) {
        this.context = context;
        this.textView = textView;
        this.searchInput = searchInput;
        this.searchBarContainer = searchBarContainer;
        this.searchFab = searchFab;
        this.scrollView = scrollView;
        this.textProvider = textProvider;
        this.searchCounterView = searchCounterView;
        this.noResultsView = noResultsView;
        this.clearButton = clearButton;

        searchFab.setOnClickListener(view -> showSearchBar());
        closeButton.setOnClickListener(view -> hideSearchBar());
        searchInput.addTextChangedListener(createTextWatcher());
        clearButton.setOnClickListener(view -> searchInput.setText(""));
        nextButton.setOnClickListener(view -> navigateNextResult());
        previousButton.setOnClickListener(view -> navigatePreviousResult());
        updateClearIcon();
        updateSearchStatus();
    }

    public void submitQuery(String query, boolean showBar) {
        if (showBar) {
            showSearchBar();
        }
        searchInput.setText(query);
        searchInput.selectAll();
        performSearch(query, -1);
    }

    /** Abre la búsqueda interna seleccionando la coincidencia que originó el resultado global. */
    public void submitQueryAtOffset(String query, boolean showBar, int preferredOffset) {
        if (showBar) {
            showSearchBar();
        }
        searchInput.setText(query);
        searchInput.selectAll();
        performSearch(query, preferredOffset);
    }

    public boolean hasActiveQuery() {
        return !searchInput.getText().toString().isEmpty();
    }

    /** Re-ejecuta la búsqueda actual sobre el texto reconstruido (ej. tras cambiar un resaltado). */
    public void refreshSearch() {
        performSearch(searchInput.getText().toString(), -1);
    }

    private void showSearchBar() {
        searchBarContainer.setVisibility(View.VISIBLE);
        searchFab.setVisibility(View.GONE);
        searchInput.requestFocus();
        Editable queryText = searchInput.getText();
        if (queryText != null) {
            searchInput.setSelection(0, queryText.length());
        }
        showKeyboard();
    }

    private void hideSearchBar() {
        searchInput.setText("");
        searchBarContainer.setVisibility(View.GONE);
        searchFab.setVisibility(View.VISIBLE);
        hideKeyboard();
        resetSearchHighlight();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
    }

    private void resetSearchHighlight() {
        textView.setText(textProvider.getFormattedText());
        searchPositions.clear();
        currentSearchIndex = 0;
        updateSearchStatus();
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateClearIcon();
                performSearch(s.toString(), -1);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void navigateNextResult() {
        if (!searchPositions.isEmpty()) {
            currentSearchIndex = (currentSearchIndex + 1) % searchPositions.size();
            scrollToPosition(searchPositions.get(currentSearchIndex));
        }
    }

    private void navigatePreviousResult() {
        if (!searchPositions.isEmpty()) {
            currentSearchIndex = (currentSearchIndex - 1 + searchPositions.size()) % searchPositions.size();
            scrollToPosition(searchPositions.get(currentSearchIndex));
        }
    }

    private void updateClearIcon() {
        boolean hasQuery = !searchInput.getText().toString().isEmpty();
        clearButton.setVisibility(hasQuery ? View.VISIBLE : View.INVISIBLE);
    }

    private void performSearch(String query, int preferredOffset) {
        SpannableString spannable = textProvider.getFormattedText();
        clearPreviousSearchHighlights(spannable);

        if (!query.isEmpty()) {
            applySearchHighlight(spannable, query);
        }

        if (preferredOffset >= 0 && !searchPositions.isEmpty()) {
            currentSearchIndex = nearestSearchPosition(preferredOffset);
        }

        textView.setText(spannable);
        if (!searchPositions.isEmpty()) {
            scrollToPosition(searchPositions.get(currentSearchIndex));
        }
        updateSearchStatus();
    }

    private void clearPreviousSearchHighlights(SpannableString spannable) {
        BackgroundColorSpan[] spans = spannable.getSpans(0, spannable.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            if (span instanceof HighlightController.UserHighlightSpan) {
                continue; // los resaltados del usuario no son resultados de búsqueda
            }
            spannable.removeSpan(span);
        }
        searchPositions.clear();
        currentSearchIndex = 0;
        updateSearchStatus();
    }

    private void applySearchHighlight(SpannableString spannable, String query) {
        for (SearchTextNormalizer.Range range
                : SearchTextNormalizer.findAll(spannable.toString(), query)) {
            spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), range.start, range.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchPositions.add(range.start);
        }

        if (!searchPositions.isEmpty()) {
            currentSearchIndex = 0;
        }
    }

    private int nearestSearchPosition(int preferredOffset) {
        int bestIndex = 0;
        int bestDistance = Integer.MAX_VALUE;
        for (int index = 0; index < searchPositions.size(); index++) {
            int distance = Math.abs(searchPositions.get(index) - preferredOffset);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    private void scrollToPosition(int charIndex) {
        textView.post(() -> {
            if (textView.getLayout() != null) {
                int line = textView.getLayout().getLineForOffset(charIndex);
                int y = textView.getLayout().getLineTop(line);
                scrollView.smoothScrollTo(0, textView.getTop() + y);
            }
        });
    }


    private void updateSearchStatus() {
        int totalResults = searchPositions.size();
        int currentResult = totalResults == 0 ? 0 : currentSearchIndex + 1;
        searchCounterView.setText(String.format(Locale.getDefault(), "%d/%d", currentResult, totalResults));

        boolean hasQuery = !searchInput.getText().toString().trim().isEmpty();
        noResultsView.setVisibility(hasQuery && totalResults == 0 ? View.VISIBLE : View.GONE);
    }
}
