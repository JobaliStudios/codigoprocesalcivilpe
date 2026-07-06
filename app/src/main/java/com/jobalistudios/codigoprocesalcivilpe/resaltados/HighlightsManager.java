package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de resaltados y notas. Cada bloque de contenido se identifica por el
 * nombre de su recurso string (ej. "seccionprimeratit1txt"), que es estable entre
 * versiones de la app, a diferencia del ID numérico del recurso.
 */
public class HighlightsManager {

    private static final String PREFS_NAME = "codigoprocesalcivil_highlights";

    private final SharedPreferences sharedPreferences;

    public HighlightsManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Highlight> getForBlock(String blockKey) {
        List<Highlight> result = new ArrayList<>();
        JSONArray array = getArray(blockKey);
        for (int i = 0; i < array.length(); i++) {
            Highlight highlight = Highlight.fromJson(array.optJSONObject(i));
            if (highlight != null) {
                result.add(highlight);
            }
        }
        return result;
    }

    public void add(String blockKey, Highlight highlight) {
        List<Highlight> highlights = getForBlock(blockKey);
        highlights.add(highlight);
        save(blockKey, highlights);
    }

    /** Reemplaza el resaltado con el mismo id (para editar nota o color). */
    public void update(String blockKey, Highlight highlight) {
        List<Highlight> highlights = getForBlock(blockKey);
        for (int i = 0; i < highlights.size(); i++) {
            if (highlights.get(i).getId().equals(highlight.getId())) {
                highlights.set(i, highlight);
                save(blockKey, highlights);
                return;
            }
        }
    }

    public void remove(String blockKey, String highlightId) {
        List<Highlight> highlights = getForBlock(blockKey);
        boolean removed = false;
        for (int i = highlights.size() - 1; i >= 0; i--) {
            if (highlights.get(i).getId().equals(highlightId)) {
                highlights.remove(i);
                removed = true;
            }
        }
        if (removed) {
            save(blockKey, highlights);
        }
    }

    @Nullable
    public Highlight find(String blockKey, String highlightId) {
        for (Highlight highlight : getForBlock(blockKey)) {
            if (highlight.getId().equals(highlightId)) {
                return highlight;
            }
        }
        return null;
    }

    /**
     * Posición vigente del resaltado en el contenido actual: usa los offsets guardados si
     * el fragmento sigue ahí; si el texto cambió, intenta reubicarlo por su contenido.
     * Devuelve null si ya no se puede ubicar (el resaltado no se muestra pero no se borra).
     */
    @Nullable
    public static int[] resolveRange(Highlight highlight, String content) {
        int start = highlight.getStart();
        int end = highlight.getEnd();
        String snippet = highlight.getSnippet();
        if (start >= 0 && end > start && end <= content.length()
                && content.substring(start, end).equals(snippet)) {
            return new int[]{start, end};
        }
        if (snippet == null || snippet.isEmpty()) {
            return null;
        }
        int index = content.indexOf(snippet);
        if (index >= 0) {
            return new int[]{index, index + snippet.length()};
        }
        return null;
    }

    private JSONArray getArray(String blockKey) {
        String raw = sharedPreferences.getString(blockKey, "[]");
        try {
            return new JSONArray(raw);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    private void save(String blockKey, List<Highlight> highlights) {
        JSONArray array = new JSONArray();
        try {
            for (Highlight highlight : highlights) {
                array.put(highlight.toJson());
            }
        } catch (JSONException ignored) {
            return;
        }
        if (array.length() == 0) {
            sharedPreferences.edit().remove(blockKey).apply();
        } else {
            sharedPreferences.edit().putString(blockKey, array.toString()).apply();
        }
    }
}
