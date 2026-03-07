package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import com.jobalistudios.codigoprocesalcivilpe.favoritos.FavoriteItem;

import org.json.JSONObject;
import org.junit.Test;

public class jobalistudiosUnitTest {

    @Test
    public void favoriteItem_toJsonAndFromJson_preservesBusinessFields() throws Exception {
        FavoriteItem favoriteItem = new FavoriteItem(
                "art-1",
                "Artículo 1",
                "Regla general",
                "articulo",
                "seccion_primera_titulo_1"
        );

        JSONObject serialized = favoriteItem.toJson();
        FavoriteItem restored = FavoriteItem.fromJson(serialized);

        assertEquals("art-1", restored.getId());
        assertEquals("Artículo 1", restored.getTitle());
        assertEquals("Regla general", restored.getSubtitle());
        assertEquals("articulo", restored.getType());
        assertEquals("seccion_primera_titulo_1", restored.getDestinationId());
    }

    @Test
    public void favoriteItem_fromJson_usesLegacyActivityFieldWhenDestinationMissing() {
        JSONObject legacyJson = new JSONObject()
                .put("id", "legacy-id")
                .put("title", "Título")
                .put("subtitle", "Subtítulo")
                .put("type", "legacy")
                .put("activity", "seccion_tercera_titulo_8");

        FavoriteItem restored = FavoriteItem.fromJson(legacyJson);

        assertEquals("seccion_tercera_titulo_8", restored.getDestinationId());
    }
}
