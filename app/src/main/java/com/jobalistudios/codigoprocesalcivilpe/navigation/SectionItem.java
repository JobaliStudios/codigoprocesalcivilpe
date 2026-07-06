package com.jobalistudios.codigoprocesalcivilpe.navigation;

/** Fila de la lista de navegación; apunta a un nodo de LegalHierarchyRepository. */
public class SectionItem {

    private final SectionFilterType type;
    private final String title;
    private final String subtitle;
    private final String range;
    private final String nodeId;

    public SectionItem(String title, String subtitle, String typeLabel, String nodeId, String range) {
        this.type = mapType(typeLabel);
        this.title = title;
        this.subtitle = subtitle;
        this.range = range == null ? "" : range;
        this.nodeId = nodeId;
    }

    private SectionFilterType mapType(String typeLabel) {
        if ("CAPITULO".equalsIgnoreCase(typeLabel)) return SectionFilterType.CAPITULO;
        if ("SUBCAPITULO".equalsIgnoreCase(typeLabel)) return SectionFilterType.SUBCAPITULO;
        return SectionFilterType.TITULO;
    }

    public SectionFilterType getType() { return type; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getRange() { return range; }
    public String getNodeId() { return nodeId; }
}
