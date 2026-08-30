package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;

import java.util.List;
import java.util.UUID;

/**
 * Conecta un TextView de contenido con los resaltados persistentes: agrega las acciones
 * "Resaltar" y "Agregar nota" al menú de selección de texto, aplica los resaltados
 * guardados al texto, y abre el detalle (ver/editar/quitar) al tocar un resaltado.
 *
 * La actividad dueña del texto debe llamar a {@link #applyHighlights(Spannable)} cada vez
 * que reconstruya el Spannable (formato base, búsqueda interna, etc.) y pasar un
 * refreshText que vuelva a renderizar el texto completo.
 */
public class HighlightController {

    private static final int MENU_HIGHLIGHT = 1;
    private static final int MENU_NOTE = 2;

    /** Span propio para distinguir los resaltados del usuario de los de búsqueda. */
    public static class UserHighlightSpan extends BackgroundColorSpan {
        public final String highlightId;

        UserHighlightSpan(int color, String highlightId) {
            super(color);
            this.highlightId = highlightId;
        }
    }

    private final Context context;
    private final TextView textView;
    private final String blockKey;
    private final HighlightsManager manager;
    private final ArticleQuickNotes articleQuickNotes;
    private final Runnable refreshText;
    private String selectedColor = "yellow";

    @SuppressLint("ClickableViewAccessibility")
    public HighlightController(Context context, TextView textView, String blockKey, Runnable refreshText) {
        this.context = context;
        this.textView = textView;
        this.blockKey = blockKey;
        this.manager = new HighlightsManager(context);
        this.articleQuickNotes = new ArticleQuickNotes(context, blockKey);
        this.refreshText = refreshText;

        setupSelectionActions();
        setupTapToOpen();
    }

    /** Aplica los resaltados guardados sobre un texto ya formateado. */
    public void applyHighlights(Spannable spannable) {
        for (UserHighlightSpan span : spannable.getSpans(0, spannable.length(), UserHighlightSpan.class)) {
            spannable.removeSpan(span);
        }
        String content = spannable.toString();
        for (Highlight highlight : manager.getForBlock(blockKey)) {
            int[] range = HighlightsManager.resolveRange(highlight, content);
            if (range == null) {
                continue;
            }
            spannable.setSpan(new UserHighlightSpan(colorFor(highlight.getColorTag()), highlight.getId()),
                    range[0], range[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public boolean hasArticleQuickNote(@NonNull Article article) {
        return articleQuickNotes.has(article);
    }

    /** Abre la nota rápida existente o crea una vinculada solo a la línea del encabezado. */
    public void openArticleQuickNote(@NonNull Article article) {
        Highlight existing = articleQuickNotes.get(article);
        if (existing != null) {
            showDetailDialog(existing);
            return;
        }
        showCreateDialog(true, article.offsetInBlock,
                article.offsetInBlock + firstLineLength(article.text), article);
    }

    private void setupSelectionActions() {
        textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, MENU_HIGHLIGHT, 0, R.string.highlight_action_highlight)
                        .setIcon(R.drawable.baseline_highlight_alt_24);
                menu.add(0, MENU_NOTE, 1, R.string.highlight_action_note)
                        .setIcon(R.drawable.baseline_note_24);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() != MENU_HIGHLIGHT && item.getItemId() != MENU_NOTE) {
                    return false;
                }
                int start = Math.min(textView.getSelectionStart(), textView.getSelectionEnd());
                int end = Math.max(textView.getSelectionStart(), textView.getSelectionEnd());
                if (start < 0 || start == end) {
                    return false;
                }
                showCreateDialog(item.getItemId() == MENU_NOTE, start, end, null);
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTapToOpen() {
        GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handleTap(e);
                return false;
            }
        });
        textView.setOnTouchListener((v, event) -> {
            detector.onTouchEvent(event);
            return false;
        });
    }

    private void handleTap(MotionEvent event) {
        if (textView.hasSelection()) {
            return;
        }
        CharSequence text = textView.getText();
        if (!(text instanceof Spanned)) {
            return;
        }
        int offset = textView.getOffsetForPosition(event.getX(), event.getY());
        if (offset < 0) {
            return;
        }
        Spanned spanned = (Spanned) text;
        if (openArticleReferenceAt(spanned, offset, textView)) {
            return;
        }
        UserHighlightSpan[] spans = spanned.getSpans(offset, offset, UserHighlightSpan.class);
        if (spans.length == 0) {
            return;
        }
        Highlight highlight = manager.find(blockKey, spans[0].highlightId);
        if (highlight != null) {
            showDetailDialog(highlight);
        }
    }

