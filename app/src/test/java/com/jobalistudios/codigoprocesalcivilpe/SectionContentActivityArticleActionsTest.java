package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.IntentCompat;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleShareFormatter;
import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoritesManager;
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
        application.getSharedPreferences(
                "codigoprocesalcivil_favorites", Context.MODE_PRIVATE)
                .edit().clear().commit();
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

    @Test
    public void dynamicHeader_andSameBlockNavigation_followVisibleArticleImmediately() {
        SectionContentActivity activity = launchArticle("564");
        Article article564 = findArticle("564");
        Article article565 = findArticle("565");
        assertEquals(
                ArticleRepository.findArticle(application, "564").get(0).block.key,
                ArticleRepository.findArticle(application, "565").get(0).block.key
        );

        assertHeader(activity, article564);
        MaterialButton previous = activity.findViewById(R.id.btnPreviousArticle);
        MaterialButton next = activity.findViewById(R.id.btnNextArticle);
        assertTrue(previous.isEnabled());
        assertTrue(next.isEnabled());

        next.performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertHeader(activity, article565);

        previous.performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertHeader(activity, article564);
    }

    @Test
    public void repealedChip_appearsOnlyWhileArticle835IsCurrent() {
        SectionContentActivity activity = launchArticle("834");
        Chip statusChip = activity.findViewById(R.id.currentArticleStatusChip);
        TextView title = activity.findViewById(R.id.currentArticleTitle);
        MaterialButton next = activity.findViewById(R.id.btnNextArticle);

        assertEquals(View.GONE, statusChip.getVisibility());

        next.performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertEquals("Artículo 835",
                ((TextView) activity.findViewById(R.id.currentArticleNumber)).getText().toString());
        assertEquals(View.VISIBLE, statusChip.getVisibility());
        assertEquals("DEROGADO", statusChip.getText().toString());
        assertEquals("Artículo 835 derogado", statusChip.getContentDescription().toString());
        assertEquals(View.GONE, title.getVisibility());

        next.performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertEquals("Artículo 836",
                ((TextView) activity.findViewById(R.id.currentArticleNumber)).getText().toString());
        assertEquals(View.GONE, statusChip.getVisibility());
        assertEquals(View.VISIBLE, title.getVisibility());
    }

    @Test
    public void favoriteButton_tracksCurrentArticleInsteadOfNode() {
        SectionContentActivity activity = launchArticle("564");
        MaterialButton favorite = activity.findViewById(R.id.btnFavoriteArticle);
        FavoritesManager manager = new FavoritesManager(application);

        assertFalse(favorite.isSelected());
        favorite.performClick();
        assertTrue(favorite.isSelected());
        assertTrue(manager.isFavorite("article:564"));

        activity.findViewById(R.id.btnNextArticle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertHeader(activity, findArticle("565"));
        assertFalse(favorite.isSelected());
        assertFalse(manager.isFavorite("article:565"));
        assertTrue(manager.isFavorite("article:564"));
    }

    @Test
    public void listenAction_isInDynamicHeaderAndNamesVisibleArticleForTalkBack() {
        SectionContentActivity activity = launchArticle("564");
        View speechControls = activity.findViewById(R.id.articleSpeechControls);
        MaterialButton listen = activity.findViewById(R.id.btnListenArticle);

        assertEquals(View.VISIBLE, speechControls.getVisibility());
        assertEquals(View.VISIBLE, listen.getVisibility());
        assertEquals(speechControls, listen.getParent());
        assertEquals("Escuchar Artículo 564", listen.getContentDescription().toString());

        activity.findViewById(R.id.btnNextArticle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertEquals("Escuchar Artículo 565", listen.getContentDescription().toString());
    }

    @Test
    public void crossingBlockBoundary_startsResolvedDestinationAndFinishesReader() {
        ArticleRepository.Location[] boundary = firstBlockBoundary();
        SectionContentActivity activity = launchArticle(boundary[0].article.number);

        activity.findViewById(R.id.btnNextArticle).performClick();

        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertEquals(boundary[1].article.offsetInBlock, started.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
        assertTrue(activity.isFinishing());
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

    private void assertHeader(SectionContentActivity activity, Article article) {
        TextView number = activity.findViewById(R.id.currentArticleNumber);
        TextView title = activity.findViewById(R.id.currentArticleTitle);
        assertEquals(application.getString(R.string.article_number_format, article.number),
                number.getText().toString());
        assertEquals(article.title, title.getText().toString());
    }

    private ArticleRepository.Location[] firstBlockBoundary() {
        ArticleRepository.Location previous = null;
        for (com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock block
                : ArticleRepository.getBlocks(application).values()) {
            for (Article article : block.articles) {
                ArticleRepository.Location current =
                        ArticleRepository.findArticle(application, article.number).get(0);
                if (previous != null && !previous.block.key.equals(current.block.key)) {
                    return new ArticleRepository.Location[]{previous, current};
                }
                previous = current;
            }
        }
        throw new AssertionError("El repositorio no contiene un límite entre bloques");
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
