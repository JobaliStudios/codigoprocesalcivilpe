package com.jobalistudios.codigoprocesalcivilpe.referencias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleBlock;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ArticleCrossReferenceCorpusTest {
    @Test
    public void everyResolvedCorpusRangeMatchesOriginalAndHasValidTarget() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleCrossReferenceResolver resolver = new ArticleCrossReferenceResolver();
        int resolvedCount = 0;

        for (ArticleBlock block : ArticleRepository.getBlocks(context).values()) {
            String original = block.fullText();
            for (ResolvedArticleCrossReference reference : resolver.resolveBlock(context, block)) {
                assertEquals(
                        reference.reference.rawText,
                        original.substring(reference.reference.start, reference.reference.end)
                );
                assertFalse(reference.target.getNumber().isEmpty());
                resolvedCount++;
            }
        }
        assertTrue("Se esperaban referencias internas reales en el CPC", resolvedCount > 100);
    }

    @Test
    public void realInternalPluralAndAlphanumericResolveButCivilCodeDoesNot() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleCrossReferenceResolver resolver = new ArticleCrossReferenceResolver();

        List<ResolvedArticleCrossReference> article495 =
                resolver.resolveArticle(context, article(context, "495"));
        assertEquals("424", article495.get(0).target.getNumber());
        assertEquals("425", article495.get(1).target.getNumber());

        List<ResolvedArticleCrossReference> article528 =
                resolver.resolveArticle(context, article(context, "528"));
        assertTrue(article528.stream()
                .anyMatch(reference -> reference.target.getNumber().equals("523-A")));

        List<ResolvedArticleCrossReference> article675 =
                resolver.resolveArticle(context, article(context, "675"));
        assertFalse(article675.stream()
                .anyMatch(reference -> reference.target.getNumber().equals("424")));
    }

    private Article article(Context context, String number) {
        return ArticleRepository.findArticle(context, number).get(0).article;
    }
}
