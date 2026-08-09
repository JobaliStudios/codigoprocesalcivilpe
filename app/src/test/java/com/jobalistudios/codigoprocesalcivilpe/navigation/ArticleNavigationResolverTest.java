package com.jobalistudios.codigoprocesalcivilpe.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ArticleNavigationResolverTest {

    @Test
    public void alphanumericArticle_reusesSectionContentNavigationAndExactOffset() {
        Context context = ApplicationProvider.getApplicationContext();

        ArticleNavigationResolver.Target target =
                ArticleNavigationResolver.resolve(context, "506-a");

        assertNotNull(target);
        assertEquals("506-A", target.getNumber());
        Intent intent = target.createIntent(context);
        assertEquals(SectionContentActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(target.getOffsetInBlock(), intent.getIntExtra(
                SectionContentActivity.EXTRA_SCROLL_TO_OFFSET, -1));
        assertNotNull(intent.getStringExtra(LegalHierarchyRepository.EXTRA_NODE_ID));
    }

    @Test
    public void missingArticle_returnsNullInsteadOfCrashing() {
        Context context = ApplicationProvider.getApplicationContext();

        assertNull(ArticleNavigationResolver.resolve(context, "9999"));
        assertNull(ArticleNavigationResolver.resolve(context, null));
    }
}
