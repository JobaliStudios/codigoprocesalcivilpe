package com.jobalistudios.codigoprocesalcivilpe.navigation;

import androidx.annotation.StringRes;

public class SectionItem {
    @StringRes
    private final int titleRes;
    @StringRes
    private final int subtitleRes;
    @StringRes
    private final int rangeRes;
    private final Class<?> destination;
    private final SectionFilterType type;

    private final String title;
    private final String subtitle;
    private final String range;
    private final String nodeId;

    public SectionItem(@StringRes int titleRes, @StringRes int subtitleRes, @StringRes int rangeRes, Class<?> destination) {
        this(titleRes, subtitleRes, rangeRes, destination, SectionFilterType.TITULO);
    }

    public SectionItem(
            @StringRes int titleRes,
            @StringRes int subtitleRes,
            @StringRes int rangeRes,
            Class<?> destination,
            SectionFilterType type
    ) {
        this.titleRes = titleRes;
        this.subtitleRes = subtitleRes;
        this.rangeRes = rangeRes;
        this.destination = destination;
        this.type = type;
        this.title = null;
        this.subtitle = null;
        this.range = null;
        this.nodeId = null;
    }

    public SectionItem(String title, String subtitle, String typeLabel, String nodeId) {
        this.titleRes = 0;
        this.subtitleRes = 0;
        this.rangeRes = 0;
        this.destination = null;
        this.type = mapType(typeLabel);
        this.title = title;
        this.subtitle = subtitle;
        this.range = "";
        this.nodeId = nodeId;
    }

    private SectionFilterType mapType(String typeLabel) {
        if ("CAPITULO".equalsIgnoreCase(typeLabel)) return SectionFilterType.CAPITULO;
        if ("SUBCAPITULO".equalsIgnoreCase(typeLabel)) return SectionFilterType.SUBCAPITULO;
        return SectionFilterType.TITULO;
    }

    public int getTitleRes() { return titleRes; }
    public int getSubtitleRes() { return subtitleRes; }
    public int getRangeRes() { return rangeRes; }
    public Class<?> getDestination() { return destination; }
    public SectionFilterType getType() { return type; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getRange() { return range; }
    public String getNodeId() { return nodeId; }

    public boolean hasStringContent() {
        return title != null;
    }
}
