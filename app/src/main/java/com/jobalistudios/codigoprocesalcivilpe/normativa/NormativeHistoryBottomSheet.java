package com.jobalistudios.codigoprocesalcivilpe.normativa;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleLegalStatusResolver;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Detalle normativo local y desplazable que no altera la navegación del lector. */
public final class NormativeHistoryBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "NormativeHistoryBottomSheet";
    private static final String ARG_BLOCK_KEY = "blockKey";
    private static final String ARG_ARTICLE_NUMBER = "articleNumber";
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'de' uuuu",
            new Locale("es", "PE")
    );

    @NonNull
    public static NormativeHistoryBottomSheet newInstance(
            @NonNull String blockKey,
            @NonNull String articleNumber
    ) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_BLOCK_KEY, blockKey);
        arguments.putString(ARG_ARTICLE_NUMBER, articleNumber);
        NormativeHistoryBottomSheet sheet = new NormativeHistoryBottomSheet();
        sheet.setArguments(arguments);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.bottom_sheet_normative_history, container, false);
        TextView title = root.findViewById(R.id.normativeHistoryTitle);
        ViewCompat.setAccessibilityHeading(title, true);
        root.findViewById(R.id.normativeHistoryClose).setOnClickListener(view -> dismiss());

        String blockKey = requireArguments().getString(ARG_BLOCK_KEY, "");
        String articleNumber = requireArguments().getString(ARG_ARTICLE_NUMBER, "");
        ArticleBlock block = ArticleRepository.getBlock(requireContext(), blockKey);
        Article article = findArticle(block, articleNumber);
        List<NormativeHistoryEntry> entries = article == null || block == null
                ? Collections.emptyList()
                : new NormativeHistoryResolver().resolve(article, block, block.fullText());

        bindHeader(root, articleNumber, article);
        LinearLayout entriesContainer = root.findViewById(R.id.normativeHistoryEntries);
        for (NormativeHistoryEntry entry : entries) {
            bindEntry(inflater.inflate(
                    R.layout.item_normative_history_entry,
                    entriesContainer,
                    false
            ), entry, entriesContainer);
        }
        return root;
    }

    private void bindHeader(View root, String articleNumber, @Nullable Article article) {
        TextView articleView = root.findViewById(R.id.normativeHistoryArticle);
        articleView.setText(getString(R.string.normative_history_article_format, articleNumber));

        TextView activeStatus = root.findViewById(R.id.normativeHistoryActiveStatus);
        Chip repealedStatus = root.findViewById(R.id.normativeHistoryRepealedStatus);
        boolean repealed = ArticleLegalStatusResolver.isRepealed(article);
        activeStatus.setVisibility(repealed ? View.GONE : View.VISIBLE);
        repealedStatus.setVisibility(repealed ? View.VISIBLE : View.GONE);
        if (repealed) {
            repealedStatus.setContentDescription(getString(
                    R.string.article_status_repealed_description,
                    articleNumber
            ));
        }
    }

    private void bindEntry(View item, NormativeHistoryEntry entry, LinearLayout container) {
        View instrumentGroup = item.findViewById(R.id.normativeInstrumentGroup);
        TextView instrumentValue = item.findViewById(R.id.normativeInstrumentValue);
        boolean hasInstrument = entry.instrumentDisplayName != null;
        instrumentGroup.setVisibility(hasInstrument ? View.VISIBLE : View.GONE);
        if (hasInstrument) {
            instrumentValue.setText(entry.instrumentDisplayName);
        }

        View dateGroup = item.findViewById(R.id.normativeDateGroup);
        TextView dateValue = item.findViewById(R.id.normativeDateValue);
        boolean hasDate = entry.publicationDate != null;
        dateGroup.setVisibility(hasDate ? View.VISIBLE : View.GONE);
        if (hasDate) {
            dateValue.setText(DISPLAY_DATE.format(entry.publicationDate));
        }

        View typeGroup = item.findViewById(R.id.normativeTypeGroup);
        TextView typeValue = item.findViewById(R.id.normativeTypeValue);
        int typeLabel = changeTypeLabel(entry.changeType);
        typeGroup.setVisibility(typeLabel == 0 ? View.GONE : View.VISIBLE);
        if (typeLabel != 0) {
            typeValue.setText(typeLabel);
        }

        MaterialButton sourceButton = item.findViewById(R.id.normativeOfficialSource);
        boolean hasSource = entry.sourceUrl != null && entry.instrumentDisplayName != null;
        sourceButton.setVisibility(hasSource ? View.VISIBLE : View.GONE);
        if (hasSource) {
            sourceButton.setContentDescription(getString(
                    R.string.normative_history_official_source_description,
                    entry.instrumentDisplayName
            ));
            sourceButton.setOnClickListener(view -> openOfficialSource(entry.sourceUrl));
        }
        container.addView(item);
    }

    private int changeTypeLabel(NormativeHistoryEntry.ChangeType type) {
        switch (type) {
            case MODIFICATION:
                return R.string.normative_history_type_modification;
            case DEROGATION:
                return R.string.normative_history_type_derogation;
            case INCORPORATION:
                return R.string.normative_history_type_incorporation;
            case REPLACEMENT:
                return R.string.normative_history_type_replacement;
            case OTHER:
            case UNKNOWN:
            default:
                return 0;
        }
    }

    private void openOfficialSource(@NonNull String sourceUrl) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl)));
        } catch (ActivityNotFoundException | SecurityException ignored) {
            Toast.makeText(
                    requireContext(),
                    R.string.normative_history_source_unavailable,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Nullable
    private Article findArticle(@Nullable ArticleBlock block, String articleNumber) {
        if (block == null) {
            return null;
        }
        for (Article article : block.articles) {
            if (article.number.equals(articleNumber)) {
                return article;
            }
        }
        return null;
    }
}
