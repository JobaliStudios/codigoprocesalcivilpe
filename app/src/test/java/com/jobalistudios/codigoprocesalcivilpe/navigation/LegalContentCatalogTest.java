package com.jobalistudios.codigoprocesalcivilpe.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Garantiza que el índice de búsqueda global cubra todo el contenido del código.
 * Si este test falla al agregar contenido nuevo, registra el bloque en LegalContentCatalog.
 */
public class LegalContentCatalogTest {

    /**
     * Convención de nombres de los strings de contenido (el texto completo de un
     * título/capítulo/subcapítulo). Si se agrega contenido con otra convención,
     * actualizar también este patrón para que la cobertura siga vigilada.
     */
    private static final Pattern CONTENT_STRING_NAME = Pattern.compile(
            "^seccion(primera|segunda|tercera|cuarta|quinta|sexta)tit\\d.*");

    @Test
    public void todoStringDeContenidoEstaEnElCatalogo() throws Exception {
        Map<Integer, String> contentStrings = contentStringResources();
        Set<Integer> indexed = new HashSet<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            indexed.add(entry.textRes);
        }

        List<String> missing = new ArrayList<>();
        for (Map.Entry<Integer, String> res : contentStrings.entrySet()) {
            if (!indexed.contains(res.getKey())) {
                missing.add(res.getValue());
            }
        }
        assertTrue("Bloques de contenido fuera del índice de búsqueda (agregarlos a LegalContentCatalog): " + missing,
                missing.isEmpty());
    }

    @Test
    public void todoElCatalogoApuntaAStringsDeContenido() throws Exception {
        Map<Integer, String> contentStrings = contentStringResources();
        List<Integer> unknown = new ArrayList<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            if (!contentStrings.containsKey(entry.textRes)) {
                unknown.add(entry.textRes);
            }
        }
        assertTrue("Entradas del catálogo cuyo textRes no sigue la convención seccion*tit* "
                        + "(¿string renombrado? actualizar CONTENT_STRING_NAME si es intencional): " + unknown,
                unknown.isEmpty());
    }

    @Test
    public void elCatalogoNoTieneContenidoDuplicado() {
        Set<Integer> seen = new HashSet<>();
        List<Integer> duplicated = new ArrayList<>();
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            if (!seen.add(entry.textRes)) {
                duplicated.add(entry.textRes);
            }
        }
        assertTrue("Bloques repetidos en el catálogo: " + duplicated, duplicated.isEmpty());
    }

    @Test
    public void lasEntradasTienenTodosSusRecursos() {
        for (LegalContentCatalog.Entry entry : LegalContentCatalog.getEntries()) {
            assertNotEquals("sectionNameRes vacío", 0, entry.sectionNameRes);
            assertNotEquals("titleRes vacío", 0, entry.titleRes);
            assertNotEquals("subtitleRes vacío", 0, entry.subtitleRes);
            assertNotEquals("articleRangeRes vacío", 0, entry.articleRangeRes);
            assertNotEquals("textRes vacío", 0, entry.textRes);
        }
    }

    @Test
    public void elCatalogoCubreLosBloquesConocidos() throws Exception {
        // 104 bloques al momento de crear el catálogo; solo debe crecer.
        assertTrue(LegalContentCatalog.getEntries().size() >= 104);
        assertEquals(contentStringResources().size(), LegalContentCatalog.getEntries().size());
    }

    private static Map<Integer, String> contentStringResources() throws IllegalAccessException {
        Map<Integer, String> result = new HashMap<>();
        for (Field field : R.string.class.getFields()) {
            if (CONTENT_STRING_NAME.matcher(field.getName()).matches()) {
                result.put(field.getInt(null), field.getName());
            }
        }
        return result;
    }
}
