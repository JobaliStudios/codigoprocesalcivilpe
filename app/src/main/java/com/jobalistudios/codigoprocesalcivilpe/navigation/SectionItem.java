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

    public SectionItem(@StringRes int titleRes, @StringRes int subtitleRes, @StringRes int rangeRes, Class<?> destination) {
        this.titleRes = titleRes;
        this.subtitleRes = subtitleRes;
        this.rangeRes = rangeRes;
        this.destination = destination;
    }

    public int getTitleRes() { return titleRes; }
    public int getSubtitleRes() { return subtitleRes; }
    public int getRangeRes() { return rangeRes; }
    public Class<?> getDestination() { return destination; }
}
