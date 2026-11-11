# Auditoría CPC — Etapa 24: inventario completo y priorización de JUR-001 (10/11/2026)

**Propósito:** cerrar el inventario de cobertura, definir una secuencia auditable para el cotejo completo y rastrear inicialmente cambios normativos posteriores al TUO oficial. **Solo documentación; ninguna edición de producción.**

## Inventario reproducible de la matriz

Se leyó `docs/audits/2026-11-10-matriz-862-articulos.csv` desde el PR #54 mediante parser CSV con soporte de campos entrecomillados. **862 filas únicas** (15 columnas cada una), sin identificadores duplicados.

| Grupo heredado | Total |
|---|---:|
| Igualdad normalizada **parcial** | 685 |
| Requiere cotejo contextual | 160 |
| Rango derogado previamente documentado | 17 |
| **Total** | **862** |

| Cobertura nueva `cotejo_2026_11_10` | Total |
|---|---:|
| Con revisión documental distinta de `SIN_COTEJO_INTEGRAL_NUEVO` | **159** |
| **Sin** cotejo integral nuevo | **703** |
| **Total** | **862** |

Descomposición exacta de **703** registros pendientes: **672** de igualdad normalizada parcial, **17** de rangos derogados, **13** de cotejo contextual parcial heredado y **1** del artículo 122 con corrección JUR-007 documentada. El cierre de **135/135** casos contextuales originalmente sin resolución no implica que los otros 703 tengan cotejo nuevo completo. Los **862 registros** conservan `vigencia_2026_11_10=NO_CERTIFICADA`. La cobertura documental nueva es **159/862 = 18,45%**; no es una tasa de vigencia certificada.

## Priorización de comprobación

**Prioridad P0: hallazgos de mayor riesgo normativo, incluso si ya están cotejados históricamente.**

- **Art. 648 (JUR previo):** aplicación contiene `el embargado procederá hasta el sesenta por ciento`; TUO oficial 2025 y Ley 26599 indican `el embargo procederá...`. Revisar además la referencia al expediente del Tribunal Constitucional. La divergencia semántica merece propuesta de corrección individual, sin aprobar automáticamente.
- **Arts. 527 y 528:** verificar disposiciones de la Ley 27117, régimen de expropiación y excepciones de derogación; aclarar títulos, reglas y aplicabilidad temporal.
- **Art. 140:** revisar mención a OCMA tras institucionalización de ANC-PJ, además de variante de plazo.
- **Art. 692-A:** resolver divergencia entre ejemplares de reproducciones normativas 2014, sin preferir automáticamente la copia secundaria.
- **Art. 122:** comprobar en `main` la corrección JUR-007 previamente aprobada y evidencias de compilación; todavía figura sin cotejo integral nuevo.
- **Arts. 424, 446, 451, 566-A, 731 y 749:** revisión completa de contenidos, plazos, listas de numerales y reformas de capacidad/discapacidad o ejecución, no limitarse a equivalencia textual.
- **Artículos 469–470, 472, 540–541, 543–545, 697–703 y 713–714:** revalidar los **17** registros de rangos derogados con ley que derogó, vigencia y régimen transitorio.

**Primer conjunto de recotejo integral propuesto:** **122, 140, 424, 446, 451, 527, 528, 566-A, 648 y 749**, con adenda para 692-A; no confundir un muestreo de riesgo con certificación del total.

## Rastreo inicial de reformas posteriores al 07/06/2025

Fuentes:
- [El Peruano, catálogo de Normas Legales Actualizadas](https://diariooficial.elperuano.pe/normas/normasactualizadas): el TUO CPC ofrecido conserva portada **ACTUALIZADA 07/06/2025**, que **no** acredita los meses siguientes.
- [TUO CPC, Editora Perú](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007): base de cotejo con fecha de corte.
- [Congreso, noticia del 06/05/2026 sobre PL 9337/2024-CR](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/): informa **dictamen**, no promulgación.
- [Congreso, ficha oficial de la Ley 31591](https://leyes.congreso.gob.pe/Documentos/2021_2026/ADLP/Ficha_Tecnica_Espanol/31591-FTE.pdf): ejemplo de traza primaria de publicación y vigencia, anterior al corte.

Se hicieron búsquedas dirigidas a **normas CPC modificatorias 2026** y al proyecto alimentario; **no se confirmó con evidencia primaria en esta pasada una nueva ley modificatoria promulgada y vigente entre 08/06/2025 y 10/11/2026**. Esta es una *búsqueda negativa no exhaustiva*, no una declaración de inexistencia. Para certificar, es imprescindible búsqueda artículo por artículo en **SPIJ, Diario Oficial y archivo normativo del Congreso**, con control de entrada en vigor, modificaciones diferidas, fe de erratas y normas especiales. JUR-002 permanece abierto.

## Procedimiento de recotejo integral

Para cada registro: (1) preservar número, sumilla y **texto íntegro de producción**, párrafos, incisos y notas históricas; (2) comparar literalmente con TUO 2025 y cada modificatoria primaria pertinente; (3) registrar fecha de publicación y de vigencia, textos sustituidos y efecto transitorio; (4) distinguir diferencia normativa, editorial, de presentación o de procedencia; (5) marcar incertidumbre o discrepancia con identificador JUR y aprobación individual; (6) certificar **solo** después de acreditar la búsqueda al 10/11/2026 y de validar que las fuentes primarias no introducen un cambio adicional.

**Criterios de avance:** reporte con citas verificables, decisión pendiente/expresa del propietario, pruebas funcionales posteriores a cualquier edición, integridad SHA del corpus y chequeo de compilación. No generar APK/AAB ni modificar fecha visible/Google Play antes de completar los controles.

## Estado de gestión

- Subgrupo de 135 cotejos contextuales: **cerrado como cobertura**, no como vigencia.
- Cotejo nuevo documentado: **159/862 (18,4%)**.
- Sin cotejo integral nuevo: **703/862**.
- Vigencia jurídica certificada al 10/11/2026: **0 de 862**.
- Fase 1 **60%** se conserva únicamente como estimación de gestión anterior, no como indicador derivado del recotejo.
- PR #54 permanece borrador, no fusionar. Solo incorporar documentos de auditoría; **sin cambios en `articles.json`, `strings.xml`, metadatos de versión, binarios ni Google Play.**
