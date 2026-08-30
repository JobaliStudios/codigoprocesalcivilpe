package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryEntry;
import com.jobalistudios.codigoprocesalcivilpe.normativa.NormativeHistoryResolver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class NormativeHistoryResolverTest {

    private final NormativeHistoryResolver resolver = new NormativeHistoryResolver();

    @Test
    public void completeEntry_isDerivedFromExistingAnnotation() {
        Fixture fixture = oneArticle(
                "100",
                "Caso",
                "* Artículo derogado por la Ley 32377, publicada el 7 de junio de 2025."
        );

        NormativeHistoryEntry entry = resolver.resolve(
                fixture.article,
                fixture.block,
                fixture.block.fullText()
        ).get(0);

        assertEquals("Ley 32377", entry.annotation.legalInstrument);
        assertEquals("Ley N.º 32377", entry.instrumentDisplayName);
        assertEquals(LocalDate.of(2025, 6, 7), entry.publicationDate);
        assertEquals(NormativeHistoryEntry.ChangeType.DEROGATION, entry.changeType);
        assertEquals(fixture.block.fullText().substring(
                entry.annotation.start,
                entry.annotation.end
        ), entry.annotation.rawText);
    }

    @Test
    public void realArticle835_hasRepealedStateLawDateTypeAndVerifiedSource() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleRepository.Location location =
                ArticleRepository.findArticle(context, "835").get(0);

        NormativeHistoryEntry entry = resolver.resolve(
                location.article,
                location.block,
                location.block.fullText()
        ).get(0);

        assertEquals(ArticleLegalStatusResolver.Status.REPEALED, entry.currentStatus);
        assertEquals("Ley N.º 32377", entry.instrumentDisplayName);
        assertEquals(LocalDate.of(2025, 6, 7), entry.publicationDate);
        assertEquals(NormativeHistoryEntry.ChangeType.DEROGATION, entry.changeType);
        assertEquals(
                "https://busquedas.elperuano.pe/dispositivo/NL/2407453-7",
                entry.sourceUrl
        );
    }

    @Test
    public void realModifiedArticle834_remainsActiveAndIsModification() {
        Context context = ApplicationProvider.getApplicationContext();
        ArticleRepository.Location location =
                ArticleRepository.findArticle(context, "834").get(0);

        NormativeHistoryEntry entry = resolver.resolve(
                location.article,
                location.block,
                location.block.fullText()
        ).get(0);

        assertEquals(ArticleLegalStatusResolver.Status.ACTIVE, entry.currentStatus);
        assertEquals(NormativeHistoryEntry.ChangeType.MODIFICATION, entry.changeType);
        assertEquals("Ley N.º 32377", entry.instrumentDisplayName);
    }

    @Test
    public void twoAnnotations_areNewestFirstAndUndatedEntriesRemainLast() {
        String note2019 = "* Artículo modificado por la Ley 30001, publicada el 15 de marzo de 2019.";
        String undated = "* Artículo modificado por la Ley 30002.";
        String note2025 = "* Artículo modificado por la Ley 32377, publicada el 7 de junio de 2025.";
        Fixture fixture = oneArticle(
                "123",
                "Caso múltiple",
                note2019 + "\n\n" + undated + "\n\n" + note2025
        );

        List<NormativeHistoryEntry> entries = resolver.resolve(
                fixture.article,
                fixture.block,
                fixture.block.fullText()
        );

        assertEquals(3, entries.size());
        assertEquals("Ley N.º 32377", entries.get(0).instrumentDisplayName);
        assertEquals("Ley N.º 30001", entries.get(1).instrumentDisplayName);
        assertEquals("Ley N.º 30002", entries.get(2).instrumentDisplayName);
        assertNull(entries.get(2).publicationDate);
    }

    @Test
    public void adjacentArticles_onlyReceiveAnnotationsInsideTheirOwnRange() {
        String textOne = articleText(
                "10",
                "Primero",
                "* Artículo modificado por la Ley 30010, publicada el 1 de enero de 2020."
        );
        String textTwo = articleText(
                "11",
                "Segundo",
                "* Artículo derogado por la Ley 30011, publicada el 2 de febrero de 2021."
        );
        Article first = new Article("10", "Primero", textOne, 0);
        Article second = new Article("11", "Segundo", textTwo, textOne.length());
        ArticleBlock block = new ArticleBlock(
                "two_articles",
                "",
                Collections.unmodifiableList(Arrays.asList(first, second))
        );

        List<NormativeHistoryEntry> firstHistory = resolver.resolve(first, block, block.fullText());
        List<NormativeHistoryEntry> secondHistory = resolver.resolve(second, block, block.fullText());

        assertEquals(1, firstHistory.size());
        assertEquals("Ley N.º 30010", firstHistory.get(0).instrumentDisplayName);
        assertEquals(1, secondHistory.size());
        assertEquals("Ley N.º 30011", secondHistory.get(0).instrumentDisplayName);
    }

    @Test
    public void articleWithoutAnnotation_hasNoHistory() {
        Fixture fixture = oneArticle("200", "Sin historial", "Contenido jurídico.");

        assertTrue(resolver.resolve(
                fixture.article,
                fixture.block,
                fixture.block.fullText()
        ).isEmpty());
    }

    private Fixture oneArticle(String number, String title, String body) {
        String text = articleText(number, title, body);
        Article article = new Article(number, title, text, 0);
        ArticleBlock block = new ArticleBlock(
                "fixture_" + number,
                "",
                Collections.singletonList(article)
        );
        return new Fixture(article, block);
    }

    private String articleText(String number, String title, String body) {
        return "Artículo " + number + ".- " + title + "\n\n" + body + "\n\n";
    }

    private static final class Fixture {
        final Article article;
        final ArticleBlock block;

        Fixture(Article article, ArticleBlock block) {
            this.article = article;
            this.block = block;
        }
    }
}
