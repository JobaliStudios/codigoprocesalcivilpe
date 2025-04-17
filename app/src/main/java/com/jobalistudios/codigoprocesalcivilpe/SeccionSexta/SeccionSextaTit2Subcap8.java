package com.jobalistudios.codigoprocesalcivilpe.SeccionSexta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobalistudios.codigoprocesalcivilpe.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeccionSextaTit2Subcap8 extends AppCompatActivity {

    private TextView textView2;
    private EditText edtBusqueda;
    private View searchBarContainer;
    private FloatingActionButton fabBuscar;
    private ScrollView scrollViewContent;

    // Lista para almacenar las posiciones (índices) de cada ocurrencia encontrada
    private final List<Integer> searchPositions = new ArrayList<>();
    private int currentSearchIndex = 0;

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_sexta_tit2_subcap8);
        // Inicialización de vistas
        textView2 = findViewById(R.id.textView2);
        edtBusqueda = findViewById(R.id.edtBusqueda);
        searchBarContainer = findViewById(R.id.searchBarContainer);
        fabBuscar = findViewById(R.id.fabBuscar);
        ImageButton btnSiguiente = findViewById(R.id.btnSiguiente);
        ImageButton btnAnterior = findViewById(R.id.btnAnterior);
        ImageButton btnCerrarBusqueda = findViewById(R.id.btnCerrarBusqueda);
        scrollViewContent = findViewById(R.id.scrollViewContent);

        // Configurar texto con formato
        setupTextFormatting();

        // Configurar listeners
        setupListeners(btnSiguiente, btnAnterior, btnCerrarBusqueda);
    }

    private void setupTextFormatting() {
        textView2.setText(getFormattedText());
    }

    private void setupListeners(ImageButton btnSiguiente, ImageButton btnAnterior, ImageButton btnCerrarBusqueda) {
        fabBuscar.setOnClickListener(view -> showSearchBar());
        btnCerrarBusqueda.setOnClickListener(view -> hideSearchBar());
        edtBusqueda.addTextChangedListener(createTextWatcher());
        edtBusqueda.setOnTouchListener(createTouchListener());
        btnSiguiente.setOnClickListener(view -> navigateNextResult());
        btnAnterior.setOnClickListener(view -> navigatePreviousResult());
    }

    private void showSearchBar() {
        searchBarContainer.setVisibility(View.VISIBLE);
        fabBuscar.setVisibility(View.GONE);
        edtBusqueda.requestFocus();
        showKeyboard();
    }

    private void hideSearchBar() {
        edtBusqueda.setText("");
        searchBarContainer.setVisibility(View.GONE);
        fabBuscar.setVisibility(View.VISIBLE);
        hideKeyboard();
        resetSearchHighlight();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtBusqueda, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtBusqueda.getWindowToken(), 0);
    }

    private void resetSearchHighlight() {
        // Reconstruye el Spannable con el formato original
        SpannableString spannable = new SpannableString(textView2.getText());
        applyArticleTitleFormatting(spannable);
        textView2.setText(spannable);

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
            if (edtBusqueda.getCompoundDrawables()[2] != null &&
                    event.getAction() == MotionEvent.ACTION_UP &&
                    event.getRawX() >= (edtBusqueda.getRight() -
                            edtBusqueda.getCompoundDrawables()[2].getBounds().width() -
                            edtBusqueda.getPaddingEnd())) {

                edtBusqueda.setText("");
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

    private void applyArticleTitleFormatting(SpannableString spannable) {
        String fullText = spannable.toString();
        int colorArticulo = ContextCompat.getColor(this, R.color.article_title_color);
        int colorModificado = ContextCompat.getColor(this, R.color.modified_article_color);

        // 1. Formatear TÍTULOS DE ARTÍCULOS en ROJO
        Pattern articlePattern = Pattern.compile("^Artículo\\s+\\d+\\s*.-\\s*[^\\n]+", Pattern.MULTILINE);
        Matcher articleMatcher = articlePattern.matcher(fullText);
        while (articleMatcher.find()) {
            int start = articleMatcher.start();
            int end = articleMatcher.end();
            spannable.setSpan(new ForegroundColorSpan(colorArticulo), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 2. Formatear BLOQUES DE MODIFICACIONES (cabecera + listas) en AZUL
        Pattern modificationBlockPattern = Pattern.compile(
                "(^\\*\\s*Artículo modificado[^\\n]*(\\n\\s*\\d+\\..*)*)",
                Pattern.MULTILINE
        );
        Matcher blockMatcher = modificationBlockPattern.matcher(fullText);
        while (blockMatcher.find()) {
            int start = blockMatcher.start();
            int end = blockMatcher.end();
            // Aplicar azul e itálica a TODO el bloque (cabecera + listas)
            spannable.setSpan(new ForegroundColorSpan(colorModificado), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 3. NO formatear las listas fuera de bloques de modificación (quedan en negro)
    }

    private void updateClearIcon() {
        if (!edtBusqueda.getText().toString().isEmpty()) {
            edtBusqueda.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_search_24,
                    0,
                    R.drawable.baseline_close_24,
                    0
            );
        } else {
            edtBusqueda.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_search_24,
                    0,
                    0,
                    0
            );
        }
    }

    private void performSearch(String query) {
        // Usar el texto original formateado como base
        SpannableString spannable = new SpannableString(getString(R.string.seccionsextatit2subcap8));
        applyArticleTitleFormatting(spannable); // <-- Aplicar formato base primero

        clearPreviousSearchHighlights(spannable);

        if (!query.isEmpty()) {
            applySearchHighlight(spannable, query);
        }

        textView2.setText(spannable);
    }

    private SpannableString getFormattedText() {
        SpannableString spannable = new SpannableString(getString(R.string.seccionsextatit2subcap8));
        applyArticleTitleFormatting(spannable);
        return spannable;
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
        textView2.post(() -> {
            if (textView2.getLayout() != null) {
                int line = textView2.getLayout().getLineForOffset(charIndex);
                int y = textView2.getLayout().getLineTop(line);
                scrollViewContent.smoothScrollTo(0, textView2.getTop() + y);
            }
        });
    }

    private String normalizeText(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }
}