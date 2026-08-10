package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(RobolectricTestRunner.class)
public class ArticleSequenceResolverTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void normalArticle_resolvesRealNeighbors() {
        ArticleSequenceResolver.Result result = resolve("563");

        assertEquals("562", result.previous.article.number);
        assertEquals("563", result.current.article.number);
        assertEquals("564", result.next.article.number);
    }

    @Test
    public void alphanumericArticle_preservesRepositoryOrder() {
        ArticleSequenceResolver.Result before = resolve("506");
        ArticleSequenceResolver.Result alpha = resolve("506-a");

        assertEquals("506-A", before.next.article.number);
        assertEquals("506", alpha.previous.article.number);
        assertEquals("506-A", alpha.current.article.number);
        assertEquals("507", alpha.next.article.number);
    }

    @Test
    public void firstAndLastArticle_disableMissingNeighbor() {
        List<ArticleRepository.Location> sequence = ArticleSequenceResolver.buildSequence(context);
        assertFalse(sequence.isEmpty());

        ArticleSequenceResolver.Result first = resolve(sequence.get(0).article.number);
        ArticleSequenceResolver.Result last = resolve(
                sequence.get(sequence.size() - 1).article.number);

        assertNull(first.previous);
        assertNull(last.next);
    }

    @Test
    public void boundaryBetweenBlocks_usesLastThenFirstInRepositoryOrder() {
        List<ArticleRepository.Location> sequence = ArticleSequenceResolver.buildSequence(context);
        ArticleRepository.Location beforeBoundary = null;
        ArticleRepository.Location afterBoundary = null;
        for (int i = 0; i + 1 < sequence.size(); i++) {
            if (!sequence.get(i).block.key.equals(sequence.get(i + 1).block.key)) {
                beforeBoundary = sequence.get(i);
                afterBoundary = sequence.get(i + 1);
                break;
            }
        }

        assertNotNull(beforeBoundary);
        assertNotNull(afterBoundary);
        ArticleSequenceResolver.Result result = resolve(beforeBoundary.article.number);
        assertEquals(afterBoundary.article.number, result.next.article.number);
        assertEquals(afterBoundary.block.key, result.next.block.key);
    }

    @Test
    public void sequenceContainsNoDuplicateArticleNumbers() {
        List<ArticleRepository.Location> sequence = ArticleSequenceResolver.buildSequence(context);
        Set<String> unique = new HashSet<>();

        for (ArticleRepository.Location location : sequence) {
            unique.add(location.article.number);
        }

        assertEquals(sequence.size(), unique.size());
    }

    private ArticleSequenceResolver.Result resolve(String number) {
        ArticleSequenceResolver.Result result = ArticleSequenceResolver.resolve(context, number);
        assertNotNull(result);
        return result;
    }
}