    /** Retorna true si el tap fue consumido por un enlace, antes de revisar Highlights. */
    static boolean openArticleReferenceAt(Spanned text, int offset, View widget) {
        if (offset < 0 || offset > text.length()) {
            return false;
        }
        ArticleCrossReferenceSpan[] links = text.getSpans(
                offset,
                Math.min(offset + 1, text.length()),
                ArticleCrossReferenceSpan.class
        );
        if (links.length == 0) {
            return false;
        }
        links[0].onClick(widget);
        return true;
    }

    private void showCreateDialog(
            boolean withNote,
            int start,
            int end,
            @Nullable Article quickNoteArticle
    ) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_note, null);
        EditText etNote = view.findViewById(R.id.etNote);
        if (!withNote) {
            etNote.setVisibility(View.GONE);
            view.findViewById(R.id.notePrivacyWarning).setVisibility(View.GONE);
        }
        selectedColor = "yellow";

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(withNote ? R.string.highlight_action_note : R.string.highlight_dialog_color_title)
                .setNegativeButton(R.string.highlight_cancel, null)
                .setPositiveButton(R.string.highlight_accept, (d, which) -> {
                    String note = withNote ? etNote.getText().toString().trim() : null;
                    if (quickNoteArticle == null) {
                        saveNewHighlight(start, end, selectedColor, note);
                    } else {
                        articleQuickNotes.save(quickNoteArticle, selectedColor, note);
                        refreshText.run();
                    }
                })
                .create();

        // Al elegir color: en resaltado simple confirma de inmediato; con nota solo marca la elección.
        setupColorViews(view, colorTag -> {
            if (!withNote) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
            }
        });
        dialog.show();
    }

    private void showDetailDialog(Highlight highlight) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_note, null);
        EditText etNote = view.findViewById(R.id.etNote);
        etNote.setText(highlight.hasNote() ? highlight.getNote() : "");
        selectedColor = highlight.getColorTag();
        setupColorViews(view, colorTag -> {});

        new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(R.string.highlight_dialog_detail_title)
                .setPositiveButton(R.string.highlight_save, (d, which) -> {
                    manager.update(blockKey, new Highlight(
                            highlight.getId(),
                            highlight.getStart(),
                            highlight.getEnd(),
                            selectedColor,
                            etNote.getText().toString().trim(),
                            highlight.getSnippet(),
                            highlight.getCreatedAt()
                    ));
                    refreshText.run();
                })
                .setNegativeButton(R.string.highlight_cancel, null)
                .setNeutralButton(R.string.highlight_remove, (d, which) -> {
                    manager.remove(blockKey, highlight.getId());
                    refreshText.run();
                })
                .show();
    }

    private interface ColorSelectedListener {
        void onColorSelected(String colorTag);
    }

    private void setupColorViews(View view, ColorSelectedListener listener) {
        int[] colorViews = {R.id.colorYellow, R.id.colorOrange, R.id.colorGreen, R.id.colorBlue};
        for (int id : colorViews) {
            View colorView = view.findViewById(id);
            markSelected(colorView, colorView.getTag().toString().equals(selectedColor));
            colorView.setOnClickListener(v -> {
                selectedColor = v.getTag().toString();
                for (int otherId : colorViews) {
                    View other = view.findViewById(otherId);
                    markSelected(other, other == v);
                }
                listener.onColorSelected(selectedColor);
            });
        }
    }

    private void markSelected(View colorView, boolean selected) {
        colorView.setScaleX(selected ? 1.2f : 1f);
        colorView.setScaleY(selected ? 1.2f : 1f);
        colorView.setAlpha(selected ? 1f : 0.6f);
    }

    private void saveNewHighlight(int start, int end, String colorTag, String note) {
        CharSequence text = textView.getText();
        if (start >= text.length() || end > text.length()) {
            return;
        }
        String snippet = text.subSequence(start, end).toString();
        manager.add(blockKey, new Highlight(
                UUID.randomUUID().toString(),
                start,
                end,
                colorTag,
                note,
                snippet,
                System.currentTimeMillis()
        ));
        refreshText.run();
    }

    private int firstLineLength(@NonNull String text) {
        int newline = text.indexOf('\n');
        return newline >= 0 ? newline : text.length();
    }

    private int colorFor(String tag) {
        switch (tag) {
            case "orange":
                return ContextCompat.getColor(context, R.color.highlight_orange);
            case "green":
                return ContextCompat.getColor(context, R.color.highlight_green);
            case "blue":
                return ContextCompat.getColor(context, R.color.highlight_blue);
            default:
                return ContextCompat.getColor(context, R.color.highlight_yellow);
        }
    }
}
