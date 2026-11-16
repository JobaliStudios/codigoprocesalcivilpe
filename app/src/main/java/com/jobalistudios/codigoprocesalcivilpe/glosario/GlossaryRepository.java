package com.jobalistudios.codigoprocesalcivilpe.glosario;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Glosario editorial LOCAL. No se descargan definiciones ni se mezclan con el texto legal.
 * El asset de producción inicia con terms: []; los términos necesitan aprobación humana.
 */
public final class GlossaryRepository {
    private static final String TAG = "GlossaryRepository";
    private static volatile List<Term> cachedTerms;
    private GlossaryRepository() {}

    @NonNull
    public static List<Term> getTerms(@NonNull Context context) {
        List<Term> terms = cachedTerms;
        if (terms != null) return terms;
        synchronized (GlossaryRepository.class) {
            if (cachedTerms == null) {
                try (Reader reader = new InputStreamReader(
                        context.getApplicationContext().getAssets().open("glossary.json"),
                        StandardCharsets.UTF_8)) {
                    cachedTerms = parse(reader);
                } catch (Exception error) {
                    Log.w(TAG, "Glosario no disponible; se desactiva sin alterar el lector", error);
                    cachedTerms = Collections.emptyList();
                }
            }
            return cachedTerms;
        }
    }

    @Nullable
    public static Term find(@NonNull Context context, @Nullable String id) {
        if (id == null) return null;
        for (Term term : getTerms(context)) {
            if (id.equals(term.id)) return term;
        }
        return null;
    }

    /** Visible para las pruebas: no modifica el cache real. */
    @NonNull
    static List<Term> parseJson(@NonNull String json) {
        return parse(new StringReader(json));
    }

    @NonNull
    private static List<Term> parse(@NonNull Reader source) {
        try {
            GlossaryFile file = new Gson().fromJson(source, GlossaryFile.class);
            if (file == null || file.version != 1 || file.terms == null) {
                return Collections.emptyList();
            }
            List<Term> result = new ArrayList<>();
            Set<String> ids = new HashSet<>();
            for (Term term : file.terms) {
                if (term == null || blank(term.id) || blank(term.term)
                        || blank(term.definition) || blank(term.source)
                        || blank(term.reviewedBy) || blank(term.reviewedAt)
                        || term.aliases == null || term.articles == null
                        || term.related == null || !ids.add(term.id)
                        || invalidStrings(term.aliases) || invalidStrings(term.articles)
                        || invalidStrings(term.related)) {
                    return Collections.emptyList();
                }
                result.add(term);
            }
            return Collections.unmodifiableList(result);
        } catch (JsonParseException | IllegalArgumentException error) {
            return Collections.emptyList();
        }
    }

    private static boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean invalidStrings(List<String> values) {
        for (String value : values) {
            if (blank(value)) return true;
        }
        return false;
    }

    /** DTO serializado mediante Gson; conservar con reglas R8. */
    private static final class GlossaryFile {
        @SerializedName("version") int version;
        @SerializedName("terms") List<Term> terms;
    }

    /** Solo metadatos editoriales. Ningún campo modifica el texto del CPC. */
    public static final class Term {
        @SerializedName("id") String id;
        @SerializedName("term") String term;
        @SerializedName("aliases") List<String> aliases;
        @SerializedName("definition") String definition;
        @SerializedName("articles") List<String> articles;
        @SerializedName("related") List<String> related;
        @SerializedName("source") String source;
        @SerializedName("reviewedBy") String reviewedBy;
        @SerializedName("reviewedAt") String reviewedAt;

        @NonNull public String getId() { return id; }
        @NonNull public String getTerm() { return term; }
        @NonNull public List<String> getAliases() { return Collections.unmodifiableList(aliases); }
        @NonNull public String getDefinition() { return definition; }
        @NonNull public List<String> getArticles() { return Collections.unmodifiableList(articles); }
        @NonNull public List<String> getRelated() { return Collections.unmodifiableList(related); }
        @NonNull public String getSource() { return source; }
        @NonNull public String getReviewedBy() { return reviewedBy; }
        @NonNull public String getReviewedAt() { return reviewedAt; }
    }
}
