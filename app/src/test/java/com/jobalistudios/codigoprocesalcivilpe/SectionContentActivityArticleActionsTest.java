package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.text.Layout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.IntentCompat;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.button.MaterialButton;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleShareFormatter;
import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class SectionContentActivityArticleActionsTest {
    private Application application;
    private ActivityController<SectionContentActivity> controller;

    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        ClipboardManager clipboard = application.getSystemService(ClipboardManager.class);
        if (clipboard != null) {
            clipboard.clearPrimaryClip();
        }
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void copy_usesExactDirectlyOpenedAlphanumericArticle() {
        SectionContentActivity activity = launchArticle("506-A");
        Article article = findArticle("506-A");
        MaterialButton copy = activity.findViewById(R.id.btnCopyArticle);
        assertTrue(copy.isEnabled());

        copy.performClick();

        ClipData clip = application.getSystemService(ClipboardManager.class).getPrimaryClip();
        assertNotNull(clip);
        assertEquals(
                new ArticleShareFormatter(application.getString(R.string.app_name)).format(article),
                clip.getItemAt(0).getText().toString()
        );
        assertEquals(
                application.getString(R.string.article_clipboard_label, "506-A"),
                clip.getDescription().getLabel().toString()
        );
    }

    @Test
    public void share_opensNativeChooserWithPlainTextForVisibleArticle() {
        SectionContentActivity activity = launchArticle("506-A");
        Article article = findArticle("506-A");

        activity.findViewById(R.id.btnShareArticle).performClick();

        Intent chooser = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(chooser);
        assertEquals(Intent.ACTION_CHOOSER, chooser.getAction());
        assertEquals(
                application.getString(R.string.article_share_chooser_title),
                chooser.getStringExtra(Intent.EXTRA_TITLE)
        );
        Intent send = IntentCompat.getParcelableExtra(
                chooser,
                Intent.EXTRA_INTENT,
                Intent.class
        );
        assertNotNull(send);
        assertEquals(Intent.ACTION_SEND, send.getAction());
        assertEquals("text/plain", send.getType());
        assertEquals(
                new ArticleShareFormatter(application.getString(R.string.app_name)).format(article),
                send.getStringExtra(Intent.EXTRA_TEXT)
        );
        assertEquals(
                application.getString(R.string.article_share_subject, "506-A"),
                send.getStringExtra(Intent.EXTRA_SUBJECT)
        );
    }

    @Test
    public void copy_afterScrolling_switchesFromArticle560ToArticle562() {
        SectionContentActivity activity = launchArticle("560");
        Article article560 = findArticle("560");
        Article article562 = findArticle("562");

        scrollProbeInside(activity, article560);
        activity.findViewById(R.id.btnCopyArticle).performClick();
        String firstCopy = clipboardText();

        scrollProbeInside(activity, article562);
        activity.findViewById(R.id.btnCopyArticle).performClick();
        String secondCopy = clipboardText();

        ArticleShareFormatter formatter = new ArticleShareFormatter(
                application.getString(R.string.app_name)
        );
        assertEquals(formatter.format(article560), firstCopy);
        assertEquals(formatter.format(article562), secondCopy);
        assertNotEquals(firstCopy, secondCopy);
        assertFalse(secondCopy.contains("Artículo 560.-"));
    }

    private SectionContentActivity launchArticle(String number) {
        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(application, number);
        assertNotNull(target);
        controller = Robolectric.buildActivity(
                SectionContentActivity.class,
                target.createIntent(application)
        ).create().start().resume().visible();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        return controller.get();
    }

    private Article findArticle(String number) {
        ArticleRepository.Location location =
                ArticleRepository.findArticle(application, number).get(0);
        return location.article;
    }

    private void scrollProbeInside(SectionContentActivity activity, Article article) {
        TextView content = activity.findViewById(R.id.textView2);
        ScrollView scroll = activity.findViewById(R.id.scrollViewContent);
        Layout layout = content.getLayout();
        assertNotNull(layout);

        int characterOffset = article.offsetInBlock + article.text.length() / 2;
        int line = layout.getLineForOffset(characterOffset);
        int probeVertical = layout.getLineTop(line);
        int scrollY = content.getTop() + probeVertical - scroll.getHeight() / 3;
        scroll.scrollTo(0, Math.max(0, scrollY));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
    }

    private String clipboardText() {
        ClipboardManager clipboard = application.getSystemService(ClipboardManager.class);
        assertNotNull(clipboard);
        ClipData clip = clipboard.getPrimaryClip();
        assertNotNull(clip);
        return clip.getItemAt(0).getText().toString();
    }
}
