package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Traduce el destino guardado de un favorito a un Intent de navegación. Los destinos
 * nuevos usan el id del nodo con prefijo "node:"; los ids antiguos (y los nombres de
 * las actividades legadas eliminadas) se remapean a su nodo equivalente para que los
 * favoritos guardados por usuarios existentes sigan funcionando.
 */
public final class FavoriteDestinationMapper {

    public static final String NODE_PREFIX = "node:";

    public static final String DEST_SECTION_PRIMERA = "section_primera";
    public static final String DEST_ART_SECTION_1_TITLE_1 = "article_section_1_title_1";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_1 = "article_section_1_title_2_chapter_1";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_2 = "article_section_1_title_2_chapter_2";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_3 = "article_section_1_title_2_chapter_3";

    private static final Map<String, String> LEGACY_ACTIVITY_TO_DESTINATION;
    private static final Map<String, String> DESTINATION_TO_NODE;

    static {
        // Nombres de clase de actividades legadas ya eliminadas; se conservan como
        // literales porque pueden estar guardados en los favoritos de usuarios.
        Map<String, String> legacyMap = new HashMap<>();
        legacyMap.put("com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit1",
                DEST_ART_SECTION_1_TITLE_1);
        legacyMap.put("com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit2Cap1",
                DEST_ART_SECTION_1_TITLE_2_CHAPTER_1);
        LEGACY_ACTIVITY_TO_DESTINATION = Collections.unmodifiableMap(legacyMap);

        Map<String, String> nodeMap = new HashMap<>();
        nodeMap.put(DEST_SECTION_PRIMERA, "sec_1");
        nodeMap.put(DEST_ART_SECTION_1_TITLE_1, "sec_1_tit_1");
        nodeMap.put(DEST_ART_SECTION_1_TITLE_2_CHAPTER_1, "sec_1_tit_2_cap_1");
        nodeMap.put(DEST_ART_SECTION_1_TITLE_2_CHAPTER_2, "sec_1_tit_2_cap_2");
        nodeMap.put(DEST_ART_SECTION_1_TITLE_2_CHAPTER_3, "sec_1_tit_2_cap_3");
        DESTINATION_TO_NODE = Collections.unmodifiableMap(nodeMap);
    }

    private FavoriteDestinationMapper() {
    }

    /** Id de destino para un nodo del árbol (formato nuevo). */
    public static String destinationForNode(String nodeId) {
        return NODE_PREFIX + nodeId;
    }

    public static String normalizeDestinationId(@Nullable String rawId) {
        if (TextUtils.isEmpty(rawId)) {
            return "";
        }
        String value = rawId.trim();
        if (LEGACY_ACTIVITY_TO_DESTINATION.containsKey(value)) {
            return LEGACY_ACTIVITY_TO_DESTINATION.get(value);
        }
        return value;
    }

    @Nullable
    public static Intent toIntent(Context context, String destinationId) {
        String normalized = normalizeDestinationId(destinationId);

        String nodeId;
        if (normalized.startsWith(NODE_PREFIX)) {
            nodeId = normalized.substring(NODE_PREFIX.length());
        } else {
            nodeId = DESTINATION_TO_NODE.get(normalized);
        }
        if (nodeId == null || nodeId.isEmpty()) {
            return null;
        }

        LegalHierarchyRepository.Node node = LegalHierarchyRepository.findNodeById(nodeId);
        if (node == null) {
            return null;
        }
        return LegalHierarchyRepository.buildIntentForNode(context, node);
    }
}
