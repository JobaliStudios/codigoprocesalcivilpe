package com.jobalistudios.codigoprocesalcivilpe.glosario;

import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GlossaryRepositoryTest {
    static final String FIXTURE = "{ \"version\":1, \"terms\": ["
            + "{\"id\":\"debido-proceso\",\"term\":\"Debido proceso\","
            + "\"aliases\":[\"Garantía procesal\"],"
            + "\"definition\":\"Definición ficticia usada solo en pruebas.\","
            + "\"articles\":[\"I\",\"2\"],\"related\":[],"
            + "\"source\":\"Test de fuente\",\"reviewedBy\":\"Revisor de test\","
            + "\"reviewedAt\":\"2026-11-16\"}]}";

    @Test public void productionGlossaryStartsEmptyAndDoesNotThrow() {
        Context context = ApplicationProvider.getApplicationContext();
        assertTrue(GlossaryRepository.getTerms(context).isEmpty());
        assertNull(GlossaryRepository.find(context, "debido-proceso"));
    }

    @Test public void parserLoadsAllEditorialFields() {
        List<GlossaryRepository.Term> terms = GlossaryRepository.parseJson(FIXTURE);
        assertEquals(1, terms.size());
        GlossaryRepository.Term term = terms.get(0);
        assertEquals("debido-proceso", term.getId());
        assertEquals("Debido proceso", term.getTerm());
        assertEquals("Garantía procesal", term.getAliases().get(0));
        assertEquals("I", term.getArticles().get(0));
        assertEquals("Test de fuente", term.getSource());
        assertEquals("Revisor de test", term.getReviewedBy());
        assertEquals("2026-11-16", term.getReviewedAt());
        assertNotNull(term.getDefinition());
    }

    @Test public void malformedJsonVersionAndIncompleteReviewFailClosed() {
        assertTrue(GlossaryRepository.parseJson("{").isEmpty());
        assertTrue(GlossaryRepository.parseJson("{}").isEmpty());
        assertTrue(GlossaryRepository.parseJson("{\"version\":2,\"terms\":[]}").isEmpty());
        assertTrue(GlossaryRepository.parseJson("{\"version\":1,\"terms\":null}").isEmpty());
        assertTrue(GlossaryRepository.parseJson(
                FIXTURE.replace("\"reviewedBy\":\"Revisor de test\"",
                        "\"reviewedBy\":\"\"")).isEmpty());
        assertTrue(GlossaryRepository.parseJson(
                FIXTURE.replace("\"aliases\":[\"Garantía procesal\"]", "\"aliases\":null"))
                .isEmpty());
    }

    @Test public void duplicateIdsDoNotEnablePartialData() {
        String valid = FIXTURE;
        String entry = valid.substring(valid.indexOf("{\"id\":"),
                valid.lastIndexOf("]}") + 1);
        assertTrue(GlossaryRepository.parseJson(
                "{\"version\":1,\"terms\":[" + entry + "," + entry + "]}").isEmpty());
    }
}
