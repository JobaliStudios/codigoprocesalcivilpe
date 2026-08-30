package com.jobalistudios.codigoprocesalcivilpe.referencias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ArticleCrossReferenceResolverTest {
    private Context context;
    private ArticleCrossReferenceResolver resolver;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        resolver = new ArticleCrossReferenceResolver();
    }

    @Test
    public void excludesExternalMissingAndSelfReferences() {
        assertTrue(resolver.resolveText(
                context, "424", "artículo 424 de este Código").isEmpty());
        assertTrue(resolver.resolveText(
                context, "1", "artículo 9999 de este Código").isEmpty());
        assertTrue(resolver.resolveText(
                context, "1", "artículo 10 del Código Civil").isEmpty());
        assertTrue(resolver.resolveText(
                context, "1", "artículo 139 de la Constitución").isEmpty());
    }

    @Test
    public void validInternalReferences_resolveThroughLocalRepository() {
        List<ResolvedArticleCrossReference> result = resolver.resolveText(
                context,
                "495",
                "Además de cumplir con los artículos 424 y 425 de este Código."
        );

        assertEquals(2, result.size());
        assertEquals("424", result.get(0).target.getNumber());
        assertEquals("425", result.get(1).target.getNumber());
    }

    @Test
    public void relatedArticles_removeDuplicatesAndKeepFirstAppearanceOrder() {
        List<com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver.Target>
                related = new RelatedArticlesResolver().resolveText(
                context,
                "100",
                "artículo 424, artículo 425, artículo 424 y artículo 426"
        );

        assertEquals(3, related.size());
        assertEquals("424", related.get(0).getNumber());
        assertEquals("425", related.get(1).getNumber());
        assertEquals("426", related.get(2).getNumber());
    }
}
