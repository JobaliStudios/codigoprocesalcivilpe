# Código Procesal Civil PE — Etapa 41: fe de erratas, técnica legislativa y medición de avance (10/11/2026)

## Alcance
Esta etapa profundiza en evidencias primarias y análisis jurídico de las alertas JUR-016/017 (648), JUR-031 (408), JUR-037 (391), JUR-040 (688), y en el barrido de reformas posteriores al TUO 07/06/2025. **No es una certificación jurídica integral ni una declaración definitiva de inexistencia de reformas posteriores**.

## Hallazgos nuevos documentados
1. **Ley 31591 (26/10/2022)**: la [ficha técnica oficial del Congreso](https://leyes.congreso.gob.pe/Documentos/2021_2026/ADLP/Ficha_Tecnica_Espanol/31591-FTE.pdf) consigna expresamente **«Fe de erratas: No consta»**. El contenido anómalo `hubiera tenido tuvo` del art.391(4) está tanto en la app como en el TUO 2025 y el texto consolidado del Congreso. «No consta» en la ficha **no demuestra** que jamás existiera otra aclaración o reforma: cotejar facsímil original y una búsqueda específica por fe de erratas antes de editar.
2. **Art.408 — técnica legislativa**: el [DL 1384 original del MINJUSDH](https://cdn.www.gob.pe/uploads/document/file/192139/DL_1384.pdf) señala expresamente que modifica el **numeral 2** del art.408. Un [estudio publicado en el portal oficial del Poder Judicial](https://www.pj.gob.pe/wps/wcm/connect/cf1bd000411cefa7bf5abf5aa55ef1d3/Reglas%2Bde%2BBrasilia%2Bjusticia%2Bpara%2Breducir%2Blas%2Bdesigualdades%2Bsociales%2Bpublicaci%C3%B3n%2Bweb.pdf?MOD=AJPERES) expone la objeción de técnica legislativa: el cambio debió afectar el numeral 1 y no el 2, y discute su incidencia sobre consulta cuando la parte perdedora estuvo representada por curador procesal. **Es análisis doctrinal, no fe de erratas ni reforma**. La app reproduce el texto de la compilación; no reordenar numerales unilateralmente.
3. **Art.648 — JUR-016**: la [publicación Ley26599 original](https://docs.peru.justia.com/federales/leyes/26599-apr-22-1996.pdf) contiene `el embargado procederá`, mientras el TUO 2025 contiene `el embargo procederá`. El hallazgo sigue como **discrepancia de fuentes**, sin decisión de sustituir. **JUR-017** sigue como propuesta respaldada por [sentencia TC 006-96-AI/TC](https://www.tc.gob.pe/jurisprudencia/1996/00006-1996-AI.pdf).
4. **Art.688 — JUR-040**: propuesta mínima de corregir `acción cambiaría` por `acción cambiaria` sustentada por [DL1069 original](https://www.sunat.gob.pe/legislacion/nor_graf/2008/junio/nl20080628.pdf), aún sin aplicar.
5. **Barrido de 2025–2026**: el [repertorio LP Derecho, actualización mayo 2026](https://lpderecho.pe/codigo-procesal-civil-actualizado/) sigue indicando Ley32377 como última reforma. Es fuente secundaria, y una fecha de actualización mayo 2026 **no cubre** hasta noviembre 2026. Se realizaron búsquedas dirigidas de normas modificatorias y no se verificó, dentro del alcance de este barrido, una ley posterior que modifique directamente artículos del CPC. **No es prueba de ausencia** y se mantiene verificación pendiente de las ediciones oficiales y modificaciones indirectas.

## Propuestas de corrección aún sin autorización

**JUR-017:**
```diff
- Expediente 006-97-AI-TC LIMA
+ Expediente 006-96-AI/TC LIMA
```

**JUR-040:**
```diff
- acción cambiaría
+ acción cambiaria
```

**NO PROPONER** sustitución automática del numeral 6 del art.648 hasta resolver la fuente discrepante. **NO CAMBIAR** los numerales del art.408 ni la expresión compartida del art.391.

## Medición y alcance de porcentajes
- **Cobertura documental inicial = 862/862 (100%)**. Significa que cada artículo tiene algún cotejo registrado; **no** que todos hayan sido cotejados literalmente de manera exhaustiva.
- **Certificación de vigencia integral a 10/11/2026 = 0/862**. No se ha completado barrido legislativo exhaustivo, análisis de vigencia temporal y revisión final del conjunto.
- **Avance global de Fase 1 = estimación administrativa provisional del 60%, con 40% restante**. Esta cifra proviene de la valoración histórica de gestión; carece de desglose cuantitativo ponderado y **no debe presentarse como medición exacta** ni convertirse automáticamente en 70% porque se completó esta etapa.
- No hay fecha de terminación ni capacidad de prometerla sin acabar las verificaciones pendientes.
- Pendiente antes de una actualización de Play: concluir control de reformas, resolver conflictos de fuentes, decidir y autorizar correcciones, pruebas regresivas y preparación de versión.

## Cambios permitidos
**PR #54**, rama `audit/vigencia-cpc-2026-11-10`: solamente `docs/audits`. Permanecer abierto y como borrador, **no fusionar**. No editar `app/src/main/assets/articles.json`, `strings.xml`, versiones de Android, APK/AAB o Google Play.

## Etapa 42 sugerida
Crear una lista de control reproducible de normas entre el **08/06/2025 y 10/11/2026**, con fuentes oficiales, fecha real de publicación, artículos afectados, nivel de certeza, aplicabilidad transitoria y comprobación independiente. Priorizar búsqueda de fe de erratas Ley26599 y publicación facsimilar Ley31591; buscar cambios indirectos por normas especiales y jurisprudencia. Mantener `NO_CERTIFICADA` mientras no se complete.
