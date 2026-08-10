package com.jobalistudios.codigoprocesalcivilpe.normativa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.contenido.Article;
import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NormativeAnnotationRepositoryTest {

    private final NormativeAnnotationParser parser = new NormativeAnnotationParser();

    @Test
    public void realArticles_extractTheirLatestExplicitInstrumentWithoutChangingContent() {
        assertArticle("561", "Ley 32266", "22 de marzo de 2025");
        assertArticle("731", "Ley 32297", "11 de abril de 2025");
        assertArticle("759", "Ley 32377", "7 de junio de 2025");
        assertArticle("834", "Ley 32377", "7 de junio de 2025");
    }

    private void assertArticle(String number, String instrument, String date) {
        Context context = ApplicationProvider.getApplicationContext();
        Article article = ArticleRepository.findArticle(context, number).get(0).article;
        NormativeAnnotation annotation = parser.parse(article.text).get(0);

        assertEquals(instrument, annotation.legalInstrument);
        assertEquals(date, annotation.publicationDate);
        assertEquals(annotation.rawText,
                article.text.substring(annotation.start, annotation.end));
        assertTrue(article.text.contains(annotation.rawText));
    }
}
