package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class BusquedaArticleQueryTest {

    @Test
    public void reconoceLasFormasComunesDeConsulta() {
        assertEquals("564", BusquedaViewModel.extractArticleNumber("564"));
        assertEquals("564", BusquedaViewModel.extractArticleNumber("Artículo 564"));
        assertEquals("647", BusquedaViewModel.extractArticleNumber("647"));
        assertEquals("647", BusquedaViewModel.extractArticleNumber("art. 647"));
        assertEquals("647", BusquedaViewModel.extractArticleNumber("Artículo 647"));
        assertEquals("647", BusquedaViewModel.extractArticleNumber("articulo 647."));
        assertEquals("647", BusquedaViewModel.extractArticleNumber(" ART 647 "));
        assertEquals("647", BusquedaViewModel.extractArticleNumber("art. n° 647"));
        assertEquals("647-A", BusquedaViewModel.extractArticleNumber("647-a"));
        assertEquals("647-A", BusquedaViewModel.extractArticleNumber("647 A"));
        assertEquals("647-A", BusquedaViewModel.extractArticleNumber("artículo 647-A"));
        assertEquals("647-A", BusquedaViewModel.extractArticleNumber("647a"));
        assertEquals("506-A", BusquedaViewModel.extractArticleNumber("Artículo 506 a"));
    }

    @Test
    public void ignoraConsultasQueNoSonNumeroDeArticulo() {
        assertNull(BusquedaViewModel.extractArticleNumber(null));
        assertNull(BusquedaViewModel.extractArticleNumber(""));
        assertNull(BusquedaViewModel.extractArticleNumber("embargo"));
        assertNull(BusquedaViewModel.extractArticleNumber("art"));
        assertNull(BusquedaViewModel.extractArticleNumber("647 embargo"));
        assertNull(BusquedaViewModel.extractArticleNumber("artículo 647 embargo"));
        assertNull(BusquedaViewModel.extractArticleNumber("12345"));
    }
}
