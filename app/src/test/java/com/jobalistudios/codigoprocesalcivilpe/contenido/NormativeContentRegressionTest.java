package com.jobalistudios.codigoprocesalcivilpe.contenido;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

/** Regresiones de las reformas normativas verificadas el 8 de agosto de 2026. */
@RunWith(RobolectricTestRunner.class)
public class NormativeContentRegressionTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void articulo561_incluyeRepresentacionIncorporadaPorLey32266() {
        String text = article("561").text;

        assertTrue(text.contains("9. Los abuelos, los tíos o hermanos mayores"));
        assertTrue(text.contains("Ley 32266"));
        assertTrue(text.contains("22 de marzo de 2025"));
    }

    @Test
    public void articulo731_usaReglaVigenteDeRemajuSinCondicionAntigua() {
        String text = article("731").text;

        assertTrue(text.contains("de conformidad con lo establecido en el artículo 1 de la Ley 30229"));
        assertTrue(text.contains("Ley 32297"));
        assertFalse(text.contains("si no existe oposición de ninguna de las partes"));
    }

    @Test
    public void articulo759_incluyeConstitucionYReglaEspecialDeSucesionIntestada() {
        String text = article("759").text;

        assertTrue(text.contains("artículo 159, numeral 2, de la Constitución Política del Perú"));
        assertTrue(text.contains("No emite dictamen"));
        assertTrue(text.contains("En los procesos de sucesión intestada no es necesaria"));
        assertTrue(text.contains("menores de edad o Consejo de Familia"));
        assertFalse(text.contains("artículo 250, inciso 2"));
    }

    @Test
    public void articulo834_aplicaQuinceDiasYVerificacionDocumental() {
        String text = article("834").text;

        assertTrue(text.contains("Dentro de los quince días"));
        assertTrue(text.contains("el juez verifica los documentos presentados"));
        assertTrue(text.contains("sin necesidad de citar a audiencia"));
        assertFalse(text.contains("treinta días"));
    }

    @Test
    public void articulo835_permaneceBuscableComoDerogado() {
        Article article = article("835");

        assertEquals("[Derogado]", article.title);
        assertTrue(article.text.startsWith("Artículo 835.- [Derogado]"));
        assertTrue(article.text.contains("Ley 32377"));
        assertFalse(article.text.contains("interviene con sujeción"));
    }

    private Article article(String number) {
        List<ArticleRepository.Location> locations = ArticleRepository.findArticle(context, number);
        assertEquals("El artículo debe tener una única ubicación: " + number, 1, locations.size());
        return locations.get(0).article;
    }
}
