# Código Procesal Civil PE — Etapa 46: verificación de iniciativas de oralidad y límite del registro 2026

**Corte de trabajo:** 10/11/2026, Perú. **Rama:** `audit/vigencia-cpc-2026-11-10`. **Alcance:** ampliación del control de promulgación C03 y clasificación de iniciativas; **no se certifica vigencia integral** y **no hay cambio de norma en producción**.

## 1. Comprobaciones reproducibles

| ID | Fuente | Comprobación y alcance | Decisión |
| --- | --- | --- | --- |
| E46-001 | [Congreso, leyes y disposiciones especiales 2026](https://www3.congreso.gob.pe/DIDP/seguimiento-reglamentacion-leyes/leyes-reglamentos-disposiciones-2026/) | El registro visible consultado llega hasta **Ley 32739 (25/07/2026)** y una fe de erratas del 26/07/2026, seguido del cierre de la tabla. No contiene resultados para «Procesal Civil» en su texto visible. | **FUENTE NO EXHAUSTIVA para agosto–noviembre 2026**. No utilizar su silencio como certificación de ausencia de reformas. |
| E46-002 | [PL 00483/2026-2031-CD, seguimiento público](https://ojo-al-congreso-sepia.vercel.app/proyectos/00483-2026-2031-CD/) y [expediente oficial del Congreso](https://wb2server.congreso.gob.pe/spley-portal/) | Se identifica iniciativa actualizada/presentada el **02/10/2026** desde antiguo PL **05855/2023-PJ**: propuesta de modificar CPC **465, 466 y 467**, e incorporar **475-A**, para reforzar oralidad civil. El seguimiento público la clasifica **«en comisión»**; la página oficial de expediente redirige a un portal dinámico, y su archivo PDF no pudo descargarse por tamaño desde el verificador. | **PROYECTO, NO LEY PUBLICADA ACREDITADA**. No modificar artículos 465–467 ni crear 475-A. Comprobar expediente y estado oficial antes de eventual release. |
| E46-003 | [PL 00484/2026-2031-CD, seguimiento público](https://ojo-al-congreso-sepia.vercel.app/proyectos/00484-2026-2031-CD/) y [expediente oficial del Congreso](https://wb2server.congreso.gob.pe/spley-portal/) | Se identifica iniciativa del **02/10/2026**, antes **07702/2023-PJ**, sobre cambios del CPC para oralidad civil. El seguimiento público consultado la clasifica **«en comisión»**. El PDF oficial enlazado tampoco pudo descargarse por tamaño. | **PROYECTO, NO LEY PUBLICADA ACREDITADA**. No incorporar artículos o redacciones propuestos. Verificar artículos afectados y trámite mediante ficha primaria completa. |
| E46-004 | [Congreso, comunicación 06/05/2026 sobre PL9337](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/) | Publicación institucional confirma **aprobación de dictamen de comisión**, no promulgación. | Mantener **JUR-002 abierto**. No aplicar propuesta a proceso alimentario. |
| E46-005 | [Ley 32377, El Peruano](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7) | Norma promulgada y publicada el **07/06/2025**; afecta CPC **759, 834 y 835**, ya conciliados en la etapa 45; anterior a la ventana 08/06/2025–10/11/2026. | **CAMBIO LEGAL YA REGISTRADO**; no duplicar como novedad 2026. |
| E46-006 | [TUO de Editora Perú](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007) | Portada del texto oficial disponible señala actualización **07/06/2025**. | **LÍNEA BASE 2025**, no certificación de que todos los artículos estén actualizados al 10/11/2026. |

**Advertencia sobre la fuente secundaria E46-002/003:** el estado de proyectos proviene de un agregador que enlaza al expediente del Congreso; el portal legislativo primario no mostró una ficha de estado legible en esta consulta. Por tanto la clasificación es **provisional**, y no se afirma que los proyectos nunca hayan avanzado.

## 2. Control de errores de clasificación

- Buscar únicamente títulos que contengan «Código Procesal Civil» **no es suficiente**: una ley con otro título puede contener disposiciones finales modificatorias, transitorias o derogatorias.
- Citas a artículos del CPC en sentencias, resoluciones administrativas o notas periodísticas **no son modificaciones del Decreto Legislativo 768**.
- Proyectos, dictámenes y propuestas de reforma judicial **no modifican por sí mismos el corpus** de la aplicación. Exigir número de norma promulgada, publicación oficial, artículos modificados y fecha de entrada en vigor.
- El registro del Congreso utilizado **no cubre agosto–noviembre de 2026**; queda un hueco temporal explícito. La búsqueda web general practicada en esta etapa **no sustituye** una comprobación normativa artículo por artículo ni una revisión completa del diario oficial.
- No agregar el propuesto art.475-A a las 862 entradas de la app hasta acreditar norma promulgada en vigor.

## 3. Reconciliación con trabajo anterior y umbral de cierre

Se conserva lo validado en etapas 38–45:
- **862/862** artículos con **algún** cotejo documental (100% de cobertura de primera pasada; no implica lectura literal íntegra de los 862 artículos).
- Matriz de **862 filas x 15 columnas**; todos mantienen `vigencia_2026_11_10=NO_CERTIFICADA`.
- **JUR-017** (648, referencia STC) y **JUR-040** (688, «acción cambiaria») aprobados y fusionados en `main` mediante PR #55.
- **JUR-016** (648 «embargado» vs «embargo»), **JUR-031** (408 duplicación y técnica legislativa), **JUR-037** (391 «hubiera tenido tuvo»), y concordancias especiales de **719** y **523-A**, permanecen reservas jurídicamente relevantes; no confundir diferencias editoriales con reformas.

**Control C01–C08 sin avances ficticios:** C01, C02, C05 y C06 completos (**4/8 = 50% de hitos verificables**); C03 y C04 abiertos; C07 parcialmente probado con CI, sin cierre manual específico; C08 no ejecutado y requiere autorización de publicación. Se mantiene retirada la estimación anterior del 60% por falta de denominador comprobable. La evidencia E46-001 demuestra por qué C03 aún no puede cerrarse.

## 4. Acciones mínimas que realmente faltan para cierre jurídico

1. Cubrir con **fuentes primarias** el hueco 27/07/2026–10/11/2026 del registro congresal, más el resto del período 08/06/2025–26/07/2026 en cuanto a reformas indirectas; cotejar publicación, fe de erratas y vigencia diferida de los actos identificados. Obtener una consulta oficial fechada de SPIJ o equivalente.
2. Resolver/documentar con revisión jurídica competente las reservas de **648(6), 408 y 391(4)**, así como la aplicabilidad de la norma especial en 719/523-A; no cambiar literalmente el código sin autorización individual.
3. Regenerar/validar pruebas de integridad textual y búsqueda, y comprobar en dispositivo los artículos 648 y 688 ya corregidos; integrar evidencia de revisión de la versión candidata.
4. Emitir acta de cierre con limitaciones explícitas y revisión final del propietario antes de preparar Google Play; la **publicación queda fuera del alcance autorizado de esta etapa**.

**Política de cambios:** PR #54 sigue documental y en borrador. **No se ha modificado `strings.xml`, `articles.json`, índice, fechas, versión de Android, APK/AAB ni publicado en Google Play** en esta etapa.
