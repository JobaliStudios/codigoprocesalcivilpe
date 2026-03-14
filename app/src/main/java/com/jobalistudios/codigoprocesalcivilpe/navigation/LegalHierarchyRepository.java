package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SeccionCuarta.SeccionCuarta;
import com.jobalistudios.codigoprocesalcivilpe.SeccionQuinta.SeccionQuinta;
import com.jobalistudios.codigoprocesalcivilpe.SeccionSegunda.SeccionSegunda;
import com.jobalistudios.codigoprocesalcivilpe.SeccionSexta.SeccionSexta;
import com.jobalistudios.codigoprocesalcivilpe.SeccionTercera.SeccionTercera;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class LegalHierarchyRepository {

    public static final String ROOT_ID = "cpc_root";
    public static final String EXTRA_NODE_ID = "EXTRA_NODE_ID";

    private LegalHierarchyRepository() {}

    public static class Node {
        public final String id;
        public final String title;
        public final String subtitle;
        public final String type;
        public final List<Node> children;
        public final ContentSpec content;
        public final Class<?> legacyDestination;

        Node(String id, String title, String subtitle, String type, List<Node> children, ContentSpec content, Class<?> legacyDestination) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.type = type;
            this.children = children == null ? Collections.emptyList() : children;
            this.content = content;
            this.legacyDestination = legacyDestination;
        }

        public boolean isLeaf() {
            return content != null || legacyDestination != null;
        }
    }

    public static class ContentSpec {
        final int textRes;
        final int titleRes;
        final int subtitleRes;
        final String sectionLabel;
        final String titleLabel;
        final String chapterLabel;

        ContentSpec(int textRes, int titleRes, int subtitleRes, String sectionLabel, String titleLabel, String chapterLabel) {
            this.textRes = textRes;
            this.titleRes = titleRes;
            this.subtitleRes = subtitleRes;
            this.sectionLabel = sectionLabel;
            this.titleLabel = titleLabel;
            this.chapterLabel = chapterLabel;
        }
    }

    public static Node getTree() {
        Node secPrimera = new Node("sec_1", "Sección Primera", "Disposiciones generales", "SECCION", Arrays.asList(
                new Node("sec_1_tit_1", "Título I", "Jurisdicción y acción", "TITULO", Collections.emptyList(),
                        new ContentSpec(R.string.seccionprimeratit1txt, R.string.titulo1, R.string.sec1titulo1sub,
                                "Sección Primera", "Título I", "Título I"), null),
                new Node("sec_1_tit_2", "Título II", "Competencia", "TITULO", Arrays.asList(
                        new Node("sec_1_tit_2_cap_1", "Capítulo I", "Disposiciones Generales", "CAPITULO", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap1txt, R.string.capitulo1, R.string.tit2cap1sub,
                                        "Sección Primera", "Título II", "Capítulo I"), null),
                        new Node("sec_1_tit_2_cap_2", "Capítulo II", "Cuestionamiento de la competencia", "CAPITULO", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap2txt, R.string.capitulo2, R.string.tit2cap2sub,
                                        "Sección Primera", "Título II", "Capítulo II"), null),
                        new Node("sec_1_tit_2_cap_3", "Capítulo III", "Competencia internacional", "CAPITULO", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap3txt, R.string.capitulo3, R.string.tit2cap3sub,
                                        "Sección Primera", "Título II", "Capítulo III"), null)
                ), null, null)
        ), null, null);

        return new Node(ROOT_ID, "Código Procesal Civil", "Navegación por secciones", "CODIGO", Arrays.asList(
                secPrimera,
                new Node("sec_2", "Sección Segunda", "Sujeto del proceso", "SECCION", Collections.emptyList(), null, SeccionSegunda.class),
                new Node("sec_3", "Sección Tercera", "Actividad procesal", "SECCION", Collections.emptyList(), null, SeccionTercera.class),
                new Node("sec_4", "Sección Cuarta", "Postulación del proceso", "SECCION", Collections.emptyList(), null, SeccionCuarta.class),
                new Node("sec_5", "Sección Quinta", "Procesos contenciosos", "SECCION", Collections.emptyList(), null, SeccionQuinta.class),
                new Node("sec_6", "Sección Sexta", "Procesos no contenciosos", "SECCION", Collections.emptyList(), null, SeccionSexta.class)
        ), null, null);
    }

    @Nullable
    public static Node findNodeById(String nodeId) {
        return findNode(getTree(), nodeId);
    }

    private static Node findNode(Node root, String nodeId) {
        if (root.id.equals(nodeId)) return root;
        for (Node child : root.children) {
            Node found = findNode(child, nodeId);
            if (found != null) return found;
        }
        return null;
    }

    public static Intent buildIntentForNode(Context context, Node node) {
        if (node.content != null) {
            ContentSpec content = node.content;
            return SectionContentActivity.createIntent(context, R.layout.activity_section_content, content.textRes, content.titleRes, content.subtitleRes)
                    .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, content.sectionLabel)
                    .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, content.titleLabel)
                    .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, content.chapterLabel);
        }
        if (node.legacyDestination != null) {
            return new Intent(context, node.legacyDestination);
        }
        return SectionListActivity.createIntent(context, node.id);
    }

    public static List<SectionGroup> buildGroups(Node node) {
        List<Node> children = node.children;
        if (children.size() <= 7) {
            return Collections.singletonList(new SectionGroup("Opciones", children.size(), toItems(children)));
        }

        List<SectionGroup> groups = new ArrayList<>();
        for (int i = 0; i < children.size(); i += 5) {
            int end = Math.min(i + 5, children.size());
            List<Node> slice = children.subList(i, end);
            groups.add(new SectionGroup("Grupo " + (groups.size() + 1), slice.size(), toItems(slice)));
        }
        return groups;
    }

    private static List<SectionItem> toItems(List<Node> nodes) {
        List<SectionItem> items = new ArrayList<>();
        for (Node child : nodes) {
            items.add(new SectionItem(child.title, child.subtitle, child.type, child.id));
        }
        return items;
    }
}
