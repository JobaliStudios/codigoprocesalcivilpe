package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TtsSpan;

import androidx.core.content.ContextCompat;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeAnnotation;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeAnnotationParser;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeCalloutSpan;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryEntry;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistorySpan;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ArticleCrossReferenceSpan;
import com.jobalistudios.codigoprocesalcivilpe.referencias.ResolvedArticleCrossReference;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SectionTextFormatter {

    private static final NormativeAnnotationParser NORMATIVE_ANNOTATION_PARSER =
            new NormativeAnnotationParser();

    private SectionTextFormatter() {
    }

    public static SpannableString buildFormattedText(Context context, String content) {
        SpannableString spannable = new SpannableString(content);
        applyArticleTitleFormatting(context, spannable);
        applyNormativeAnnotationFormatting(context, spannable);
        return spannable;
    }

    public static void applyArticleTitleFormatting(Context context, SpannableString spannable) {
        String fullText = spannable.toString();
        int colorArticulo = ContextCompat.getColor(context, R.color.article_title_color);

        Pattern articlePattern = Pattern.compile(
                "^Artículo\\s+\\d+(?:(?:\\s*-\\s*|\\s+)[A-Z])?\\s*\\.\\s*-\\s*[^\\n]+",
                Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher articleMatcher = articlePattern.matcher(fullText);
        while (articleMatcher.find()) {
            int start = articleMatcher.start();
            int end = articleMatcher.end();
            spannable.setSpan(new ForegroundColorSpan(colorArticulo), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

    public static void applyNormativeAnnotationFormatting(
            Context context,
            SpannableString spannable
    ) {
        int backgroundColor = ContextCompat.getColor(context, R.color.legal_update_background);
        int borderColor = ContextCompat.getColor(context, R.color.legal_update_border);
        int titleColor = ContextCompat.getColor(context, R.color.legal_update_title);
        int summaryColor = ContextCompat.getColor(context, R.color.legal_update_summary);
        int textColor = ContextCompat.getColor(context, R.color.legal_update_text);
        float borderWidth = context.getResources().getDimension(R.dimen.stroke_width_thin);
        int horizontalPadding = context.getResources().getDimensionPixelSize(R.dimen.space_m);
        int verticalPadding = context.getResources().getDimensionPixelSize(R.dimen.space_s);
        float titleTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                11,
                context.getResources().getDisplayMetrics()
        );
        float summaryTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                12,
                context.getResources().getDisplayMetrics()
        );

        for (NormativeAnnotation annotation
                : NORMATIVE_ANNOTATION_PARSER.parse(spannable.toString())) {
            String title = context.getString(annotation.type == NormativeAnnotation.Type.REPEALED
                    ? R.string.normative_callout_title_repealed
                    : R.string.normative_callout_title_update);
            spannable.setSpan(
                    new NormativeCalloutSpan(
                            annotation.start,
                            annotation.end,
                            backgroundColor,
                            borderColor,
                            titleColor,
                            summaryColor,
                            borderWidth,
                            horizontalPadding,
                            verticalPadding,
                            titleTextSize,
                            summaryTextSize,
                            title,
                            annotation.compactSummary(),
                            context.getString(R.string.normative_callout_action)
                    ),
                    annotation.start,
                    annotation.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannable.setSpan(
                    new ForegroundColorSpan(textColor),
                    annotation.start,
                    annotation.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannable.setSpan(
                    new StyleSpan(Typeface.ITALIC),
                    annotation.start,
                    annotation.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    /** Superpone una acción accesible sobre cada anotación sin alterar ningún carácter. */
    public static void applyNormativeHistoryInteraction(
            Context context,
            SpannableString spannable,
            List<NormativeHistoryEntry> entries,
            NormativeHistorySpan.Listener listener
    ) {
        for (NormativeHistoryEntry entry : entries) {
            NormativeAnnotation annotation = entry.annotation;
            if (annotation.start < 0
                    || annotation.end <= annotation.start
                    || annotation.end > spannable.length()) {
                continue;
            }
            spannable.setSpan(
                    new NormativeHistorySpan(entry.articleNumber, listener),
                    annotation.start,
                    annotation.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            String title = context.getString(
                    annotation.type == NormativeAnnotation.Type.REPEALED
                            ? R.string.normative_callout_title_repealed
                            : R.string.normative_callout_title_update
            );
            String details = annotation.compactSummary() == null
                    ? annotation.rawText
                    : annotation.compactSummary();
            spannable.setSpan(
                    new TtsSpan.TextBuilder(context.getString(
                            R.string.normative_history_accessibility_description,
                            title,
                            details
                    )).build(),
                    annotation.start,
                    annotation.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    /** Añade navegación sobre rangos existentes; no inserta ni reemplaza caracteres. */
    public static void applyArticleCrossReferenceFormatting(
            Context context,
            SpannableString spannable,
            List<ResolvedArticleCrossReference> references,
            ArticleCrossReferenceSpan.Listener listener
    ) {
        int linkColor = ContextCompat.getColor(context, R.color.colorPrimary);
        for (ResolvedArticleCrossReference resolved : references) {
            int start = resolved.reference.start;
            int end = resolved.reference.end;
            if (start < 0 || end <= start || end > spannable.length()) {
                continue;
            }
            spannable.setSpan(
                    new ArticleCrossReferenceSpan(
                            resolved.target.getNumber(),
                            linkColor,
                            listener
                    ),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }
}
