package com.jobalistudios.codigoprocesalcivilpe.contenido;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contenido del código segmentado por artículo, cargado desde assets/articles.json
 * (generado por tools/generate_articles.ps1 a partir de strings.xml). La carga es
 * todo-o-nada: ante cualquier problema con el asset se usan los strings compilados
 * como respaldo, así la app nunca queda sin texto ni con bloques a medias.
 */
public final class ArticleRepository {

    private static final String TAG = "ArticleRepository";
    private static final String ASSET_NAME = "articles.json";
    private static final int SUPPORTED_VERSION = 1;

    private static volatile Map<String, ArticleBlock> blocksByKey;
    private static volatile Map<String, List<Location>> locationsByNumber;
    private static final Pattern NUMBER_WITH_SEPARATED_SUFFIX =
            Pattern.compile("^(\\d+)\\s+([A-Z])$");

    private ArticleRepository() {
    }

    /** Bloque por nombre de recurso string, o null si el JSON no lo tiene. */
    @Nullable
    public static ArticleBlock getBlock(Context context, String blockKey) {
        return getBlocks(context).get(blockKey);
    }

    /** Texto completo del bloque; cae al string compilado si el JSON no está disponible. */
    @NonNull
    public static String getContentText(Context context, @StringRes int textRes) {
        String key = context.getResources().getResourceEntryName(textRes);
        ArticleBlock block = getBlock(context, key);
        return block != null ? block.fullText() : context.getString(textRes);
    }

    /** Todas las ubicaciones del artículo con ese número canónico (ej. "647", "647-A"). */
    @NonNull
    public static List<Location> findArticle(Context context, String number) {
        String wanted = normalizeArticleNumber(number);
        if (wanted.isEmpty()) {
            return Collections.emptyList();
        }
        List<Location> locations = getLocationsByNumber(context).get(wanted);
        return locations == null ? Collections.emptyList() : locations;
    }

    /** Normaliza solo para búsquedas; nunca cambia el número que se muestra al usuario. */
    @NonNull
    public static String normalizeArticleNumber(@Nullable String number) {
        if (number == null) {
            return "";
        }
        String normalized = number.trim().toUpperCase(Locale.ROOT)
                .replaceAll("\\s*-\\s*", "-");
        Matcher separatedSuffix = NUMBER_WITH_SEPARATED_SUFFIX.matcher(normalized);
        return separatedSuffix.matches()
                ? separatedSuffix.group(1) + "-" + separatedSuffix.group(2)
                : normalized;
    }

    @NonNull
    public static Map<String, ArticleBlock> getBlocks(Context context) {
        Map<String, ArticleBlock> cached = blocksByKey;
        if (cached == null) {
            synchronized (ArticleRepository.class) {
                cached = blocksByKey;
                if (cached == null) {
                    cached = load(context.getApplicationContext());
                    blocksByKey = cached;
                }
            }
        }
        return cached;
    }

    @NonNull
    private static Map<String, List<Location>> getLocationsByNumber(Context context) {
        Map<String, List<Location>> cached = locationsByNumber;
        if (cached == null) {
            synchronized (ArticleRepository.class) {
                cached = locationsByNumber;
                if (cached == null) {
                    Map<String, List<Location>> mutable = new LinkedHashMap<>();
                    for (ArticleBlock block : getBlocks(context).values()) {
                        for (Article article : block.articles) {
                            String key = normalizeArticleNumber(article.number);
                            mutable.computeIfAbsent(key, ignored -> new ArrayList<>())
                                    .add(new Location(block, article));
                        }
                    }
                    Map<String, List<Location>> immutable = new LinkedHashMap<>();
                    for (Map.Entry<String, List<Location>> entry : mutable.entrySet()) {
                        immutable.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
                    }
                    cached = Collections.unmodifiableMap(immutable);
                    locationsByNumber = cached;
                }
            }
        }
        return cached;
    }

    private static Map<String, ArticleBlock> load(Context context) {
        try (Reader reader = new InputStreamReader(
                context.getAssets().open(ASSET_NAME), StandardCharsets.UTF_8)) {
            JsonFile file = new Gson().fromJson(reader, JsonFile.class);
            if (file == null || file.blocks == null) {
                throw new IllegalStateException("JSON vacío o sin bloques");
            }
            if (file.version != SUPPORTED_VERSION) {
                throw new IllegalStateException("Versión de formato no soportada: " + file.version);
            }
            Map<String, ArticleBlock> byKey = new LinkedHashMap<>();
            for (JsonBlock block : file.blocks) {
                if (block == null || block.key == null) {
                    throw new IllegalStateException("Bloque sin clave");
                }
                String preamble = block.preamble == null ? "" : block.preamble;
                List<Article> articles = new ArrayList<>();
                int offset = preamble.length();
                if (block.articles != null) {
                    for (JsonArticle article : block.articles) {
                        if (article == null || article.number == null || article.text == null) {
                            throw new IllegalStateException("Artículo malformado en " + block.key);
                        }
                        articles.add(new Article(article.number,
                                article.title == null ? "" : article.title,
                                article.text, offset));
                        offset += article.text.length();
                    }
                }
                byKey.put(block.key, new ArticleBlock(block.key, preamble,
                        Collections.unmodifiableList(articles)));
            }
            return Collections.unmodifiableMap(byKey);
        } catch (Exception e) {
            Log.e(TAG, "No se pudo cargar " + ASSET_NAME + "; se usarán los strings compilados", e);
            return Collections.emptyMap();
        }
    }

    /** Un artículo localizado dentro de su bloque. */
    public static final class Location {
        public final ArticleBlock block;
        public final Article article;

        Location(ArticleBlock block, Article article) {
            this.block = block;
            this.article = article;
        }
    }

    // DTO que Gson rellena por reflexión (ver reglas de conservación en proguard-rules.pro).
    private static final class JsonFile {
        @SerializedName("version")
        int version;
        @SerializedName("blocks")
        List<JsonBlock> blocks;
    }

    private static final class JsonBlock {
        @SerializedName("key")
        String key;
        @SerializedName("preamble")
        String preamble;
        @SerializedName("articles")
        List<JsonArticle> articles;
    }

    private static final class JsonArticle {
        @SerializedName("number")
        String number;
        @SerializedName("title")
        String title;
        @SerializedName("text")
        String text;
    }
}
