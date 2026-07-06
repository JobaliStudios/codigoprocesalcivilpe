package com.jobalistudios.codigoprocesalcivilpe.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Integridad del árbol de navegación: debe cubrir exactamente los mismos bloques de
 * contenido que LegalContentCatalog (el índice de búsqueda), sin nodos huérfanos.
 */
public class LegalHierarchyRepositoryTest {

    @Test
    public void lasHojasDelArbolCoincidenUnoAUnoConElCatalogo() {
        List<Integer> treeTextRes = new ArrayList<>();
        collectLeafTextRes(LegalHierarchyRepository.getTree(), treeTextRes);

        Set<Integer> treeSet = new HashSet<>(treeTextRes);
        assertEquals("Hojas duplicadas en el árbol", treeTextRes.size(), treeSet.size());

        Set<Integer> catalogSet = new HashSet<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            catalogSet.add(entry.textRes);
        }

        assertEquals("El árbol y el catálogo deben cubrir los mismos bloques de contenido",
                catalogSet, treeSet);
    }

    @Test
    public void losIdsDeNodoSonUnicos() {
        List<String> ids = new ArrayList<>();
        collectIds(LegalHierarchyRepository.getTree(), ids);
        assertEquals("Ids de nodo duplicados", ids.size(), new HashSet<>(ids).size());
    }

    @Test
    public void lasRamasTienenHijosYLasHojasContenido() {
        assertNodeShape(LegalHierarchyRepository.getTree());
    }

    @Test
    public void todoBloqueDelCatalogoSeResuelvePorTextRes() {
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            LegalHierarchyRepository.Node node = LegalHierarchyRepository.findNodeByTextRes(entry.textRes);
            assertNotNull("Sin nodo para textRes " + entry.textRes, node);
            assertTrue(node.isLeaf());
        }
    }

    @Test
    public void lasSeccionesSeResuelvenPorId() {
        for (String id : new String[]{"sec_1", "sec_2", "sec_3", "sec_4", "sec_5", "sec_6"}) {
            LegalHierarchyRepository.Node node = LegalHierarchyRepository.findNodeById(id);
            assertNotNull("Falta la sección " + id, node);
            assertTrue("La sección " + id + " no tiene hijos", !node.children.isEmpty());
        }
    }

    private static void collectLeafTextRes(LegalHierarchyRepository.Node node, List<Integer> out) {
        if (node.content != null) {
            out.add(node.content.getTextRes());
        }
        for (LegalHierarchyRepository.Node child : node.children) {
            collectLeafTextRes(child, out);
        }
    }

    private static void collectIds(LegalHierarchyRepository.Node node, List<String> out) {
        out.add(node.id);
        for (LegalHierarchyRepository.Node child : node.children) {
            collectIds(child, out);
        }
    }

    private static void assertNodeShape(LegalHierarchyRepository.Node node) {
        if (node.isLeaf()) {
            assertTrue("La hoja " + node.id + " no debe tener hijos", node.children.isEmpty());
        } else {
            assertTrue("El nodo " + node.id + " no tiene hijos ni contenido", !node.children.isEmpty());
        }
        for (LegalHierarchyRepository.Node child : node.children) {
            assertNodeShape(child);
        }
    }
}
