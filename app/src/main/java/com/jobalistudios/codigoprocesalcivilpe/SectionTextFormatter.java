package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.core.content.ContextCompat;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SectionTextFormatter {

    private SectionTextFormatter() {
    }

    public static SpannableString buildFormattedText(Context context, int textResId) {
        SpannableString spannable = new SpannableString(context.getString(textResId));
        applyArticleTitleFormatting(context, spannable);
        return spannable;
    }

    public static void applyArticleTitleFormatting(Context context, SpannableString spannable) {
        String fullText = spannable.toString();
        int colorArticulo = ContextCompat.getColor(context, R.color.article_title_color);
        int colorModificado = ContextCompat.getColor(context, R.color.modified_article_color);

        Pattern articlePattern = Pattern.compile("^Artículo\\s+\\d+\\s*.-\\s*[^\\n]+", Pattern.MULTILINE);
        Matcher articleMatcher = articlePattern.matcher(fullText);
        while (articleMatcher.find()) {
            int start = articleMatcher.start();
            int end = articleMatcher.end();
            spannable.setSpan(new ForegroundColorSpan(colorArticulo), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Pattern modificationBlockPattern = Pattern.compile(
                "(^\\*\\s*Artículo modificado[^\\n]*(\\n\\s*\\d+\\..*)*)",
                Pattern.MULTILINE
        );
        Matcher blockMatcher = modificationBlockPattern.matcher(fullText);
        while (blockMatcher.find()) {
            int start = blockMatcher.start();
            int end = blockMatcher.end();
            spannable.setSpan(new ForegroundColorSpan(colorModificado), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
