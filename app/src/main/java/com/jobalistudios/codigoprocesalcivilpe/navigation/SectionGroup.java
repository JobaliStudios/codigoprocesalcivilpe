package com.jobalistudios.codigoprocesalcivilpe.navigation;

import java.util.List;

import androidx.annotation.NonNull;

public class SectionGroup {
    private final String title;
    private final int articleCount;
    private final List<SectionItem> items;

    public SectionGroup(@NonNull String title, int articleCount, @NonNull List<SectionItem> items) {
        this.title = title;
        this.articleCount = articleCount;
        this.items = items;
    }

    public String getTitle() { return title; }
    public int getArticleCount() { return articleCount; }
    public List<SectionItem> getItems() { return items; }
}
