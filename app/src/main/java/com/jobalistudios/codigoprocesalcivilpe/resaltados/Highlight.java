package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Resaltado hecho por el usuario sobre un bloque de contenido, con nota opcional.
 * Guarda el fragmento resaltado (snippet) para poder reubicarlo si el texto legal
 * cambia de posición en una actualización de la app.
 */
public class Highlight {

    private final String id;
    private final int start;
    private final int end;
    private final String colorTag;
    @Nullable
    private final String note;
    private final String snippet;
    private final long createdAt;

    public Highlight(String id, int start, int end, String colorTag, @Nullable String note,
                     String snippet, long createdAt) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.colorTag = colorTag;
        this.note = note == null || note.isEmpty() ? null : note;
        this.snippet = snippet;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getColorTag() {
        return colorTag;
    }

    @Nullable
    public String getNote() {
        return note;
    }

    public boolean hasNote() {
        return note != null;
    }

    public String getSnippet() {
        return snippet;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("start", start);
        json.put("end", end);
        json.put("colorTag", colorTag);
        json.put("note", note == null ? "" : note);
        json.put("snippet", snippet);
        json.put("createdAt", createdAt);
        return json;
    }

    @Nullable
    public static Highlight fromJson(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String id = json.optString("id", "");
        if (id.isEmpty()) {
            return null;
        }
        return new Highlight(
                id,
                json.optInt("start", -1),
                json.optInt("end", -1),
                json.optString("colorTag", "yellow"),
                json.optString("note", ""),
                json.optString("snippet", ""),
                json.optLong("createdAt", 0L)
        );
    }
}
