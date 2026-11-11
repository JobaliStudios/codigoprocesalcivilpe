# Auditoría CPC PE — Etapa 38: cierre de primera cobertura documental de 862 artículos (10/11/2026)

## Resultado y naturaleza de la revisión

Se concluyó la **primera pasada de cotejos documentales dirigidos** para los **51 registros restantes**. La matriz de producción abarca 862 artículos/identificadores y ahora **862/862 tienen algún cotejo nuevo o corroboración registrada** en `cotejo_2026_11_10`. **Importante: algunos son cotejos dirigidos de pasajes, NO cotejos literales exhaustivos ni certificaciones de vigencia.** Todos los 862 mantienen `vigencia_2026_11_10=NO_CERTIFICADA`.

Fuente principal: [TUO del Código Procesal Civil, Editora Perú, actualización 07/06/2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), sobre todo pp.84–90; los sufijos alfabéticos se revisaron en su ubicación original (artículos 119-A, 523-A, 565-A). Para comprobar visualmente se inspeccionaron capturas de PDF correspondientes a páginas índices 84, 85, 86, 87, 88 y 89; **la captura del índice 89 devolvió blanco**, por lo que se usó la capa textual del PDF. El cotejo de fecha posterior a junio 2025 **sigue sin realizarse exhaustivamente**.

Los **51 identificadores nuevos** abarcan: **119-A, 523-A, 565-A**, y **48 artículos del tramo 786–846** cuya matriz seguía marcada `SIN_COTEJO_INTEGRAL_NUEVO`.

### Clasificación de esta etapa

- **37** cotejos documentales dirigidos ordinarios, sin divergencia relevante advertida en los pasajes contrastados.
- **14** cotejos dirigidos con control especial: **119-A, 523-A, 565-A, 786, 788, 796, 805, 815, 828, 830, 831, 842, 843 y 845**.
- **0** pendientes de *primera pasada documental* tras esta etapa.
- **862** pendientes de certificación jurídica integral de vigencia a **10/11/2026**.

## Nuevas alertas y controles

| Artículo | Resultado | Decisión |
|---|---|---|
| **119-A** | Derecho a ajustes razonables y de procedimiento coincide con TUO p.17; DL1384. | Control de accesibilidad; sumilla de app lleva asterisco editorial |
| **523-A** | **JUR-042.** Los tres párrafos sobre indemnización justipreciada, contracautela y contradicción corresponden a TUO 2025 pp.57–58; incorporación por Ley27117. | **Alto riesgo de aplicar indiscriminadamente reglas del CPC sin estudiar DL1192**. No modificar literal sin decisión jurídica |
| **565-A** | Requisito de hallarse al día en pensión para reducción/variación/prorrateo/exoneración corresponde a Ley29486, TUO p.62. | Control de concordancia de alimentos, jurisprudencia no verificada |
| **786** | App y TUO p.84 mantienen referencia a representantes de `incapaces`. | Revisar concordancia con DL1384 sin alterar la norma |
| **788** | Reglas de declaración testimonial: entre tres y cinco testigos mayores de 25 años (p.85). | Control positivo |
| **796** | Cinco requisitos para patrimonio familiar según TUO p.85. | Control positivo |
| **805** | Cinco días para contradicción en ofrecimiento judicial de pago (p.86). | Control positivo |
| **815** | **JUR-043.** En la app y TUO p.87 se lee `los costas y costos`. | Anomalía gramatical **compartida con fuente oficial**; no corregir sin revisar el texto primario y fe de erratas |
| **828** | `Abogado` app frente a `abogado` TUO p.88. | Diferencia de mayúscula, pendiente de aprobación |
| **830** | Se conserva mención a `incapaces sin representante` en solicitud de sucesión intestada (p.89). | Concordancia terminológica con DL1384 pendiente |
| **831** | Cinco requisitos de sucesión intestada y párrafo sobre unión de hecho de Ley30007, TUO p.89. | Control de documentación |
| **842–843** | Referencias a `659A`/ `659 A` y `45B`/`45 B`, respectivamente, en Código Civil (p.90). | Diferencias de espaciamiento jurídico, no de sentido |
| **845** | Ajustes procesales para expresión de voluntad de persona con discapacidad, DL1384 (p.90). | Control positivo |

## Reforma de sucesión intestada en junio de 2025: comprobación, SIN doble conteo

La [Ley 32377 publicada el 07/06/2025](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7) modificó el **artículo 834** del CPC y derogó el **835**; también modificó el 759. La aplicación contiene el texto de la reforma de 834 y el estado de derogación de 835; esos artículos **ya tenían cotejo documental en la matriz y NO se sumaron a los 51 nuevos**. El art. 830 mantiene terminología histórica sobre incapacidad, de modo que no debe confundirse la actualización de 834 con certificación de todo el capítulo sucesorio.

## Reparación de integridad previa — JUR-DATA-001

Antes de esta etapa, se detectaron **446 filas con columnas CSV mal escapadas** al leer el archivo acumulado con un analizador estándar. La normalización realizada en commit `174a3fd` reconstruyó sumillas desde la producción, agrupó observaciones quebradas por comas y reescribió escapado estándar. Se comprobó que las **862 filas tienen 15 columnas**, que ninguna perdió vigencia `NO_CERTIFICADA` y que los 51 pendientes se conservaban antes de actualizar.

Esta reparación es **sintáctica**, no certifica que las 862 descripciones estén perfectas semánticamente; la limpieza editorial de algunos signos de puntuación continúa sujeta a controles posteriores. La matriz actual tras esta etapa volvió a validarse por analizador CSV y conserva **862 filas de 15 columnas**.

## Qué está terminado y qué NO

**Terminada:** primera pasada documental con alguna revisión dirigida/corroboración para **862 de 862 artículos (100% de cobertura inicial de registro)**.

**NO terminado:** cotejo íntegro literal de todos los artículos, auditoría de la **vigencia jurídica al 10/11/2026**, análisis de leyes y efectos posteriores al 07/06/2025, evaluación de jurisprudencia relevante y aprobación de rectificaciones. Por tanto **0 artículos se certifican jurídicamente** y no se debe crear un AAB ni publicar versión nueva todavía.

**Prioridades abiertas:**
- **JUR-016/017** art.648: `embargado` / `embargo` y cita incorrecta al TC.
- **JUR-031** art.408: interpretación de modificación del DL1384.
- **JUR-037** art.391: `hubiera tenido tuvo` compartido con reproducción legislativa.
- **JUR-040** art.688: `cambiaría` vs `cambiaria`.
- **JUR-041** art.719: arbitraje, Ley26572 vs DL1071.
- **JUR-042** art.523-A: régimen expropiatorio DL1192.
- **JUR-043** art.815: `los costas` compartido con TUO.
- **JUR-DATA-001**: validación semántica de comentarios tras reparación CSV, aunque la incidencia sintáctica fue subsanada.

**Estado de seguridad:** PR #54 permanece abierto/borrador sin fusionar; cambios exclusivamente en `docs/audits`. Ninguna alteración de producción `articles.json`, `strings.xml`, versión, firma, APK/AAB ni consola Google Play.

## Próxima fase

Dejar los lotes por numeración y pasar a **auditoría transversal de vigencia 2025–2026**, identificación y clasificación de todas las leyes modificatorias posteriores al corte de TUO 07/06/2025, controles integrales de artículos de mayor riesgo, verificación de jurisprudencia y preparación de propuestas de corrección sujetas a autorización. Reevaluar el indicador administrativo de Fase 1, históricamente estimado al 60%, solo tras medir tareas pendientes de certificación.
