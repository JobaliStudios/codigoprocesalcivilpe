package com.jobalistudios.codigoprocesalcivilpe.historial;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.LongSupplier;

/** Mantiene en SharedPreferences los cinco últimos artículos distintos consultados. */
public final class ReadingHistoryManager {

    public static final String PREFS_NAME = "reading_history";
    static final String KEY_RECENT_ARTICLES = "recent_articles";
    public static final int MAX_RECENT_ARTICLES = 5;

    private static final String JSON_NUMBER = "number";
    private static final String JSON_TITLE = "title";
    private static final String JSON_LAST_VIEWED_AT = "lastViewedAt";

    private final SharedPreferences preferences;
    private final LongSupplier clock;

    public ReadingHistoryManager(@NonNull Context context) {
        this(context, System::currentTimeMillis);
    }

    ReadingHistoryManager(@NonNull Context context, @NonNull LongSupplier clock) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.clock = clock;
    }

    public void recordArticle(@Nullable String number, @Nullable String title) {
        String canonicalNumber = canonicalNumber(number);
        if (canonicalNumber == null) {
            return;
        }

        List<RecentArticle> recent = new ArrayList<>(readRecentArticles());
        recent.removeIf(article -> article.getNumber().equals(canonicalNumber));
        recent.add(0, new RecentArticle(canonicalNumber, safeTitle(title), clock.getAsLong()));
        if (recent.size() > MAX_RECENT_ARTICLES) {
            recent = new ArrayList<>(recent.subList(0, MAX_RECENT_ARTICLES));
        }
        writeRecentArticles(recent);
    }

    @Nullable
    public RecentArticle getLastArticle() {
        List<RecentArticle> recent = getRecentArticles();
        return recent.isEmpty() ? null : recent.get(0);
    }

    @NonNull
    public List<RecentArticle> getRecentArticles() {
        return Collections.unmodifiableList(new ArrayList<>(readRecentArticles()));
    }

    public void removeArticle(@Nullable String number) {
        String canonicalNumber = canonicalNumber(number);
        if (canonicalNumber == null) {
            return;
        }
        List<RecentArticle> recent = new ArrayList<>(readRecentArticles());
        if (recent.removeIf(article -> article.getNumber().equals(canonicalNumber))) {
            writeRecentArticles(recent);
        }
    }

    public void clearHistory() {
        preferences.edit().clear().apply();
    }

    private List<RecentArticle> readRecentArticles() {
        String raw = preferences.getString(KEY_RECENT_ARTICLES, "");
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }

        List<RecentArticle> result = new ArrayList<>();
        Set<String> numbers = new HashSet<>();
        boolean needsRepair = false;
        try {
            JSONArray array = new JSONArray(raw);
            for (int index = 0; index < array.length(); index++) {
                JSONObject stored = array.optJSONObject(index);
                if (stored == null) {
                    needsRepair = true;
                    continue;
                }
                String number = canonicalNumber(stored.optString(JSON_NUMBER, null));
                long lastViewedAt = stored.optLong(JSON_LAST_VIEWED_AT, -1L);
                if (number == null || lastViewedAt < 0L || !numbers.add(number)) {
                    needsRepair = true;
                    continue;
                }
                if (result.size() == MAX_RECENT_ARTICLES) {
                    needsRepair = true;
                    continue;
                }
                result.add(new RecentArticle(number,
                        safeTitle(stored.optString(JSON_TITLE, "")), lastViewedAt));
            }
        } catch (JSONException exception) {
            preferences.edit().remove(KEY_RECENT_ARTICLES).apply();
            return Collections.emptyList();
        }

        if (needsRepair) {
            writeRecentArticles(result);
        }
        return result;
    }

    private void writeRecentArticles(List<RecentArticle> recent) {
        JSONArray array = new JSONArray();
        for (RecentArticle article : recent) {
            JSONObject stored = new JSONObject();
            try {
                stored.put(JSON_NUMBER, article.getNumber());
                stored.put(JSON_TITLE, article.getTitle());
                stored.put(JSON_LAST_VIEWED_AT, article.getLastViewedAt());
                array.put(stored);
            } catch (JSONException ignored) {
                // Los valores son primitivos no nulos; se omite sólo una entrada inesperadamente inválida.
            }
        }
        preferences.edit().putString(KEY_RECENT_ARTICLES, array.toString()).apply();
    }

    @Nullable
    private static String canonicalNumber(@Nullable String number) {
        if (number == null) {
            return null;
        }
        String canonical = number.trim().toUpperCase(Locale.ROOT);
        return canonical.matches("\\d{1,4}(?:-[A-Z])?") ? canonical : null;
    }

    private static String safeTitle(@Nullable String title) {
        return title == null ? "" : title.trim();
    }
}
