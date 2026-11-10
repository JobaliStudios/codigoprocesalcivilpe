# Auditoría CPC — Etapa 27: lote ampliado de 45 artículos (10/11/2026)

**Ámbito:** cotejo textual dirigido, artículo por artículo, de 45 registros de las secciones de jurisdicción, competencia y auxiliares, en `main:app/src/main/assets/articles.json`, frente a [TUO Editora Perú, corte 07/06/2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007). Se revisaron páginas impresas **5–10** con capa de texto y muestras visuales; algunas capturas devolvieron blanco, de modo que **no deben presentarse todas las páginas como verificadas gráficamente**. Los resultados son cotejo de contenido de edición 2025, **NO certificación de vigencia hasta noviembre 2026**, ni aprobación de modificación de código.

## Cobertura

**45 artículos, sin doble cómputo**: 1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 39, 40, 41, 42, 43, 44, 45, 46, 48, 50, 51, 52, 54, 55.

- **40** presentan coincidencia del cuerpo completo de la norma con el TUO, sin divergencias advertidas en el contraste.
- **5** coinciden en regla procesal pero muestran variantes textuales/grafías que deben conservarse como propuestas editoriales pendientes: **12, 14, 44, 50 y 51**.
- El **artículo 4** ya estaba auditado previamente y se excluyó expresamente de este lote; otros artículos intermedios ya revisados tampoco se duplican.

| Artículo | Literal app | Literal TUO 2025 | Tipo |
|---|---|---|---|
| **12** | `interposicion` | `interposición` | Falta tilde; **JUR-033**, nueva propuesta editorial |
| **14** | `éste último` | `este último` | Acentuación diacrítica editorial, sin cambio de competencia |
| **44** | `re-examen` | `reexamen` | Variante gráfica; no altera régimen cautelar |
| **50** | `Abogado` | `abogado` | Mayúscula/minúscula en deber de sancionar |
| **51** | `Abogados` | `abogados` | Mayúscula/minúscula en facultad de interrogación |

Las reglas de competencia territorial, funcional, por cuantía y prevención se han mantenido sin divergencias sustantivas advertidas en este cotejo. En los artículos **35–46** se corroboraron plazos, inhibición, contienda, remisión, conservación de medidas, multas y costos. La edición de 2025 respalda las fórmulas cotejadas, pero el contraste no incluye una revisión exhaustiva de toda eventual disposición especial posterior.

## JUR-031 (artículo 408) — Investigación adicional, SIN MODIFICAR

El [DL 1384 original, publicado 04/09/2018](https://cdn.www.gob.pe/uploads/document/file/192139/DL_1384.pdf) reproduce expresamente la modificación como **numeral 2 del artículo 408**, y el TUO 2025 sigue esa numeración. Un estudio jurídico difundido desde el repositorio del Poder Judicial razona que, para preservar coherencia del régimen de consulta, la modificación **debería interpretarse referida al inciso 1**, conservando el inciso 2 anterior relativo a la parte perdedora representada por curador procesal. Esta es una **tesis doctrinal, no una fe de erratas ni una reforma**. No sobrescribir el texto promulgado por la interpretación de un autor. Pendiente jurisprudencia vinculante y estudio de aplicabilidad.

## JUR-016 y JUR-017 (artículo 648) — Prioridad alta mantenida

El [texto primario de Ley 26599](https://www2.congreso.gob.pe/sicr/cendocbib/con4_uibd.nsf/3FF4976BAC5C75E8052580AD0061A0FB/%24FILE/Ley_26599_Sustituyen_art%C3%ADculo_del_C%C3%B3digo_Procesal_Civil.pdf) y TUO 2025 respaldan la expresión `el embargo procederá` frente a `el embargado procederá` en la app. La referencia al expediente del Tribunal Constitucional también merece corrección documentada independiente. **Ninguna se aplicará sin autorización expresa del propietario.**

## GitHub y avance

Se actualizaron únicamente 45 filas de la matriz con el tipo de comparación y fuente. Cifras anteriores: **173/862** cotejos nuevos y **689** sin nuevo cotejo integral. Ahora **218/862 (25,29%)** con cotejo nuevo documentado y **644/862** pendientes. Los **862** registros siguen **NO_CERTIFICADA** para vigencia a 10/11/2026. La estimación gerencial global de Fase 1 (**60%**) no deriva de esta tasa de cobertura y no se incrementa automáticamente.

## Siguiente lote ampliado sugerido

Continuar por artículos aún pendientes **56–118**, excluyendo los ya cotejados, con especial atención a capacidad procesal, representación y poderes. Continuar resolución jurídica de JUR-031 y preparación de propuestas individuales JUR-016/017. Mantener exclusivamente documentos en `docs/audits`; sin editar `articles.json`, `strings.xml`, metadatos Android, fecha visible, binarios ni Google Play.
