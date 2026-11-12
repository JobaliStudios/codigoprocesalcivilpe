package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.IntentCompat;
import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleShareFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Contratos de Android para el portapapeles y el chooser, sin alterar el texto. */
@RunWith(RobolectricTestRunner.class)
public class ReaderArticleTransferControllerTest {
    private Context context;
    private ReaderArticleTransferController transfer;
    private ArticleShareFormatter formatter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        transfer = new ReaderArticleTransferController(context);
        formatter = new ArticleShareFormatter(context.getString(R.string.app_name));
        ClipboardManager clipboard = context.getSystemService(ClipboardManager.class);
        assertNotNull(clipboard);
        clipboard.clearPrimaryClip();
    }

    @Test
    public void copyAlphanumericArticlePreservesOriginalTextAndClipboardLabel() {
        Article article = find("506-A");

        assertTrue(transfer.copy(article));

        ClipboardManager clipboard = context.getSystemService(ClipboardManager.class);
        ClipData copied = clipboard.getPrimaryClip();
        assertNotNull(copied);
        assertEquals(context.getString(R.string.article_clipboard_label, "506-A"),
                copied.getDescription().getLabel().toString());
        assertEquals(formatter.format(article), copied.getItemAt(0).getText().toString());
    }

    @Test
    public void chooserPreservesSubjectMimeTypeAndCompleteRepealedArticle() {
        Article article = find("835");

        Intent chooser = transfer.createShareChooser(article);

        assertEquals(Intent.ACTION_CHOOSER, chooser.getAction());
        assertEquals(context.getString(R.string.article_share_chooser_title),
                chooser.getStringExtra(Intent.EXTRA_TITLE));

        Intent send = IntentCompat.getParcelableExtra(
                chooser, Intent.EXTRA_INTENT, Intent.class);
        assertNotNull(send);
        assertEquals(Intent.ACTION_SEND, send.getAction());
        assertEquals("text/plain", send.getType());
        assertEquals(context.getString(R.string.article_share_subject, "835"),
                send.getStringExtra(Intent.EXTRA_SUBJECT));
        assertEquals(formatter.format(article), send.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Test
    public void copyAndShareUseTheSameFormatterForTheCurrentArticle() {
        Article article = find("564");
        assertTrue(transfer.copy(article));
        ClipboardManager clipboard = context.getSystemService(ClipboardManager.class);
        String copied = clipboard.getPrimaryClip().getItemAt(0).getText().toString();

        Intent chooser = transfer.createShareChooser(article);
        Intent send = IntentCompat.getParcelableExtra(
                chooser, Intent.EXTRA_INTENT, Intent.class);

        assertNotNull(send);
        assertEquals(copied, send.getStringExtra(Intent.EXTRA_TEXT));
    }

    private Article find(String number) {
        return ArticleRepository.findArticle(context, number).get(0).article;
    }
}
