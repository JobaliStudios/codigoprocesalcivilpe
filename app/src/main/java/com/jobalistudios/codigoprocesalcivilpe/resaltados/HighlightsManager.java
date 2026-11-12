package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de resaltados y notas. Cada bloque de contenido se identifica por el
 * nombre de su recurso string (ej. "seccionprimeratit1txt"), que es estable entre
 * versiones de la app, a diferencia del ID numérico del recurso.
 */
public class HighlightsManager {

    private static final String PREFS_NAME = "codigoprocesalcivil_highlights";
    private static final String NOTE_CIPHERTEXT = "noteCiphertext";
    private static final String NOTE_IV = "noteIv";
    private static final String NOTE_VERSION = "noteVersion";
    private static final int CURRENT_NOTE_VERSION = 1;

    private final SharedPreferences sharedPreferences;
    private final NoteCipher noteCipher;

    public HighlightsManager(Context context) {
        this(context, new AndroidKeystoreNoteCipher());
    }

    HighlightsManager(Context context, NoteCipher noteCipher) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.noteCipher = noteCipher;
    }

    /**
     * Lee los resaltados utilizables sin arriesgar el contenido cifrado que no se
     * pueda descifrar con la clave actual (o con una futura versión del formato).
     */
    public List<Highlight> getForBlock(String blockKey) {
        return readBlock(blockKey).highlights;
    }

    private ReadResult readBlock(String blockKey) {
        String raw = sharedPreferences.getString(blockKey, "[]");
        final JSONArray array;
        try {
            array = new JSONArray(raw);
        } catch (JSONException exception) {
            // Los datos originales permanecen en SharedPreferences para recuperación.
            return new ReadResult(new ArrayList<>(), false);
        }

        List<Highlight> result = new ArrayList<>();
        boolean containsLegacyPlaintext = false;
        boolean safeToWrite = true;
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json == null) {
                safeToWrite = false;
                continue;
            }
            String note = null;
            if (json.has(NOTE_CIPHERTEXT)) {
                try {
                    note = decryptNote(json);
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    // Nunca serializar de nuevo un registro que perdió acceso a su nota.
                    safeToWrite = false;
                }
            } else if (json.has("note")) {
                note = json.optString("note", "");
                containsLegacyPlaintext = true;
            }
            Highlight highlight = Highlight.fromJson(json, note);
            if (highlight != null) {
                result.add(highlight);
            } else {
                safeToWrite = false;
            }
        }

        // No migrar notas antiguas si eso borraría notas cifradas ilegibles.
        if (containsLegacyPlaintext && safeToWrite) {
            save(blockKey, result);
        }
        return new ReadResult(result, safeToWrite);
    }

    private static final class ReadResult {
        final List<Highlight> highlights;
        final boolean safeToWrite;

        ReadResult(List<Highlight> highlights, boolean safeToWrite) {
            this.highlights = highlights;
            this.safeToWrite = safeToWrite;
        }
    }

    public void add(String blockKey, Highlight highlight) {
        ReadResult stored = readBlock(blockKey);
        if (!stored.safeToWrite) {
            return;
        }
        stored.highlights.add(highlight);
        save(blockKey, stored.highlights);
    }

    /** Reemplaza el resaltado con el mismo id (para editar nota o color). */
    public void update(String blockKey, Highlight highlight) {
        ReadResult stored = readBlock(blockKey);
        if (!stored.safeToWrite) {
            return;
        }
        List<Highlight> highlights = stored.highlights;
        for (int i = 0; i < highlights.size(); i++) {
            if (highlights.get(i).getId().equals(highlight.getId())) {
                highlights.set(i, highlight);
                save(blockKey, highlights);
                return;
            }
        }
    }

    public void remove(String blockKey, String highlightId) {
        ReadResult stored = readBlock(blockKey);
        if (!stored.safeToWrite) {
            return;
        }
        List<Highlight> highlights = stored.highlights;
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

    /** Borra en una sola operación todos los resaltados y notas del usuario. */
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
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

    private void save(String blockKey, List<Highlight> highlights) {
        JSONArray array = new JSONArray();
        try {
            for (Highlight highlight : highlights) {
                JSONObject json = highlight.toJson();
                if (highlight.hasNote()) {
                    NoteCipher.EncryptedNote encrypted = noteCipher.encrypt(
                            highlight.getId(), highlight.getNote());
                    json.put(NOTE_CIPHERTEXT, encrypted.ciphertext);
                    json.put(NOTE_IV, encrypted.iv);
                    json.put(NOTE_VERSION, CURRENT_NOTE_VERSION);
                }
                array.put(json);
            }
        } catch (JSONException | GeneralSecurityException ignored) {
            // Never fall back to plaintext or overwrite valid data if encryption fails.
            return;
        }
        if (array.length() == 0) {
            sharedPreferences.edit().remove(blockKey).apply();
        } else {
            sharedPreferences.edit().putString(blockKey, array.toString()).apply();
        }
    }

    private String decryptNote(JSONObject json) throws GeneralSecurityException {
        if (json.optInt(NOTE_VERSION, -1) != CURRENT_NOTE_VERSION) {
            throw new GeneralSecurityException("Versión de nota cifrada no compatible");
        }
        String highlightId = json.optString("id", "");
        String ciphertext = json.optString(NOTE_CIPHERTEXT, "");
        String iv = json.optString(NOTE_IV, "");
        if (highlightId.isEmpty() || ciphertext.isEmpty() || iv.isEmpty()) {
            throw new GeneralSecurityException("Nota cifrada incompleta");
        }
        return noteCipher.decrypt(highlightId, new NoteCipher.EncryptedNote(ciphertext, iv));
    }
}
