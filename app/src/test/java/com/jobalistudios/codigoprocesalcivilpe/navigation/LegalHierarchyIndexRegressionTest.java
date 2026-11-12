package com.jobalistudios.codigoprocesalcivilpe.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Compara todos los resultados indexados con los recorridos recursivos anteriores
 * (misma prioridad y mismas referencias), incluidos los destinos de Intent.
 */
@RunWith(RobolectricTestRunner.class)
public class LegalHierarchyIndexRegressionTest {

    @Test
    public void allNodesMatchPreviousRecursiveLookupAndImmediateParent() {
        LegalHierarchyRepository.Node root = LegalHierarchyRepository.getTree();
        List<LegalHierarchyRepository.Node> nodes = new ArrayList<>();
        collect(root, nodes);
        assertSame(root, LegalHierarchyRepository.getTree());

        for (LegalHierarchyRepository.Node node : nodes) {
            assertSame("Nodo distinto para " + node.id, findNodeRecursively(root, node.id),
                    LegalHierarchyRepository.findNodeById(node.id));
            assertSame("Padre distinto para " + node.id, findParentRecursively(root, node.id),
                    LegalHierarchyRepository.findParentNode(node.id));
            if (node.content != null) {
                assertSame("Bloque distinto para " + node.id,
                        findByTextResRecursively(root, node.content.getTextRes()),
                        LegalHierarchyRepository.findNodeByTextRes(node.content.getTextRes()));
            }
        }
        assertEquals(LegalHierarchyRepository.ROOT_ID, root.id);
    }

    @Test
    public void allCatalogEntriesStillResolveToSameLegalLeaves() {
        LegalHierarchyRepository.Node root = LegalHierarchyRepository.getTree();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            LegalHierarchyRepository.Node leaf =
                    LegalHierarchyRepository.findNodeByTextRes(entry.textRes);
            assertNotNull("Falta el bloque del catálogo " + entry.textRes, leaf);
            assertSame(findByTextResRecursively(root, entry.textRes), leaf);
            assertEquals(entry.textRes, leaf.content.getTextRes());
        }
    }

    @Test
    public void allNodeIntentsRetainOriginalDestinationAndExtras() {
        Context context = ApplicationProvider.getApplicationContext();
        List<LegalHierarchyRepository.Node> nodes = new ArrayList<>();
        collect(LegalHierarchyRepository.getTree(), nodes);

        for (LegalHierarchyRepository.Node node : nodes) {
            Intent intent = LegalHierarchyRepository.buildIntentForNode(context, node);
            assertNotNull("Sin destino " + node.id, intent.getComponent());
            if (node.content == null) {
                assertEquals(SectionListActivity.class.getName(),
                        intent.getComponent().getClassName());
                assertEquals(node.id,
                        intent.getStringExtra(LegalHierarchyRepository.EXTRA_NODE_ID));
            } else {
                LegalHierarchyRepository.ContentSpec spec = node.content;
                assertEquals(SectionContentActivity.class.getName(),
                        intent.getComponent().getClassName());
                assertEquals(node.id,
                        intent.getStringExtra(LegalHierarchyRepository.EXTRA_NODE_ID));
                assertEquals(R.layout.activity_section_content,
                        intent.getIntExtra(SectionContentActivity.EXTRA_LAYOUT_RES_ID, -1));
                assertEquals(spec.textRes,
                        intent.getIntExtra(SectionContentActivity.EXTRA_TEXT_RES_ID, -1));
                assertEquals(spec.titleRes,
                        intent.getIntExtra(SectionContentActivity.EXTRA_TITLE, -1));
                assertEquals(spec.subtitleRes,
                        intent.getIntExtra(SectionContentActivity.EXTRA_SUBTITLE, -1));
                assertEquals(spec.sectionLabel,
                        intent.getStringExtra(SectionContentActivity.EXTRA_SECTION_LABEL));
                assertEquals(spec.titleLabel,
                        intent.getStringExtra(SectionContentActivity.EXTRA_TITLE_LABEL));
                assertEquals(spec.chapterLabel,
                        intent.getStringExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL));
            }
        }
    }

    @Test
    public void allSectionRowsKeepTheirOriginalOrderAndMetadata() {
        List<LegalHierarchyRepository.Node> nodes = new ArrayList<>();
        collect(LegalHierarchyRepository.getTree(), nodes);

        for (LegalHierarchyRepository.Node node : nodes) {
            List<SectionGroup> groups = LegalHierarchyRepository.buildGroups(node);
            assertEquals("Cantidad de grupos distinta para " + node.id, 1, groups.size());
            SectionGroup group = groups.get(0);
            assertEquals(node.children.size(), group.getArticleCount());
            assertEquals(node.children.size(), group.getItems().size());

            for (int i = 0; i < node.children.size(); i++) {
                LegalHierarchyRepository.Node child = node.children.get(i);
                SectionItem item = group.getItems().get(i);
                assertEquals(child.id, item.getNodeId());
                assertEquals(child.title, item.getTitle());
                assertEquals(child.subtitle, item.getSubtitle());
                assertEquals(child.articleRange, item.getRange());
            }
        }
    }

    @Test
    public void unknownAndRootLookupsRetainNullSemantics() {
        assertNull(LegalHierarchyRepository.findNodeById("nonexistent-node-id"));
        assertNull(LegalHierarchyRepository.findNodeById(null));
        assertNull(LegalHierarchyRepository.findParentNode("nonexistent-node-id"));
        assertNull(LegalHierarchyRepository.findParentNode(null));
        assertNull(LegalHierarchyRepository.findParentNode(LegalHierarchyRepository.ROOT_ID));
        assertNull(LegalHierarchyRepository.findNodeByTextRes(-1));
    }

    private static void collect(LegalHierarchyRepository.Node node,
                                List<LegalHierarchyRepository.Node> out) {
        out.add(node);
        for (LegalHierarchyRepository.Node child : node.children) {
            collect(child, out);
        }
    }

    /** Algoritmo original: búsqueda en profundidad con prioridad al nodo actual. */
    private static LegalHierarchyRepository.Node findNodeRecursively(
            LegalHierarchyRepository.Node root, String id) {
        if (root.id.equals(id)) {
            return root;
        }
        for (LegalHierarchyRepository.Node child : root.children) {
            LegalHierarchyRepository.Node found = findNodeRecursively(child, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /** Algoritmo original: examina hijos inmediatos antes de descender. */
    private static LegalHierarchyRepository.Node findParentRecursively(
            LegalHierarchyRepository.Node parent, String id) {
        for (LegalHierarchyRepository.Node child : parent.children) {
            if (child.id.equals(id)) {
                return parent;
            }
            LegalHierarchyRepository.Node found = findParentRecursively(child, id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static LegalHierarchyRepository.Node findByTextResRecursively(
            LegalHierarchyRepository.Node root, int textRes) {
        if (root.content != null && root.content.getTextRes() == textRes) {
            return root;
        }
        for (LegalHierarchyRepository.Node child : root.children) {
            LegalHierarchyRepository.Node found = findByTextResRecursively(child, textRes);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
