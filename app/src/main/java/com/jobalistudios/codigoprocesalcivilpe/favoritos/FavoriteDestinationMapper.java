package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimera;
import com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit1;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;
import com.jobalistudios.codigoprocesalcivilpe.SeccionPrimera.SeccionPrimeraTit2Cap1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class FavoriteDestinationMapper {

    public static final String DEST_SECTION_PRIMERA = "section_primera";
    public static final String DEST_ART_SECTION_1_TITLE_1 = "article_section_1_title_1";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_1 = "article_section_1_title_2_chapter_1";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_2 = "article_section_1_title_2_chapter_2";
    public static final String DEST_ART_SECTION_1_TITLE_2_CHAPTER_3 = "article_section_1_title_2_chapter_3";

    private static final Map<String, String> LEGACY_ACTIVITY_TO_DESTINATION;

    static {
        Map<String, String> legacyMap = new HashMap<>();
        legacyMap.put(SeccionPrimera.class.getName(), DEST_SECTION_PRIMERA);
        legacyMap.put(SeccionPrimeraTit1.class.getName(), DEST_ART_SECTION_1_TITLE_1);
        legacyMap.put(SeccionPrimeraTit2Cap1.class.getName(), DEST_ART_SECTION_1_TITLE_2_CHAPTER_1);
        LEGACY_ACTIVITY_TO_DESTINATION = Collections.unmodifiableMap(legacyMap);
    }

    private FavoriteDestinationMapper() {
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
        switch (normalized) {
            case DEST_SECTION_PRIMERA:
                return new Intent(context, SeccionPrimera.class);
            case DEST_ART_SECTION_1_TITLE_1:
                return new Intent(context, SeccionPrimeraTit1.class);
            case DEST_ART_SECTION_1_TITLE_2_CHAPTER_1:
                return new Intent(context, SeccionPrimeraTit2Cap1.class);
            case DEST_ART_SECTION_1_TITLE_2_CHAPTER_2:
                return SectionContentActivity.createIntent(
                        context,
                        R.layout.activity_section_content,
                        R.string.seccionprimeratit2cap2txt,
                        R.string.capitulo2,
                        R.string.tit2cap2sub
                )
                        .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, context.getString(R.string.seccion_primeratit))
                        .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, context.getString(R.string.titulo2))
                        .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, context.getString(R.string.capitulo2))
                        .putExtra(SectionContentActivity.EXTRA_CURRENT_DESTINATION_ID, DEST_ART_SECTION_1_TITLE_2_CHAPTER_2)
                        .putExtra(SectionContentActivity.EXTRA_PREVIOUS_DESTINATION_ID, DEST_ART_SECTION_1_TITLE_2_CHAPTER_1)
                        .putExtra(SectionContentActivity.EXTRA_NEXT_DESTINATION_ID, DEST_ART_SECTION_1_TITLE_2_CHAPTER_3);
            case DEST_ART_SECTION_1_TITLE_2_CHAPTER_3:
                return SectionContentActivity.createIntent(
                        context,
                        R.layout.activity_section_content,
                        R.string.seccionprimeratit2cap3txt,
                        R.string.capitulo3,
                        R.string.tit2cap3sub
                )
                        .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, context.getString(R.string.seccion_primeratit))
                        .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, context.getString(R.string.titulo2))
                        .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, context.getString(R.string.capitulo3))
                        .putExtra(SectionContentActivity.EXTRA_CURRENT_DESTINATION_ID, DEST_ART_SECTION_1_TITLE_2_CHAPTER_3)
                        .putExtra(SectionContentActivity.EXTRA_PREVIOUS_DESTINATION_ID, DEST_ART_SECTION_1_TITLE_2_CHAPTER_2);
            default:
                return null;
        }
    }
}
