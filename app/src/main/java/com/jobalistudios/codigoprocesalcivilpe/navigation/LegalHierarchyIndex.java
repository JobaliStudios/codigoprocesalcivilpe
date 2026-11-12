package com.jobalistudios.codigoprocesalcivilpe.navigation;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Índice inmutable del árbol jurídico. Mantiene las referencias originales a los
 * nodos (no copia ni reordena títulos, capítulos, subcapítulos o contenidos).
 *
 * Se construye una sola vez en el primer acceso al repositorio. Los mapas son
 * de solo lectura y se publican mediante el inicializador de clase de Java.
 */
final class LegalHierarchyIndex {
    private final LegalHierarchyRepository.Node root;
    private final Map<String, LegalHierarchyRepository.Node> nodesById;
    private final Map<String, LegalHierarchyRepository.Node> parentsByChildId;
    private final Map<Integer, LegalHierarchyRepository.Node> nodesByTextRes;

    private LegalHierarchyIndex(
            LegalHierarchyRepository.Node root,
            Map<String, LegalHierarchyRepository.Node> nodesById,
            Map<String, LegalHierarchyRepository.Node> parentsByChildId,
            Map<Integer, LegalHierarchyRepository.Node> nodesByTextRes
    ) {
        this.root = root;
        this.nodesById = Collections.unmodifiableMap(nodesById);
        this.parentsByChildId = Collections.unmodifiableMap(parentsByChildId);
        this.nodesByTextRes = Collections.unmodifiableMap(nodesByTextRes);
    }

    static LegalHierarchyIndex from(LegalHierarchyRepository.Node root) {
        Map<String, LegalHierarchyRepository.Node> byId = new HashMap<>();
        Map<String, LegalHierarchyRepository.Node> parents = new HashMap<>();
        Map<Integer, LegalHierarchyRepository.Node> byTextRes = new HashMap<>();
        index(root, null, byId, parents, byTextRes);
        return new LegalHierarchyIndex(root, byId, parents, byTextRes);
    }

    private static void index(
            LegalHierarchyRepository.Node node,
            @Nullable LegalHierarchyRepository.Node parent,
            Map<String, LegalHierarchyRepository.Node> byId,
            Map<String, LegalHierarchyRepository.Node> parents,
            Map<Integer, LegalHierarchyRepository.Node> byTextRes
    ) {
        // Primero el propio nodo y después sus hijos, en el orden original del
        // árbol. Si hubiera claves duplicadas, se conserva el primer hallazgo.
        if (!byId.containsKey(node.id)) {
            byId.put(node.id, node);
        }
        if (parent != null && !parents.containsKey(node.id)) {
            parents.put(node.id, parent);
        }
        if (node.content != null && !byTextRes.containsKey(node.content.getTextRes())) {
            byTextRes.put(node.content.getTextRes(), node);
        }
        for (LegalHierarchyRepository.Node child : node.children) {
            index(child, node, byId, parents, byTextRes);
        }
    }

    LegalHierarchyRepository.Node root() {
        return root;
    }

    @Nullable
    LegalHierarchyRepository.Node findNodeById(String id) {
        return nodesById.get(id);
    }

    @Nullable
    LegalHierarchyRepository.Node findParent(String childId) {
        return parentsByChildId.get(childId);
    }

    @Nullable
    LegalHierarchyRepository.Node findNodeByTextRes(int textRes) {
        return nodesByTextRes.get(textRes);
    }
}
