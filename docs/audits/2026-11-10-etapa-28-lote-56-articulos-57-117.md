# Auditoría CPC PE — Etapa 28: lote ampliado de 56 artículos, comparecencia y sujetos procesales (10/11/2026)

## Alcance y metodología

Cotejo del texto de producción de `main:app/src/main/assets/articles.json` con el [TUO oficial del Código Procesal Civil de Editora Perú, edición ACTUALIZADA 07/06/2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), páginas impresas **10–17** (PDF índices 9–16). Se examinaron bloques completos mediante capa textual indexada y capturas visuales de control: **PDF índices 10, 13 y 15**; la captura del índice **12** devolvió página en blanco y se utilizó su capa textual. Esta es revisión histórica de contenido, **no certificación de vigencia a 10/11/2026**. No se validó exhaustivamente el corpus con leyes posteriores a junio de 2025.

Se auditaron **56 registros nuevos**, excluyendo los artículos **56, 61, 62, 67, 91, 96 y 118**, que no estaban en la cola seleccionada (ya contaban con otro estado documental previo); no se contabilizan dos veces.

**Artículos tratados:** 57, 58, 59, 60, 63, 64, 65, 66, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 92, 93, 94, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116 y 117.

### Resultado

- **47** registros sin diferencia sustantiva advertida en el contraste dirigido del cuerpo normativo con la edición 2025.
- **9** registros con variantes editoriales o necesidad de precisión histórica, también sin cambio sustantivo identificado: **65, 76, 79, 80, 100, 109, 110, 111 y 112**.
- Se preservan reglas de representación y designación judicial, apoyo para personas con capacidad de ejercicio restringida (art. 66), acumulación (83–90), litisconsorcio (92–95), intervención (97–108), deberes/multas (109–112) y Ministerio Público (113–117). No se infiere vigencia actual de la ausencia de divergencias frente al documento de 2025.

## Hallazgos editoriales e históricos

| Artículo | Situación | Decisión |
|---|---|---|
| **65 — Representación del patrimonio autónomo** | **JUR-034.** La nota histórica de la app presenta DL 861 y Ley 26827 como modificaciones sucesivas; el TUO oficial (p.11) identifica **restitución** del artículo por el art. 2 de Ley 26827 (29/06/1997), tras la modificación de DL 861. El cuerpo de cuatro párrafos coincide. | Proponer nota histórica exacta tras cotejo con leyes originales; **SIN APROBAR** |
| **76 — Apoderado común** | `Abogados` app frente `abogados` TUO (p.12) | Solo variante de capitalización; mantener |
| **79 — Cese de representación** | `éste` frente `este` (p.12) | Tilde editorial; mantener |
| **80 — Representación por abogado** | `Abogado` frente `abogado` (p.12) | Capitalización; mantener |
| **100 — Intervención excluyente** | `Artículo` y `Sub Capítulo` en referencia app, TUO usa minúscula y composición moderna (p.15) | Formato y estilo; mantener |
| **109–111** | `Abogado(s)` en mayúscula en app frente minúscula en TUO (p.16) | Capitalización; mantener |
| **112 — Temeridad o mala fe** | **JUR-035.** La nota app dice `Artículo modificado por ... Ley 26635`; TUO (p.16) identifica que **numeral 7 fue incorporado** por art. 2 de Ley 26635 el 23/06/1996. Cuerpo y siete numerales coinciden. | Proponer nota histórica que delimite incorporación del numeral; **SIN APROBAR** |

## Controles de especial atención

- Art. **66**: cuatro supuestos completos y terminología `persona con capacidad de ejercicio restringida` cotejados con DL 1384 reflejado en TUO.
- Arts. **75–81**: facultades especiales, plazos de apoderado común, efectos de cese y procuración; mantener límites y excepciones, no reformular.
- Art. **82**: legitimación para intereses difusos, intervención de comunidades y rondas, consulta de sentencia desestimatoria y destino de indemnización.
- Arts. **85 y 86**: reglas de acumulación y competencia del juez de mayor grado; el consolidado de 2025 recoge Ley 30293.
- Art. **108**: cuatro supuestos y plazo treinta días; no interpretar automáticamente el texto como vigente al 2026 sin cotejo complementario.
- Art. **112**: siete supuestos, con numeral 7 incorporado históricamente.

## Estado cuantitativo

- Antes: **218/862** registros con cotejo nuevo documentado; **644** pendientes.
- Después: **274/862 (31,79%)** con cotejo nuevo documentado; **588** todavía sin él.
- Todos los registros conservan `vigencia_2026_11_10=NO_CERTIFICADA` (862/862).
- **Fase 1 estimada en 60%** permanece como indicador administrativo anterior, sin equivaler a cobertura jurídica certificada.
- No hay revisión exhaustiva de cambios normativos 08/06/2025–10/11/2026 en esta tanda.

## Continuación

Siguiente lote ampliado sugerido: pendientes desde **119 hasta 181**, priorizando resoluciones, nulidad, notificaciones, plazos y reglas de ejercicio de derechos. Mantener sin tocar **`articles.json`, `strings.xml`, fechas visibles, `versionCode`, `versionName`, APK/AAB y publicación Google Play**. PR #54 abierto y en borrador; todo cambio en `docs/audits` y propuestas solo con autorización del propietario.
