# Fase 5 / Misión 2 — Lote 2: 32 preguntas jurídicas adicionales

**Fecha de trabajo:** 12 de noviembre de 2026.  
**Estado:** PR pendiente de integración y pruebas de aceptación. **Sin publicación Google Play**.  
**Banco:** 63 preguntas previas + **32 nuevas** = **95 preguntas**, con **94 artículos diferentes** en el cuestionario.  
**Preservación:** `tools/quiz_bank_legacy_63.json` congela las 63 preguntas anteriores, y la línea base de 31 preguntas originales continúa protegida.

## Fuentes y límites del cotejo

- **Fuente normativa primaria:** [El Peruano — TUO del Código Procesal Civil (edición compilada hasta el 7/06/2025)](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007). Se compararon los conceptos de los artículos escogidos con el texto de la edición oficial y los correspondientes artículos en `app/src/main/assets/articles.json`.
- **Norma específica sobre representación en alimentos:** [Ley 32266, publicación 22/03/2025 en El Peruano](https://spij.minjus.gob.pe/Normas/textos/220325T.pdf), incorporó el inciso 9 del art. 561; incorporado en la edición del TUO citada.
- **Limitación temporal:** el PDF oficial disponible lleva portada **07/06/2025**. La búsqueda complementaria en normas publicadas y compilaciones de 2026 no equivale a un rastreo normativo exhaustivo hasta **12/11/2026**. Es necesaria la comprobación final de todas las modificatorias posteriores al PDF antes de publicar estos ejercicios como contenidos actualizados.
- **Hallazgo jurídico independiente:** al contrastar el art. 551, la edición del PDF de El Peruano muestra «Esta resolución es impugnable», mientras `articles.json` y la compilación de LP Derecho 2026 dicen «inimpugnable». La pregunta 87 solo trata del plazo de **tres días**, coincidente en ambos textos. **No se modifica ningún artículo legal ni se resuelve aquí esa discrepancia**; elevarla a una revisión jurídica separada antes de publicar.

## Tabla editorial por pregunta

Las opciones correctas se redactaron como preguntas educativas con una alternativa escogida. Índice de respuesta Java/JSON desde cero.

| Nº banco | Artículo | Área | Respuesta correcta | Referencia de cotejo |
|---:|---:|---|---|---|
| 64 | 51 | II — Sujetos del proceso | Adaptar la demanda a la vía procedimental apropiada | TUO CPC, art. 51 |
| 65 | 53 | II — Sujetos del proceso | Una multa compulsiva y progresiva | TUO CPC, art. 53 |
| 66 | 55 | II — Sujetos del proceso | El perito | TUO CPC, art. 55 |
| 67 | 59 | II — Sujetos del proceso | Al Poder Judicial, con los privilegios expresamente previstos en el Código | TUO CPC, art. 59 |
| 68 | 92 | II — Sujetos del proceso | Cuando dos o más personas litigan conjuntamente como demandantes o demandados en los supuestos previstos | TUO CPC, art. 92 |
| 69 | 93 | II — Sujetos del proceso | Que comparezcan o sean emplazados todos los litisconsortes afectados uniformemente | TUO CPC, art. 93 |
| 70 | 123 | III — Actividad procesal | Cuando ya no procedan otros medios impugnatorios distintos de los resueltos, entre otros supuestos legales | TUO CPC, art. 123 |
| 71 | 125 | III — Actividad procesal | De manera correlativa en el día de su expedición | TUO CPC, art. 125 |
| 72 | 128 | III — Actividad procesal | La inadmisibilidad corresponde a defectos formales y la improcedencia a defectos de fondo | TUO CPC, art. 128 |
| 73 | 191 | III — Actividad procesal | Sí, los medios y sus sucedáneos pueden ser idóneos aunque no estén tipificados | TUO CPC, art. 191 |
| 74 | 194 | III — Actividad procesal | Cuando sean insuficientes los medios ofrecidos y se cumplan los límites y garantías del artículo | TUO CPC, art. 194 |
| 75 | 199 | III — Actividad procesal | Una prueba obtenida mediante intimidación o soborno | TUO CPC, art. 199 |
| 76 | 200 | III — Actividad procesal | No se tienen por verdaderos y su demanda se declara infundada | TUO CPC, art. 200 |
| 77 | 355 | III — Actividad procesal | Solicitar la anulación o revocación total o parcial de un acto procesal presuntamente viciado o erróneo | TUO CPC, art. 355 |
| 78 | 356 | III — Actividad procesal | Contra actos procesales no contenidos en resoluciones, dentro de los casos previstos | TUO CPC, art. 356 |
| 79 | 357 | III — Actividad procesal | Ante el órgano jurisdiccional que cometió el vicio o error, salvo disposición distinta | TUO CPC, art. 357 |
| 80 | 428 | IV — Postulación | Antes de que la demanda sea notificada | TUO CPC, art. 428 |
| 81 | 430 | IV — Postulación | Da por ofrecidos los medios probatorios y corre traslado al demandado | TUO CPC, art. 430 |
| 82 | 442 | IV — Postulación | De manera precisa, pronunciándose sobre cada uno de los hechos | TUO CPC, art. 442 |
| 83 | 443 | IV — Postulación | El plazo es el mismo y simultáneo | TUO CPC, art. 443 |
| 84 | 444 | IV — Postulación | Los previstos para la demanda en el artículo 425, en lo que corresponda | TUO CPC, art. 444 |
| 85 | 445 | IV — Postulación | En el mismo escrito en que se contesta la demanda, con los requisitos aplicables | TUO CPC, art. 445 |
| 86 | 448 | IV — Postulación | Los documentales ofrecidos en el escrito de excepción o absolución | TUO CPC, art. 448 |
| 87 | 551 | V — Procesos contenciosos | Tres días | TUO CPC, art. 551 |
| 88 | 552 | V — Procesos contenciosos | Al contestarse la demanda | TUO CPC, art. 552 |
| 89 | 554 | V — Procesos contenciosos | Cinco días | TUO CPC, art. 554 |
| 90 | 559 | V — Procesos contenciosos | La reconvención | TUO CPC, art. 559 |
| 91 | 560 | V — Procesos contenciosos | El demandante | TUO CPC, art. 560 |
| 92 | 561 | V — Procesos contenciosos | El abuelo, el tío o el hermano mayor que tiene la responsabilidad de cuidarlo | TUO CPC, art. 561 |
| 93 | 562 | V — Procesos contenciosos | Hasta veinte Unidades de Referencia Procesal de la pensión demandada | TUO CPC, art. 562 |
| 94 | 754 | VI — No contenciosos | Dentro de quince días, salvo la excepción prevista en el Código | TUO CPC, art. 754 |
| 95 | 761 | VI — No contenciosos | La reconvención | TUO CPC, art. 761 |

## Compatibilidad y controles

- No se modifica ninguna de las 63 preguntas anteriores, sus respuestas, opciones ni referencias. El nuevo validador obliga a que las **63 primeras entradas coincidan exactamente** con `tools/quiz_bank_legacy_63.json`, además del control anterior de 31 entradas.
- Los IDs del repaso de errores de preguntas previas permanecen estables; las nuevas preguntas reciben sus propios IDs.
- El banco mantiene `questionText`, `options`, `correctAnswerIndex`, `relatedArticle`. Sin backend, red, dependencias nuevas ni cambios a preferencias.
- Se comprueban 95 preguntas, 94 referencias, cobertura de todas las secciones del CPC y reapertura de una pregunta nueva desde el repaso de errores.
- Sin cambios a `articles.json`, textos normativos, navegación, UMP, anuncios, `versionName 1.08` ni `versionCode 13`.

## Aceptación pendiente

- [ ] Validador JSON/CPC + pruebas unitarias + Lint + debug + release/R8 verdes en GitHub Actions.
- [ ] Comprobar de manera independiente los hallazgos de vigencia normativa posteriores a 07/06/2025 y cerrar o documentar los posibles conflictos, incluido el art. 551.
- [ ] Probar en teléfono las sesiones generales y por sección, preguntas 64 a 95, navegación al artículo y conservación del historial de errores.
- [ ] Revisar y fusionar el PR por decisión del propietario.
- [ ] **No generar actualización para Google Play**: la versión 1.08 continúa sin lanzamiento.

## Meta educativa

**95 de 120** preguntas de la meta mínima (**79,2 %**); quedan **25** para llegar a 120 o **105** para la meta ampliada de 200. La Misión 2 sigue abierta; la cobertura no se completará sacrificando la corrección jurídica.
