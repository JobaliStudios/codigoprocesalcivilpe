package com.jobalistudios.codigoprocesalcivilpe.contenido;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        String wanted = number.trim().toUpperCase(Locale.ROOT);
        List<Location> locations = new ArrayList<>();
        for (ArticleBlock block : getBlocks(context).values()) {
            for (Article article : block.articles) {
                if (article.number.equals(wanted)) {
                    locations.add(new Location(block, article));
                }
            }
        }
        return locations;
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

    // DTO que Gson rellena por reflexión (ver regla -keepclassmembers en proguard-rules.pro).
    private static final class JsonFile {
        int version;
        List<JsonBlock> blocks;
    }

    private static final class JsonBlock {
        String key;
        String preamble;
        List<JsonArticle> articles;
    }

    private static final class JsonArticle {
        String number;
        String title;
        String text;
    }
}
