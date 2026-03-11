package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

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

        searchFab.setOnClickListener(view -> showSearchBar());
        closeButton.setOnClickListener(view -> hideSearchBar());
        searchInput.addTextChangedListener(createTextWatcher());
        searchInput.setOnTouchListener(createTouchListener());
        nextButton.setOnClickListener(view -> navigateNextResult());
        previousButton.setOnClickListener(view -> navigatePreviousResult());
    }

    public void submitQuery(String query, boolean showBar) {
        if (showBar) {
            showSearchBar();
        }
        searchInput.setText(query);
        searchInput.setSelection(searchInput.getText().length());
        performSearch(query);
    }

    private void showSearchBar() {
        searchBarContainer.setVisibility(View.VISIBLE);
        searchFab.setVisibility(View.GONE);
        searchInput.requestFocus();
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
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateClearIcon();
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private View.OnTouchListener createTouchListener() {
        return (v, event) -> {
            if (searchInput.getCompoundDrawables()[2] != null
                    && event.getAction() == MotionEvent.ACTION_UP
                    && event.getRawX() >= (searchInput.getRight()
                    - searchInput.getCompoundDrawables()[2].getBounds().width()
                    - searchInput.getPaddingEnd())) {
                searchInput.setText("");
                v.performClick();
                return true;
            }
            return false;
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
        if (!searchInput.getText().toString().isEmpty()) {
            searchInput.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_search_24,
                    0,
                    R.drawable.baseline_close_24,
                    0
            );
        } else {
            searchInput.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_search_24,
                    0,
                    0,
                    0
            );
        }
    }

    private void performSearch(String query) {
        SpannableString spannable = textProvider.getFormattedText();
        clearPreviousSearchHighlights(spannable);

        if (!query.isEmpty()) {
            applySearchHighlight(spannable, query);
        }

        textView.setText(spannable);
    }

    private void clearPreviousSearchHighlights(SpannableString spannable) {
        BackgroundColorSpan[] spans = spannable.getSpans(0, spannable.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            spannable.removeSpan(span);
        }
        searchPositions.clear();
        currentSearchIndex = 0;
    }

    private void applySearchHighlight(SpannableString spannable, String query) {
        String normalizedContent = normalizeText(spannable.toString());
        String normalizedQuery = normalizeText(query);
        int index = normalizedContent.indexOf(normalizedQuery);

        while (index >= 0) {
            int end = index + normalizedQuery.length();
            spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchPositions.add(index);
            index = normalizedContent.indexOf(normalizedQuery, end);
        }
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

    private String normalizeText(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }
}
