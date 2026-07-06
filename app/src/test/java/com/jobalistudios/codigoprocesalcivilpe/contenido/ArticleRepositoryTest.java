package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalContentCatalog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vigila que assets/articles.json (generado por tools/generate_articles.ps1) reconstruya
 * cada bloque EXACTAMENTE igual que el string compilado. Si estos tests fallan tras editar
 * strings.xml, hay que volver a ejecutar el generador.
 */
@RunWith(RobolectricTestRunner.class)
public class ArticleRepositoryTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void jsonReconstruyeCadaBloqueExactamenteComoGetString() {
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            String key = context.getResources().getResourceEntryName(entry.textRes);
            ArticleBlock block = ArticleRepository.getBlock(context, key);
            assertNotNull("Bloque ausente en articles.json: " + key, block);
            assertIdentical(key, context.getString(entry.textRes), block.fullText());
        }
    }

    @Test
    public void jsonNoTieneBloquesFueraDelCatalogo() {
        Set<String> catalogKeys = new HashSet<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            catalogKeys.add(context.getResources().getResourceEntryName(entry.textRes));
        }
        Map<String, ArticleBlock> blocks = ArticleRepository.getBlocks(context);
        assertEquals(catalogKeys.size(), blocks.size());
        for (String key : blocks.keySet()) {
            assertTrue("Bloque extra en articles.json: " + key, catalogKeys.contains(key));
        }
    }

    @Test
    public void articulosTienenFormaOffsetsYNumerosUnicos() {
        int total = 0;
        Set<String> numbers = new HashSet<>();
        for (ArticleBlock block : ArticleRepository.getBlocks(context).values()) {
            String fullText = block.fullText();
            assertTrue("El texto no empieza con el preámbulo en " + block.key,
                    fullText.startsWith(block.preamble));
            for (Article article : block.articles) {
                assertFalse("Número vacío en " + block.key, article.number.isEmpty());
                assertTrue("Número inválido en " + block.key + ": " + article.number,
                        article.number.matches("\\d+(-[A-F])?"));
                assertTrue("Sin encabezado en " + block.key + " art. " + article.number,
                        article.text.trim().startsWith("Artículo"));
                assertTrue("Offset incoherente en " + block.key + " art. " + article.number,
                        fullText.startsWith(article.text, article.offsetInBlock));
                assertTrue("Número global repetido: " + article.number, numbers.add(article.number));
                total++;
            }
        }
        assertTrue("Se esperaban ~862 artículos y hay " + total, total >= 800);
    }

    @Test
    public void encuentraElArticulo647YSuVariante647A() {
        List<ArticleRepository.Location> plain = ArticleRepository.findArticle(context, "647");
        assertEquals(1, plain.size());
        assertEquals("seccionquintatit4cap2subcap1", plain.get(0).block.key);
        assertEquals("Secuestro de vehículo", plain.get(0).article.title);

        List<ArticleRepository.Location> variant = ArticleRepository.findArticle(context, "647-a");
        assertEquals(1, variant.size());
        assertEquals("647-A", variant.get(0).article.number);

        assertTrue(ArticleRepository.findArticle(context, "9999").isEmpty());
    }

    @Test
    public void getContentTextCaeAlStringCompiladoFueraDelCatalogo() {
        assertEquals(context.getString(R.string.app_name),
                ArticleRepository.getContentText(context, R.string.app_name));
    }

    /** assertEquals daría mensajes de 40 KB; esto reporta solo el primer punto de diferencia. */
    private static void assertIdentical(String key, String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }
        int max = Math.min(expected.length(), actual.length());
        int i = 0;
        while (i < max && expected.charAt(i) == actual.charAt(i)) {
            i++;
        }
        int from = Math.max(0, i - 40);
        fail("Bloque " + key + " difiere en el índice " + i
                + " (longitudes " + expected.length() + " vs " + actual.length() + ")\n"
                + "esperado: …" + expected.substring(from, Math.min(expected.length(), i + 40)) + "…\n"
                + "obtenido: …" + actual.substring(from, Math.min(actual.length(), i + 40)) + "…");
    }
}
