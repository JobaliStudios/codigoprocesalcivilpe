# Fase 5 / Misión 2 — Lote 3: 25 preguntas (95 → 120)

**Preparado:** 12 de noviembre de 2026.  
**Estado:** rama de revisión, CI y aceptación manual pendientes. **No fusionado, no publicado en Google Play.**  
**Alcance:** 25 preguntas nuevas, una por artículo no utilizado anteriormente; 120 preguntas en total y 119 artículos distintos.

## Fuente jurídica y límites

- **Referencia normativa primaria:** [TUO del Código Procesal Civil — El Peruano, edición compilada hasta el 7 de junio de 2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), consultado el 12/11/2026. Las páginas de la tabla son páginas del PDF contando la portada como primera.
- **Comparación interna:** `app/src/main/assets/articles.json`, contrastando el artículo y la regla que se convierte en respuesta correcta. El cotejo de respuesta se ciñe a la disposición citada, sin generalizar excepciones o regímenes ajenos.
- **Limitación:** el PDF oficial tiene fecha de actualización 07/06/2025. El cotejo puntual y las búsquedas complementarias no son un rastreo exhaustivo de leyes, disposiciones especiales, efectos temporales o modificaciones posteriores hasta 12/11/2026. Este lote **no certifica vigencia jurídica total a noviembre de 2026** y no sustituye la auditoría jurídica anterior al lanzamiento.
- **Diferencia editorial no sustantiva en este lote:** el título del art. 420 figura abreviado como «Literalidad de la multa» en la app, mientras la sumilla del TUO es «Literalidad y destino de la multa». La pregunta solo trata la distribución en partes iguales si no se precisa proporción; no se edita el corpus en este PR. Evaluar la sumilla separadamente.
- **Hallazgo externo previo sin cierre:** discrepancia «impugnable/inimpugnable» del art. 551. No se toca ese artículo ni se declara resuelto.

## Registro editorial por pregunta

Las alternativas correctas son una única opción dentro de cuatro, y el índice de respuesta comienza en cero.

| Nº banco | Sección CPC | Artículo | Respuesta correcta | Índice | Página PDF |
|---:|:---:|---:|---|---:|---:|
| 96 | I | 1 | El Poder Judicial | 0 | 5 |
| 97 | I | 2 | El derecho de contradicción | 1 | 5 |
| 98 | I | 7 | Comisionar a otro la realización de actuaciones judiciales fuera de su ámbito territorial | 2 | 5 |
| 99 | II | 54 | Un secretario de juzgado | 0 | 9 |
| 100 | II | 64 | A la Constitución, la ley o el respectivo estatuto | 3 | 10 |
| 101 | II | 70 | Tener capacidad para comparecer por sí en un proceso | 1 | 11 |
| 102 | II | 94 | No favorecen ni perjudican a los demás, sin afectar la unidad del proceso | 2 | 14 |
| 103 | III | 129 | La constitución, modificación o extinción de derechos y cargas procesales | 1 | 18 |
| 104 | III | 142 | Cuando se trate de actuaciones urgentes cuya demora pueda perjudicar a una parte, entre los supuestos legales | 3 | 19 |
| 105 | III | 155 | Poner en conocimiento de los interesados el contenido de las resoluciones judiciales | 0 | 20 |
| 106 | III | 202 | El juez | 2 | 25 |
| 107 | III | 358 | El agravio y el vicio o error que motiva la impugnación | 1 | 39 |
| 108 | III | 420 | Se entiende impuesta en partes iguales | 3 | 46 |
| 109 | IV | 431 | Por cédula entregada en su domicilio real | 1 | 48 |
| 110 | IV | 447 | En cuaderno separado, sin suspender la tramitación del proceso principal | 0 | 50 |
| 111 | IV | 458 | Se le declara rebelde | 2 | 51 |
| 112 | IV | 468 | Tres días | 1 | 52 |
| 113 | V | 481 | Como parte, sin emitir dictamen | 2 | 53 |
| 114 | V | 548 | El artículo 476 del Código Procesal Civil | 0 | 61 |
| 115 | V | 590 | Cuando la restitución haya sido demandada acumulativamente, con la salvedad legal correspondiente | 3 | 65 |
| 116 | V | 612 | Provisoria, instrumental y variable | 1 | 67 |
| 117 | V | 689 | Que la obligación sea líquida o liquidable por operación aritmética | 2 | 75 |
| 118 | VI | 760 | La correspondiente a la audiencia de pruebas | 0 | 82 |
| 119 | VI | 762 | Por oficio o partes firmados por el juez, según corresponda | 1 | 83 |
| 120 | VI | 763 | Para individualizar y establecer la existencia de bienes que se pretende asegurar | 3 | 83 |

## Distribución y garantías

- Sección I: **3**; II: **4**; III: **6**; IV: **4**; V: **5**; VI: **3**. Total: **25**.
- Las primeras **95 preguntas** conservan exactamente `questionText`, `options`, `correctAnswerIndex`, `relatedArticle` y su orden; se congelan en `tools/quiz_bank_legacy_95.json`. También se preservan los baselines originales de **31** y **63** preguntas.
- Los IDs locales SHA-256 que permiten revisar errores previos no cambian; las 25 entradas nuevas tienen IDs nuevos. No hay backend, sincronización ni solicitudes de red.
- El validador controla esquema, opciones, duplicados, referencias a artículos y prefijos anteriores; las pruebas Android verifican 120 preguntas, 119 artículos distintos, cobertura de secciones y recuperación de un error del tercer lote.
- Se mantienen `articles.json`, navegación, preferencias, anuncios, UMP, `versionName 1.08` y `versionCode 13` sin modificaciones.

## Puerta de aceptación

- [ ] Python: `python3 tools/validate_quiz_bank.py` y `python3 -m unittest discover -s tools -p 'test_validate_quiz_bank.py' -v`.
- [ ] Android: `QuizExpansionCoverageTest`, `QuestionBankIntegrityTest`, demás tests unitarios, Lint, ensamblado debug y release/R8 en verde.
- [ ] Revisar manualmente las preguntas 96 a 120 en modo Todos y por Sección; verificar respuesta, vínculo al artículo, repaso de errores, persistencia al cerrar/reabrir y funcionamiento offline.
- [ ] Cerrar, con revisión jurídica independiente, incertidumbres de vigencia posteriores a 07/06/2025 antes de publicar como contenido actualizado.
- [ ] Solo fusionar con CI verde y aprobación del propietario. **No publicar en Google Play** en esta misión.

## Resultado esperado

Se alcanzará la **meta mínima educativa de 120 preguntas** (119 artículos distintos) tras la aprobación y fusión. La meta de 200 preguntas sigue siendo una posible ampliación, no un requisito para lanzar prematuramente.
