package com.jobalistudios.codigoprocesalcivilpe;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

/** Paleta local de lectura, independiente de la preferencia visual del resto de la app. */
public final class ReaderAppearancePalette {
    public enum ResolvedTheme { LIGHT, SEPIA, DARK }

    public final int background;
    public final int surface;
    public final int text;
    public final int muted;
    public final int primary;
    public final int container;
    public final int divider;
    public final int highlight;
    public final int onPrimary;
    public final ResolvedTheme theme;

    private ReaderAppearancePalette(Context c, ResolvedTheme theme) {
        this.theme = theme;
        int[] resources;
        switch (theme) {
            case SEPIA:
                resources = new int[] {R.color.reader_sepia_background,
                        R.color.reader_sepia_surface, R.color.reader_sepia_text,
                        R.color.reader_sepia_muted, R.color.reader_sepia_primary,
                        R.color.reader_sepia_container, R.color.reader_sepia_line,
                        R.color.reader_sepia_highlight, R.color.reader_sepia_on_primary};
                break;
            case DARK:
                resources = new int[] {R.color.reader_dark_background,
                        R.color.reader_dark_surface, R.color.reader_dark_text,
                        R.color.reader_dark_muted, R.color.reader_dark_primary,
                        R.color.reader_dark_container, R.color.reader_dark_line,
                        R.color.reader_dark_highlight, R.color.reader_dark_on_primary};
                break;
            case LIGHT:
            default:
                resources = new int[] {R.color.reader_light_background,
                        R.color.reader_light_surface, R.color.reader_light_text,
                        R.color.reader_light_muted, R.color.reader_light_primary,
                        R.color.reader_light_container, R.color.reader_light_line,
                        R.color.reader_light_highlight, R.color.reader_light_on_primary};
        }
        background = ContextCompat.getColor(c, resources[0]);
        surface = ContextCompat.getColor(c, resources[1]);
        text = ContextCompat.getColor(c, resources[2]);
        muted = ContextCompat.getColor(c, resources[3]);
        primary = ContextCompat.getColor(c, resources[4]);
        container = ContextCompat.getColor(c, resources[5]);
        divider = ContextCompat.getColor(c, resources[6]);
        highlight = ContextCompat.getColor(c, resources[7]);
        onPrimary = ContextCompat.getColor(c, resources[8]);
    }

    @NonNull
    public static ResolvedTheme resolve(ReadingPreferenceManager.ReaderTheme pref,
                                        boolean appIsDark) {
        switch (pref) {
            case LIGHT: return ResolvedTheme.LIGHT;
            case SEPIA: return ResolvedTheme.SEPIA;
            case DARK: return ResolvedTheme.DARK;
            case AUTO:
            default: return appIsDark ? ResolvedTheme.DARK : ResolvedTheme.LIGHT;
        }
    }

    @NonNull
    public static ReaderAppearancePalette from(Context c) {
        boolean dark = (c.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        return new ReaderAppearancePalette(c,
                resolve(ReadingPreferenceManager.getReaderTheme(c), dark));
    }

    /** Únicamente el lector recibe este overlay; Configuración conserva el tema global. */
    public static void applyOverlayBeforeCreate(Activity activity) {
        int style = overlayStyle(ReadingPreferenceManager.getReaderTheme(activity));
        if (style != 0) activity.getTheme().applyStyle(style, true);
    }

    public static int overlayStyle(ReadingPreferenceManager.ReaderTheme mode) {
        switch (mode) {
            case LIGHT: return R.style.ThemeOverlay_App_Reader_Light;
            case SEPIA: return R.style.ThemeOverlay_App_Reader_Sepia;
            case DARK: return R.style.ThemeOverlay_App_Reader_Dark;
            case AUTO:
            default: return 0;
        }
    }

    /** Se puede llamar otra vez cuando el usuario cambia el tema en la hoja. */
    public static void applyReaderColors(Activity activity) {
        ReaderAppearancePalette p = from(activity);
        activity.getWindow().getDecorView().setBackgroundColor(p.background);
        WindowCompat.getInsetsController(activity.getWindow(),
                activity.getWindow().getDecorView())
                .setAppearanceLightStatusBars(p.theme != ResolvedTheme.DARK);
        WindowCompat.getInsetsController(activity.getWindow(),
                activity.getWindow().getDecorView())
                .setAppearanceLightNavigationBars(p.theme != ResolvedTheme.DARK);

        View scroll = activity.findViewById(R.id.scrollViewContent);
        if (scroll != null) scroll.setBackgroundColor(p.background);
        View content = activity.findViewById(R.id.textView2);
        if (content instanceof TextView) ((TextView) content).setTextColor(p.text);

        View header = activity.findViewById(R.id.fixedCardView);
        if (header instanceof MaterialCardView) {
            ((MaterialCardView) header).setCardBackgroundColor(p.surface);
            ((MaterialCardView) header).setStrokeColor(p.divider);
        }
        View footer = activity.findViewById(R.id.readingBottomBar);
        if (footer instanceof MaterialCardView) {
            ((MaterialCardView) footer).setCardBackgroundColor(p.surface);
            ((MaterialCardView) footer).setStrokeColor(p.divider);
        }
        int[] headingIds = {R.id.sectionTitle, R.id.sectionSubtitle,
                R.id.currentArticleNumber, R.id.currentArticleTitle};
        for (int id : headingIds) {
            View v = activity.findViewById(id);
            if (v instanceof TextView) ((TextView) v).setTextColor(p.text);
        }
        int[] primaryButtons = {R.id.btnTamanoLetra, R.id.btnCopyArticle,
                R.id.btnPreviousArticle, R.id.btnFavoriteArticle,
                R.id.btnArticleNote, R.id.btnShareArticle,
                R.id.btnNextArticle};
        for (int id : primaryButtons) {
            View v = activity.findViewById(id);
            if (v instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) v;
                button.setTextColor(p.primary);
                button.setIconTint(ColorStateList.valueOf(p.primary));
            }
        }
        View chips = activity.findViewById(R.id.relatedArticlesChipGroup);
        if (chips instanceof ChipGroup) {
            ChipGroup group = (ChipGroup) chips;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof Chip) {
                    ((Chip) child).setTextColor(p.primary);
                    ((Chip) child).setChipBackgroundColor(ColorStateList.valueOf(p.container));
                }
            }
        }
    }

    public static Typeface getBodyTypeface(Context context) {
        switch (ReadingPreferenceManager.getReaderFont(context)) {
            case SERIF:
                return ResourcesCompat.getFont(context, R.font.literata);
            case ACCESSIBLE:
                return ResourcesCompat.getFont(context, R.font.atkinson_regular);
            case SANS:
            default:
                return Typeface.DEFAULT;
        }
    }

    private ReaderAppearancePalette() {
        throw new AssertionError("Static API");
    }
}
